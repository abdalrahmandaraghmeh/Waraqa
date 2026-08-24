package com.waraqa.backend.dto

import java.math.BigDecimal

data class UpdateBookRequest(
    val title: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,
    val category: String? = null,
    val universityId: Long? = null,
    val facultyId: Long? = null,
    val majorId: Long? = null
)