package com.waraqa.backend.service

import com.waraqa.backend.dto.RegisterRequest
import com.waraqa.model.User
import com.waraqa.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(request: RegisterRequest): User {

        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        if (userRepository.existsByPhoneNumber(request.phoneNumber)) {
            throw IllegalArgumentException("Phone number already exists")
        }

        val hashedPassword = passwordEncoder.encode(request.password)
            ?: throw IllegalStateException("Failed to encode password")
        val user = User(
            name = request.name,
            email = request.email,
            phoneNumber = request.phoneNumber,
            password = hashedPassword
        )

        return userRepository.save(user)
    }
}