package com.waraqa.backend.controller

import com.waraqa.backend.dto.RegisterRequest
import com.waraqa.backend.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<Map<String, String>> {

        authService.register(request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapOf("message" to "Registration successful"))
    }
}