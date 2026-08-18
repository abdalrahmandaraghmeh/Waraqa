package com.waraqa.backend.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)