package com.waraqa.backend.service

import com.waraqa.backend.dto.RegisterRequest
import com.waraqa.model.User
import com.waraqa.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

class RegistrationException(
    val field: String,
    override val message: String
) : RuntimeException(message)

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(request: RegisterRequest): User {

        if (userRepository.existsByEmail(request.email)) {
            throw RegistrationException(
                field = "email",
                message = "Email is already registered"
            )
        }

        if (userRepository.existsByPhoneNumber(request.phoneNumber)) {
            throw RegistrationException(
                field = "phone_number",
                message = "Phone number is already registered"
            )
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