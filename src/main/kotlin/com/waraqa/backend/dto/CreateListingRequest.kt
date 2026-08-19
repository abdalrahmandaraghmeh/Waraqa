package com.waraqa.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreateListingRequest(
    @field:NotBlank(message = "العنوان مطلوب")
    val title: String,

    val description: String? = null,

    @field:NotNull(message = "السعر مطلوب")
    @field:Positive(message = "يجب أن يكون السعر أكبر من صفر")
    val price: BigDecimal,

    @field:NotBlank(message = "نوع الفئة مطلوب (academic أو general)")
    val category: String,

    val universityId: Long? = null,
    val facultyId: Long? = null,
    val majorId: Long? = null,

    val images: List<String>? = emptyList()
)