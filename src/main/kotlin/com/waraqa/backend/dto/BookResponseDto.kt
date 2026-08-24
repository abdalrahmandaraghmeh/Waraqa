package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class BookResponseDto(
    val id: Long,
    @JsonProperty("cover_image")
    val coverImage: String?,
    val title: String,
    @JsonProperty("publisher_name")
    val publisherName: String,
    val price: BigDecimal
)
