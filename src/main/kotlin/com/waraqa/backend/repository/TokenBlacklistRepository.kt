package com.waraqa.backend.repository

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TokenBlacklistRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun save(token: String, expiresAt: LocalDateTime) {
        val sql = """
            INSERT INTO blacklisted_tokens (token, expires_at)
            VALUES (:token, :expiresAt)
            ON CONFLICT (token) DO NOTHING
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("token", token)
            .addValue("expiresAt", expiresAt)
        
        jdbcTemplate.update(sql, params)
    }

    fun exists(token: String): Boolean {
        val sql = "SELECT COUNT(*) FROM blacklisted_tokens WHERE token = :token"
        val params = MapSqlParameterSource("token", token)
        val count = jdbcTemplate.queryForObject(sql, params, Int::class.java) ?: 0
        return count > 0
    }

    fun deleteExpiredTokens(now: LocalDateTime): Int {
        val sql = "DELETE FROM blacklisted_tokens WHERE expires_at < :now"
        val params = MapSqlParameterSource("now", now)
        return jdbcTemplate.update(sql, params)
    }
}
