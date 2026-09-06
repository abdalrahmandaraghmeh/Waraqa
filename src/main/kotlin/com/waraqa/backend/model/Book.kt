package com.waraqa.backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("books")
data class Book(
    @Id
    val id: Long? = null,

    val title: String,
    val author: String = "",
    val category: String = "general",

    @Column("is_academic")
    val isAcademic: Boolean = false,

    val edition: String? = null
)