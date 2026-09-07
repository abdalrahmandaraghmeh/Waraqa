package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ProfileResponse(
    @JsonProperty("user_id")
    val userId: Long,

    @JsonProperty("full_name")
    val fullName: String,

    val email: String,

    @JsonProperty("phone_number")
    val phoneNumber: String,

    @JsonProperty("profile_image")
    val profileImage: String,

    @JsonProperty("member_since")
    val memberSince: LocalDateTime,

    @JsonProperty("listings_count")
    val listingsCount: Int,

    @JsonProperty("sales_count")
    val salesCount: Int,

    val rating: Double,
    val bio: String? = null
)
