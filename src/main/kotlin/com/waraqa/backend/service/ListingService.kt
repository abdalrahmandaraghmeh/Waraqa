package com.waraqa.backend.service

import com.waraqa.backend.dto.CreateListingRequest
import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.exception.ForbiddenException
import com.waraqa.backend.exception.ValidationException
import com.waraqa.backend.repository.UserRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime

@Service
class ListingService(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val userRepository: UserRepository
) {

    fun createListing(request: CreateListingRequest): ListingResponseDto {
        // 1. Authenticate user
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw ForbiddenException("Unauthenticated user")
        val userEmail = authentication.name

        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { RuntimeException("User not found") }

        val publisherId = user.userId ?: throw RuntimeException("User ID is missing")

        // 2. Validate conditional requirements
        val errors = mutableMapOf<String, String>()

        // Image validation
        if (request.image.isNullOrBlank()) {
            errors["image"] = "Book photo is required"
        }

        // Title validation
        if (request.title.isNullOrBlank()) {
            errors["title"] = "Title is required"
        }

        // Listing Type validation
        if (request.listingType !in listOf("for_sale", "for_sale_and_exchange")) {
            errors["listing_type"] = "Listing type must be 'for_sale' or 'for_sale_and_exchange'"
        }

        // Category & Sub-branches validation
        when (request.category) {
            "academic" -> {
                if (request.universityId == null) {
                    errors["university_id"] = "University is required for academic listings"
                }
                if (request.facultyId == null) {
                    errors["faculty_id"] = "Faculty is required for academic listings"
                }
                if (request.majorId == null) {
                    errors["major_id"] = "Major is required for academic listings"
                }
            }
            "general" -> {
                if (request.subType !in listOf("book", "novel")) {
                    errors["sub_type"] = "General listing sub_type must be 'book' or 'novel'"
                }
            }
            else -> {
                errors["category"] = "Category must be 'academic' or 'general'"
            }
        }

        // Price validation
        if (request.price == null || request.price <= BigDecimal.ZERO) {
            errors["price"] = "Price is required and must be greater than zero"
        }

        // Exchange_for validation conditional on listing_type
        if (request.listingType == "for_sale_and_exchange" && request.exchangeFor.isNullOrBlank()) {
            errors["exchange_for"] = "Exchange details are required when 'For Sale & Exchange' is selected"
        }

        // Condition validation
        if (request.condition !in listOf("new", "good", "fair")) {
            errors["condition"] = "Condition must be 'new', 'good', or 'fair'"
        }

        // Description validation
        if (request.description.isNullOrBlank()) {
            errors["description"] = "Description is required"
        }

        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }

        // 3. Save to database
        val now = LocalDateTime.now()
        val sql = """
            INSERT INTO books (
                title, author, description, price, listing_type, exchange_for,
                condition, category, sub_type, cover_image, images_url, publisher_id,
                university_id, faculty_id, major_id, published_at
            ) VALUES (
                :title, :author, :description, :price, :listingType, :exchangeFor,
                :condition, :category, :subType, :coverImage, :imagesUrl, :publisherId,
                :universityId, :facultyId, :majorId, :publishedAt
            )
        """.trimIndent()

        val allImages = request.imagesUrl?.takeIf { it.isNotEmpty() }
            ?: listOfNotNull(request.image?.trim()).filter { it.isNotBlank() }

        val params = MapSqlParameterSource()
            .addValue("title", request.title!!.trim())
            .addValue("author", request.author?.trim()?.takeIf { it.isNotBlank() })
            .addValue("description", request.description!!.trim())
            .addValue("price", request.price!!)
            .addValue("listingType", request.listingType)
            .addValue("exchangeFor", if (request.listingType == "for_sale_and_exchange") request.exchangeFor?.trim() else null)
            .addValue("condition", request.condition)
            .addValue("category", request.category)
            .addValue("subType", if (request.category == "general") request.subType else null)
            .addValue("coverImage", request.image!!.trim())
            .addValue("imagesUrl", allImages.toTypedArray())
            .addValue("publisherId", publisherId)
            .addValue("universityId", if (request.category == "academic") request.universityId else null)
            .addValue("facultyId", if (request.category == "academic") request.facultyId else null)
            .addValue("majorId", if (request.category == "academic") request.majorId else null)
            .addValue("publishedAt", Timestamp.valueOf(now))

        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(sql, params, keyHolder, arrayOf("id"))
        val generatedId = keyHolder.key?.toLong() ?: throw RuntimeException("Failed to retrieve listing ID")

        return ListingResponseDto(
            id = generatedId,
            title = request.title.trim(),
            author = request.author?.trim()?.takeIf { it.isNotBlank() },
            description = request.description.trim(),
            price = request.price!!,
            listingType = request.listingType!!,
            exchangeFor = if (request.listingType == "for_sale_and_exchange") request.exchangeFor?.trim() else null,
            condition = request.condition!!,
            category = request.category!!,
            subType = if (request.category == "general") request.subType else null,
            image = request.image.trim(),
            publisherId = publisherId,
            universityId = if (request.category == "academic") request.universityId else null,
            facultyId = if (request.category == "academic") request.facultyId else null,
            majorId = if (request.category == "academic") request.majorId else null,
            createdAt = now
        )
    }
}