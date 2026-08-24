package com.waraqa.backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("books")
data class Book(
    @Id
    val bookId: Long? = null,
    val title: String,
    val price: BigDecimal,
    val imagesUrl: List<String> = emptyList(),
    val userId: Long
)
