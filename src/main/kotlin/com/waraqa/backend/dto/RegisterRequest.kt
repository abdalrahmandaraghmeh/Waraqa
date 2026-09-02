package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class RegisterRequest(

    @field:NotBlank(message = "Full name is required")
    @JsonProperty("full_name")
    @JsonAlias("fullName", "name")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "Password must be at least 8 characters and contain uppercase, lowercase, and a number"
    )
    val password: String,

    @field:NotBlank(message = "Phone number is required")
    @JsonProperty("phone_number")
    @JsonAlias("phoneNumber")
    val phoneNumber: String
)