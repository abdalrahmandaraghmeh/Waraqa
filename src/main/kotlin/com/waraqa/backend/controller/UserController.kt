package com.waraqa.backend.controller

import com.waraqa.backend.repository.UserRepository
import com.waraqa.backend.security.SecurityUtils
import com.waraqa.backend.dto.UserProfileResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val securityUtils: SecurityUtils,
    private val userRepository: UserRepository
) {

    @GetMapping("/profile")
    fun getProfile(): ResponseEntity<Any> {
        val email = securityUtils.getCurrentUserEmail()
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Unauthorized"))

        val user = userRepository.findByEmail(email).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "User not found"))

        val profileResponse = UserProfileResponse(
            userId = user.userId!!,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber
        )

        return ResponseEntity.ok(profileResponse)
    }
}
