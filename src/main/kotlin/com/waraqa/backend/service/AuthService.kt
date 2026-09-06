package com.waraqa.backend.service

import com.waraqa.backend.dto.AuthResponse
import com.waraqa.backend.dto.LoginRequest
import com.waraqa.backend.dto.RegisterRequest
import com.waraqa.backend.repository.UserRepository
import com.waraqa.backend.security.JwtUtils
import com.waraqa.backend.model.User
import com.waraqa.backend.security.TokenBlacklistService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

class RegistrationException(
    val field: String,
    override val message: String
) : RuntimeException(message)

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val tokenBlacklistService: TokenBlacklistService
) {

    @Transactional
    fun register(request: RegisterRequest) {
        if (userRepository.existsByEmail(request.email)) {
            throw RegistrationException("email", "Email is already in use")
        }

        if (userRepository.existsByPhoneNumber(request.phoneNumber)) {
            throw RegistrationException("phone_number", "Phone number is already in use")
        }

        val user = User(
            name = request.name,
            email = request.email,
            phoneNumber = request.phoneNumber,
            password = passwordEncoder.encode(request.password)!!
        )

        userRepository.save(user)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { BadCredentialsException("Invalid email or password") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BadCredentialsException("Invalid email or password")
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

    fun logout(token: String) {
        tokenBlacklistService.blacklistToken(token)
        org.springframework.security.core.context.SecurityContextHolder.clearContext()
    }
}