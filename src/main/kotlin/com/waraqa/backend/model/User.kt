package com.waraqa.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    @Id
    @Column("user_id")
    val userId: Long? = null,
    val name: String,
    val email: String,
    @Column("phone_number")
    val phoneNumber: String,
    val password: String
)