package com.waraqa.backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("listings")
data class Listing(
    @Id
    val id: Long? = null,

    @Column("book_id")
    val bookId: Long,

    @Column("publisher_id")
    val publisherId: Long,

    val description: String = "",
    val price: BigDecimal? = null,

    @Column("listing_type")
    val listingType: String? = "FOR_SALE",

    @Column("exchange_for")
    val exchangeFor: String? = null,

    val condition: String = "good",

    @Column("cover_image")
    val coverImage: String? = null,

    @Column("images_url")
    val imagesUrl: List<String> = emptyList(),

    @Column("views_count")
    val viewsCount: Int = 0,

    @Column("saves_count")
    val savesCount: Int = 0,

    @Column("university_id")
    val universityId: Long? = null,

    @Column("faculty_id")
    val facultyId: Long? = null,

    @Column("major_id")
    val majorId: Long? = null,

    @Column("published_at")
    val publishedAt: LocalDateTime = LocalDateTime.now(),

    val status: String = "active"
)