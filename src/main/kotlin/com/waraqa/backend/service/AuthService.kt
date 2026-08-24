package com.waraqa.backend.service

import com.waraqa.backend.dto.AuthResponse
import com.waraqa.backend.dto.LoginRequest
import com.waraqa.backend.dto.RegisterRequest
import com.waraqa.backend.repository.UserRepository
import com.waraqa.backend.security.JwtUtils
import com.waraqa.backend.model.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

class RegistrationException(
    val field: String,
    override val message: String
) : RuntimeException(message)

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils
) {

        fun register(request: RegisterRequest) {
        if (userRepository.existsByEmail(request.email)) {
            throw RegistrationException("email", "Email is already in use")
        }

        val encodedPassword = passwordEncoder.encode(request.password)

        val user = User(
            name = request.name,
            email = request.email,
            phoneNumber = request.phoneNumber,
            password = encodedPassword!!
        )

        userRepository.save(user)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { RuntimeException("Invalid email or password") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw RuntimeException("Invalid email or password")
        }

        val token = jwtUtils.generateToken(user.email)

        return AuthResponse(
            token = token,
            userId = user.userId,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber
        )
    }
}