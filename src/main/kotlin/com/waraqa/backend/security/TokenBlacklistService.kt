package com.waraqa.backend.security

import com.waraqa.backend.repository.TokenBlacklistRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class TokenBlacklistService(
    private val tokenBlacklistRepository: TokenBlacklistRepository,
    private val jwtUtils: JwtUtils
) {
    fun blacklistToken(token: String) {
        try {
            val expirationDate = jwtUtils.getExpirationDateFromToken(token)
            val localDateTime = LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault())
            tokenBlacklistRepository.save(token, localDateTime)
        } catch (e: Exception) {
            // If token is invalid or expired, we just ignore it
        }
    }

    fun isBlacklisted(token: String): Boolean {
        return tokenBlacklistRepository.exists(token)
    }

    /**
     * Runs every hour to clean up tokens that have already expired
     * from the database to prevent unbounded growth.
     */
    @Scheduled(fixedRate = 3600000)
    fun cleanupExpiredTokens() {
        tokenBlacklistRepository.deleteExpiredTokens(LocalDateTime.now())
    }
}
