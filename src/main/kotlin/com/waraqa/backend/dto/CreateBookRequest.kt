package com.waraqa.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreateBookRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:Positive(message = "Price must be greater than zero")
    val price: BigDecimal,

    val imagesUrl: List<String> = emptyList(),

    val category: String? = "academic",

    val type: String? = "Used",

    val author: String? = null,

    val universityId: Long? = null,

    val facultyId: Long? = null,

    val majorId: Long? = null
)