package com.waraqa.backend.service

import com.waraqa.backend.dto.*
import com.waraqa.backend.exception.ForbiddenException
import com.waraqa.backend.exception.NotFoundException
import com.waraqa.backend.exception.ValidationException
import com.waraqa.backend.model.Book
import com.waraqa.backend.model.Listing
import com.waraqa.backend.repository.BookRepository
import com.waraqa.backend.repository.ListingRepository
import com.waraqa.backend.repository.UserRepository
import com.waraqa.backend.security.SecurityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class ListingService(
    private val listingRepository: ListingRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val securityUtils: SecurityUtils
) {

    fun createListing(request: CreateListingRequest): ListingResponseDto {
        // 1. التحقق من هوية المستخدم
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw ForbiddenException("Unauthenticated user")
        val userEmail = authentication.name

        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { RuntimeException("User not found") }

        val publisherId = user.userId ?: throw RuntimeException("User ID is missing")

        // 2. التحقق من صحة المدخلات (Validation) - أخذناها كما هي من ملفك لأنها ممتازة
        val errors = mutableMapOf<String, String>()

        if (request.image.isNullOrBlank()) errors["image"] = "Book photo is required"
        if (request.title.isNullOrBlank()) errors["title"] = "Title is required"
        if (request.listingType !in listOf("for_sale", "for_sale_and_exchange")) {
            errors["listing_type"] = "Listing type must be 'for_sale' or 'for_sale_and_exchange'"
        }

        when (request.category) {
            "academic" -> {
                if (request.universityId == null) errors["university_id"] = "University is required for academic listings"
                if (request.facultyId == null) errors["faculty_id"] = "Faculty is required for academic listings"
                if (request.majorId == null) errors["major_id"] = "Major is required for academic listings"
            }
            "general" -> {
                if (request.subType !in listOf("book", "novel")) {
                    errors["sub_type"] = "General listing sub_type must be 'book' or 'novel'"
                }
            }
            else -> errors["category"] = "Category must be 'academic' or 'general'"
        }

        if (request.listingType == "for_sale" && (request.price == null || request.price <= BigDecimal.ZERO)) {
            errors["price"] = "Price is required and must be greater than zero"
        } else if (request.listingType == "for_sale_and_exchange" && request.price != null && request.price < BigDecimal.ZERO) {
            errors["price"] = "Price cannot be negative"
        }

        if (request.listingType == "for_sale_and_exchange" && request.exchangeFor.isNullOrBlank()) {
            errors["exchange_for"] = "Exchange details are required when 'For Sale & Exchange' is selected"
        }

        if (request.condition !in listOf("new", "good", "fair")) {
            errors["condition"] = "Condition must be 'new', 'good', or 'fair'"
        }

        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }

        // 3. المنطق الجديد: فصل الكتاب عن الإعلان

        // البحث عن الكتاب أو إنشاؤه
        val book = bookRepository.findByTitleAndAuthor(request.title!!.trim(), request.author?.trim() ?: "")
            .orElseGet {
                val newBook = Book(
                    title = request.title.trim(),
                    author = request.author?.trim() ?: "",
                    category = request.category!!,
                    isAcademic = (request.category == "academic")
                )
                bookRepository.save(newBook)
            }

        // تجهيز الصور
        val allImages = request.imagesUrl?.takeIf { it.isNotEmpty() }
            ?: listOfNotNull(request.image?.trim()).filter { it.isNotBlank() }

        // إنشاء الإعلان وربطه بالكتاب
        val listing = Listing(
            bookId = book.id!!,
            publisherId = publisherId,
            description = request.description?.trim() ?: "",
            price = request.price,
            listingType = request.listingType,
            exchangeFor = if (request.listingType == "for_sale_and_exchange") request.exchangeFor?.trim() else null,
            condition = request.condition!!,
            coverImage = request.image!!.trim(),
            imagesUrl = allImages,
            universityId = if (request.category == "academic") request.universityId else null,
            facultyId = if (request.category == "academic") request.facultyId else null,
            majorId = if (request.category == "academic") request.majorId else null
        )

        val savedListing = listingRepository.save(listing)

        // 4. إرجاع الـ DTO
        return ListingResponseDto(
            id = savedListing.id!!,
            title = book.title,
            author = book.author,
            description = savedListing.description,
            price = savedListing.price,
            listingType = savedListing.listingType!!,
            exchangeFor = savedListing.exchangeFor,
            isExchange = savedListing.listingType == "for_sale_and_exchange",
            condition = savedListing.condition,
            category = book.category,
            subType = request.subType, // يمكن إضافتها لجدول الكتب لاحقاً إذا لزم الأمر
            image = savedListing.coverImage!!,
            publisherId = savedListing.publisherId,
            universityId = savedListing.universityId,
            facultyId = savedListing.facultyId,
            majorId = savedListing.majorId,
            postedAt = savedListing.publishedAt,
            createdAt = savedListing.publishedAt
        )
    }

    /**
     * Public listings feed — only returns active listings.
     */
    fun getListings(
        search: String?,
        category: String?,
        universityId: Long?,
        facultyId: Long?,
        majorId: Long?,
        subType: String?,
        sort: String?,
        page: Int,
        limit: Int
    ): List<ListingResponseDto> {
        return listingRepository.findListingsWithFilters(
            search = search,
            category = category,
            universityId = universityId,
            facultyId = facultyId,
            majorId = majorId,
            subType = subType,
            sort = sort,
            offset = page * limit,
            limit = limit
        )
    }

    /**
     * Returns active listings for a specific publisher — used on public profile page.
     */
    fun getListingsByPublisher(publisherId: Long, page: Int, limit: Int): List<ListingResponseDto> {
        return listingRepository.findListingsByPublisherId(
            publisherId = publisherId,
            offset = page * limit,
            limit = limit
        )
    }

    // =========================================================================
    // Owner-only operations
    // =========================================================================

    /**
     * Returns the owner's listings filtered by status (for Active/Sold tabs).
     * Enforces ownership — only the listing owner can see their sold listings.
     */
    fun getOwnerListings(userId: Long, status: String, page: Int, limit: Int): List<ListingResponseDto> {
        verifyOwnership(userId)
        val validStatus = if (status in listOf("active", "sold")) status else "active"
        return listingRepository.findOwnerListingsByStatus(
            publisherId = userId,
            status = validStatus,
            offset = page * limit,
            limit = limit
        )
    }

    /**
     * Returns active/sold counts for the owner's listings (tab labels).
     */
    fun getListingCounts(userId: Long): ListingCountsResponse {
        verifyOwnership(userId)
        return listingRepository.countByPublisherAndStatus(userId)
    }

    /**
     * Updates a listing. Only the listing owner can edit.
     */
    fun updateListing(listingId: Long, request: UpdateListingRequest): ListingResponseDto {
        val listing = listingRepository.findById(listingId)
            .orElseThrow { NotFoundException("Listing not found") }

        val currentUserId = securityUtils.getCurrentUserId()
            ?: throw ForbiddenException("Authentication required")

        if (listing.publisherId != currentUserId) {
            throw ForbiddenException("You can only edit your own listings")
        }

        // Validate editable fields
        val errors = mutableMapOf<String, String>()

        if (request.listingType != null && request.listingType !in listOf("for_sale", "for_sale_and_exchange")) {
            errors["listing_type"] = "Listing type must be 'for_sale' or 'for_sale_and_exchange'"
        }
        if (request.condition != null && request.condition !in listOf("new", "good", "fair")) {
            errors["condition"] = "Condition must be 'new', 'good', or 'fair'"
        }

        val effectiveType = request.listingType ?: listing.listingType
        if (effectiveType == "for_sale") {
            val effectivePrice = request.price ?: listing.price
            if (effectivePrice == null || effectivePrice <= BigDecimal.ZERO) {
                errors["price"] = "Price is required and must be greater than zero"
            }
        }

        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }

        // Update the book title/author if provided
        if (request.title != null || request.author != null) {
            val book = bookRepository.findById(listing.bookId)
                .orElseThrow { NotFoundException("Associated book not found") }
            val updatedBook = book.copy(
                title = request.title?.trim() ?: book.title,
                author = request.author?.trim() ?: book.author
            )
            bookRepository.save(updatedBook)
        }

        // Update listing fields
        listingRepository.updateListingFields(
            listingId = listingId,
            description = request.description?.trim(),
            price = request.price,
            listingType = request.listingType,
            exchangeFor = request.exchangeFor?.trim(),
            condition = request.condition,
            coverImage = request.image?.trim(),
            imagesUrl = request.imagesUrl,
            universityId = request.universityId,
            facultyId = request.facultyId,
            majorId = request.majorId
        )

        // Return refreshed listing via a query
        val refreshed = listingRepository.findOwnerListingsByStatus(
            publisherId = currentUserId,
            status = listing.status,
            offset = 0,
            limit = 1
        )
        // If the listing was just updated, return it; fallback to a simple DTO
        return refreshed.firstOrNull { it.id == listingId }
            ?: throw NotFoundException("Listing not found after update")
    }

    /**
     * Marks a listing as sold. Only the listing owner can perform this action.
     * This is irreversible — only "sold" is accepted.
     */
    fun updateListingStatus(listingId: Long, request: ListingStatusRequest): ListingResponseDto {
        if (request.status != "sold") {
            throw ValidationException(mapOf("status" to "Only 'sold' status is supported"))
        }

        val listing = listingRepository.findById(listingId)
            .orElseThrow { NotFoundException("Listing not found") }

        val currentUserId = securityUtils.getCurrentUserId()
            ?: throw ForbiddenException("Authentication required")

        if (listing.publisherId != currentUserId) {
            throw ForbiddenException("You can only update your own listings")
        }

        if (listing.status == "sold") {
            throw ValidationException(mapOf("status" to "Listing is already marked as sold"))
        }

        listingRepository.updateStatus(listingId, "sold")

        // Return updated listing
        val updated = listingRepository.findOwnerListingsByStatus(
            publisherId = currentUserId,
            status = "sold",
            offset = 0,
            limit = 100
        )
        return updated.firstOrNull { it.id == listingId }
            ?: throw NotFoundException("Listing not found after status update")
    }

    /**
     * Deletes a listing. Only the listing owner can delete.
     */
    fun deleteListing(listingId: Long) {
        val listing = listingRepository.findById(listingId)
            .orElseThrow { NotFoundException("Listing not found") }

        val currentUserId = securityUtils.getCurrentUserId()
            ?: throw ForbiddenException("Authentication required")

        if (listing.publisherId != currentUserId) {
            throw ForbiddenException("You can only delete your own listings")
        }

        listingRepository.deleteById(listingId)
    }

    /**
     * Verifies that the currently authenticated user matches the given userId.
     */
    private fun verifyOwnership(userId: Long) {
        val currentUserId = securityUtils.getCurrentUserId()
            ?: throw ForbiddenException("Authentication required")
        if (currentUserId != userId) {
            throw ForbiddenException("Access denied")
        }
    }
}