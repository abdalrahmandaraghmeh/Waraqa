package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class ListingResponseDto(
    val id: Long,
    val title: String,
    val author: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,

    @JsonProperty("listing_type")
    val listingType: String = "FOR_SALE",

    @JsonProperty("exchange_for")
    val exchangeFor: String? = null,

    val condition: String = "good",
    val category: String = "academic",

    @JsonProperty("sub_type")
    val subType: String? = null,

    val image: String? = null,

    @JsonProperty("publisher_id")
    val publisherId: Long,

    @JsonProperty("university_id")
    val universityId: Long? = null,

    @JsonProperty("university_name")
    val universityName: String? = null,

    @JsonProperty("faculty_id")
    val facultyId: Long? = null,

    @JsonProperty("faculty_name")
    val facultyName: String? = null,

    @JsonProperty("major_id")
    val majorId: Long? = null,

    @JsonProperty("views_count")
    val viewsCount: Int = 0,

    @JsonProperty("saves_count")
    val savesCount: Int = 0,

    val status: String = "active",

    @JsonProperty("posted_at")
    val postedAt: LocalDateTime = LocalDateTime.now(),

    @JsonProperty("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)