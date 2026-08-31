package com.waraqa.backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class User(
    @Id
    @Column("user_id")
    val userId: Long? = null,
    val name: String,
    val email: String,
    @Column("phone_number")
    val phoneNumber: String,
    val password: String,
    @Column("avatar_url")
    val avatarUrl: String? = null,
    val rating: Double = 0.0,
    @Column("total_sales")
    val totalSales: Int = 0,
    @Column("last_seen")
    val lastSeen: LocalDateTime? = null,
    @Column("created_at")
    val createdAt: LocalDateTime? = LocalDateTime.now()
)