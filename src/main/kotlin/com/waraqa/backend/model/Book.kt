package com.waraqa.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("books")
data class Book(
    @Id
    @Column("book_id")
    val bookId: Long? = null,
    val title: String,
    val price: BigDecimal,
    @Column("images_url")
    val imagesUrl: List<String>,
    @Column("user_id")
    val userId: Long
)