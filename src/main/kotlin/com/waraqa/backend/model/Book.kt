package com.waraqa.backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("books")
data class Book(
    @Id
    val id: Long? = null,
    val title: String,
    val author: String,
    val price: BigDecimal,
    @Column("published_at")
    val publishedAt: LocalDateTime,
    @Column("cover_image")
    val coverImage: String? = null,
    @Column("user_id")
    val userId: Long
)
