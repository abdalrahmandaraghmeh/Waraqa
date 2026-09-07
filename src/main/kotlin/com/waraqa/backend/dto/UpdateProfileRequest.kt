package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateProfileRequest(
    @JsonProperty("full_name")
    val fullName: String? = null,

    @JsonProperty("phone_number")
    val phoneNumber: String? = null,

    @JsonProperty("profile_image")
    val profileImage: String? = null,

    val email: String? = null,
    val bio: String? = null
)
