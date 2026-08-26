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
    val author: String = "",
    val price: BigDecimal,
    @Column("published_at")
    val publishedAt: LocalDateTime = LocalDateTime.now(),
    @Column("cover_image")
    val coverImage: String? = null,
    val imagesUrl: List<String> = emptyList(),
    val category: String = "general", // academic / general
    val type: String? = null, // book / novel
    val rating: Double = 0.0,
    @Column("publisher_id")
    val publisherId: Long,
    @Column("university_id")
    val universityId: Long? = null,
    @Column("faculty_id")
    val facultyId: Long? = null,
    @Column("major_id")
    val majorId: Long? = null
)
