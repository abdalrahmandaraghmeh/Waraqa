package com.waraqa.backend.controller
import com.waraqa.backend.dto.AuthResponse
import com.waraqa.backend.dto.LoginRequest
import com.waraqa.backend.service.AuthService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): AuthResponse {
        return authService.login(request)
    }
}