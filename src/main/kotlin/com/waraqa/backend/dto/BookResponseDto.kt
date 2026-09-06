package com.waraqa.backend.dto

data class BookResponseDto(
    val id: Long,
    val title: String,
    val author: String,
    val category: String,
    val isAcademic: Boolean
)