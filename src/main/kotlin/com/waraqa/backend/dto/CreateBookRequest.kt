package com.waraqa.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreateBookRequest(
    @field:NotBlank
    val title: String,

    @field:Positive
    val price: BigDecimal,

    val imagesUrl: List<String>
)