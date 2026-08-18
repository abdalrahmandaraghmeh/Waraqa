package com.waraqa.backend.controller

import com.waraqa.backend.dto.RegisterRequest
import com.waraqa.backend.service.AuthService
import com.waraqa.backend.service.RegistrationException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<Map<String, Any>> {

        return try {

            authService.register(request)

            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    mapOf(
                        "success" to true,
                        "message" to "Registration successful"
                    )
                )

        } catch (e: RegistrationException) {

            ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                    mapOf(
                        "success" to false,
                        "errors" to mapOf(
                            e.field to e.message
                        )
                    )
                )
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<Map<String, Any>> {

        val errors = ex.bindingResult.fieldErrors.associate { error ->

            val fieldName = when (error.field) {
                "name" -> "full_name"
                "phoneNumber" -> "phone_number"
                else -> error.field
            }

            fieldName to (error.defaultMessage ?: "Invalid value")
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                mapOf(
                    "success" to false,
                    "errors" to errors
                )
            )
    }
}