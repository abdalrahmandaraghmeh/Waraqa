package com.waraqa.backend.security

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class TokenBlacklistService {
    private val blacklistedTokens = ConcurrentHashMap.newKeySet<String>()

    fun blacklistToken(token: String) {
        blacklistedTokens.add(token)
    }

    fun isBlacklisted(token: String): Boolean {
        return blacklistedTokens.contains(token)
    }
}
