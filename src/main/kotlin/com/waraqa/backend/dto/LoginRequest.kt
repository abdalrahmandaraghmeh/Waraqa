package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class LoginRequest(
    val email: String,
    val password: String
)