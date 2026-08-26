package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserProfileResponse(
    @JsonProperty("user_id")
    val userId: Long,
    val name: String,
    val email: String,
    @JsonProperty("phone_number")
    val phoneNumber: String
)
