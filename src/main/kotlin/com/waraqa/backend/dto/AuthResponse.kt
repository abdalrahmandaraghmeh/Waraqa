package com.waraqa.backend.dto

data class AuthResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val userId: Long?,
    val name: String,
    val email: String,
    val phoneNumber: String
)
