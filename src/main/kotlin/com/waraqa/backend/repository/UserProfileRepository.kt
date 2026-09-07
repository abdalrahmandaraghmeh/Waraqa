package com.waraqa.backend.repository

import com.waraqa.backend.dto.ProfileResponse
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UserProfileRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    /**
     * Fetches a user's profile card data including listings_count computed via subquery.
     * Returns null if the user doesn't exist.
     */
    fun findProfileById(userId: Long): ProfileResponse? {
        val sql = """
            SELECT
                u.user_id,
                u.name,
                u.email,
                u.phone_number,
                u.avatar_url,
                u.rating,
                u.total_sales,
                u.last_seen,
                u.bio,
                u.created_at,
                COUNT(l.id) AS listings_count
            FROM users u
            LEFT JOIN listings l ON l.publisher_id = u.user_id
            WHERE u.user_id = :userId
            GROUP BY u.user_id
        """.trimIndent()

        val params = MapSqlParameterSource("userId", userId)

        val results = jdbcTemplate.query(sql, params) { rs, _ ->
            ProfileResponse(
                userId = rs.getLong("user_id"),
                fullName = rs.getString("name") ?: "",
                email = rs.getString("email") ?: "",
                phoneNumber = rs.getString("phone_number") ?: "",
                profileImage = rs.getString("avatar_url") ?: "/images/default-avatar.png",
                memberSince = rs.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                listingsCount = rs.getInt("listings_count"),
                salesCount = rs.getInt("total_sales"),
                rating = rs.getDouble("rating"),
                bio = rs.getString("bio")
            )
        }

        return results.firstOrNull()
    }

    /**
     * Updates the profile fields for a given user.
     * Only updates fields that are non-null in the request.
     */
    fun updateProfile(userId: Long, name: String?, phoneNumber: String?, avatarUrl: String?, email: String?, bio: String?): Boolean {
        val setClauses = mutableListOf<String>()
        val params = MapSqlParameterSource("userId", userId)

        if (name != null) {
            setClauses.add("name = :name")
            params.addValue("name", name)
        }
        if (phoneNumber != null) {
            setClauses.add("phone_number = :phoneNumber")
            params.addValue("phoneNumber", phoneNumber)
        }
        if (avatarUrl != null) {
            setClauses.add("avatar_url = :avatarUrl")
            params.addValue("avatarUrl", avatarUrl)
        }
        if (email != null) {
            setClauses.add("email = :email")
            params.addValue("email", email)
        }
        if (bio != null) {
            setClauses.add("bio = :bio")
            params.addValue("bio", bio)
        }

        if (setClauses.isEmpty()) return false

        val sql = "UPDATE users SET ${setClauses.joinToString(", ")} WHERE user_id = :userId"
        return jdbcTemplate.update(sql, params) > 0
    }

    /**
     * Checks if a phone number is already in use by another user.
     */
    fun isPhoneNumberTakenByOther(phoneNumber: String, excludeUserId: Long): Boolean {
        val sql = "SELECT COUNT(*) FROM users WHERE phone_number = :phone AND user_id != :userId"
        val params = MapSqlParameterSource()
            .addValue("phone", phoneNumber)
            .addValue("userId", excludeUserId)
        val count = jdbcTemplate.queryForObject(sql, params, Int::class.java) ?: 0
        return count > 0
    }

    /**
     * Checks if an email is already in use by another user.
     */
    fun isEmailTakenByOther(email: String, excludeUserId: Long): Boolean {
        val sql = "SELECT COUNT(*) FROM users WHERE email = :email AND user_id != :userId"
        val params = MapSqlParameterSource()
            .addValue("email", email)
            .addValue("userId", excludeUserId)
        val count = jdbcTemplate.queryForObject(sql, params, Int::class.java) ?: 0
        return count > 0
    }
}
