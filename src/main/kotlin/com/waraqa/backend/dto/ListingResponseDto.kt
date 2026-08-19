package com.waraqa.backend.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class ListingResponseDto(
    val id: Long,
    val title: String,
    val description: String? = null,
    val price: BigDecimal,
    val category: String,
    val publisherId: Long,
    val universityId: Long? = null,
    val facultyId: Long? = null,
    val majorId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)