package com.waraqa.backend.service

import com.waraqa.backend.dto.CreateListingRequest
import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.exception.ForbiddenException
import com.waraqa.backend.exception.ValidationException
import com.waraqa.backend.model.Book
import com.waraqa.backend.model.Listing
import com.waraqa.backend.repository.BookRepository
import com.waraqa.backend.repository.ListingRepository
import com.waraqa.backend.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class ListingService(
    private val listingRepository: ListingRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
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
            condition = savedListing.condition,
            category = book.category,
            subType = request.subType, // يمكن إضافتها لجدول الكتب لاحقاً إذا لزم الأمر
            image = savedListing.coverImage!!,
            publisherId = savedListing.publisherId,
            universityId = savedListing.universityId,
            facultyId = savedListing.facultyId,
            majorId = savedListing.majorId,
            createdAt = savedListing.publishedAt
        )
    }
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
        val listings = listingRepository.findListingsWithFilters(
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

        return listings.map { listing ->
            val book = bookRepository.findById(listing.bookId).orElse(null)
            ListingResponseDto(
                id = listing.id ?: 0L,
                title = book?.title ?: "",
                author = book?.author,
                description = listing.description,
                price = listing.price,
                listingType = listing.listingType!!,
                exchangeFor = listing.exchangeFor,
                condition = listing.condition,
                category = book?.category ?: "general",
                subType = null,
                image = listing.coverImage ?: "",
                publisherId = listing.publisherId,
                universityId = listing.universityId,
                facultyId = listing.facultyId,
                majorId = listing.majorId,
                createdAt = listing.publishedAt
            )
        }
    }
}