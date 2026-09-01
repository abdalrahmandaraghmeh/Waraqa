package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class CreateListingRequest(
    val image: String?,

    @JsonProperty("images_url")
    val imagesUrl: List<String>? = emptyList(),

    @JsonProperty("listing_type")
    val listingType: String?,

    val title: String?,

    val author: String? = null,

    val category: String?,

    @JsonProperty("university_id")
    val universityId: Long? = null,

    @JsonProperty("faculty_id")
    val facultyId: Long? = null,

    @JsonProperty("major_id")
    val majorId: Long? = null,

    @JsonProperty("sub_type")
    val subType: String? = null,

    val price: BigDecimal?,

    @JsonProperty("exchange_for")
    val exchangeFor: String? = null,

    val condition: String?,

    val description: String?
)