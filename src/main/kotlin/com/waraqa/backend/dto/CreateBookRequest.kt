package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateBookRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotBlank(message = "Author is required")
    val author: String,

    @field:NotNull(message = "Price is required")
    @field:Positive(message = "Price must be positive")
    val price: BigDecimal,

    @field:NotNull(message = "Published date is required")
    @JsonProperty("published_at")
    val publishedAt: LocalDateTime,

    @JsonProperty("cover_image")
    val coverImage: String? = null,

    @field:NotBlank(message = "Category is required")
    val category: String, // academic / general

    val type: String? = null, // book / novel

    @JsonProperty("university_id")
    val universityId: Long? = null,

    @JsonProperty("faculty_id")
    val facultyId: Long? = null,

    @JsonProperty("major_id")
    val majorId: Long? = null
)
