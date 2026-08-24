package com.waraqa.backend.security

import com.waraqa.backend.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtils(private val userRepository: UserRepository) {

    fun getCurrentUserEmail(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal
            if (principal is String) {
                return principal
            }
        }
        return null
    }

    fun getCurrentUserId(): Long? {
        val email = getCurrentUserEmail() ?: return null
        return userRepository.findByEmail(email).map { it.userId }.orElse(null)
    }
}
