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
    val description: String = "",
    val price: BigDecimal? = null,

    @Column("listing_type")
    val listingType: String = "for_sale",

    @Column("exchange_for")
    val exchangeFor: String? = null,
    val condition: String = "good",
    val category: String = "general",
    val type: String? = null,

    @Column("sub_type")
    val subType: String? = null,
    val edition: String? = null,

    @Column("cover_image")
    val coverImage: String? = null,

    @Column("images_url")
    val imagesUrl: List<String> = emptyList(),

    @Column("views_count")
    val viewsCount: Int = 0,

    @Column("saves_count")
    val savesCount: Int = 0,

    @Column("publisher_id")
    val publisherId: Long,

    @Column("university_id")
    val universityId: Long? = null,

    @Column("faculty_id")
    val facultyId: Long? = null,

    @Column("major_id")
    val majorId: Long? = null,

    @Column("published_at")
    val publishedAt: LocalDateTime = LocalDateTime.now()
)
