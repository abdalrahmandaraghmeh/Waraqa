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
    val listingType: String = "for_sale",

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

    @JsonProperty("faculty_id")
    val facultyId: Long? = null,

    @JsonProperty("major_id")
    val majorId: Long? = null,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)