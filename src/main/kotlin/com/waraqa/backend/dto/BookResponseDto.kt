package com.waraqa.backend.dto

import com.waraqa.backend.model.Book
import java.math.BigDecimal

data class BookResponseDto(
    val bookId: Long?,
    val title: String,
    val price: BigDecimal,
    val imagesUrl: List<String>,
    val userId: Long
)

fun Book.toDto(): BookResponseDto {
    return BookResponseDto(
        bookId = this.bookId,
        title = this.title,
        price = this.price,
        imagesUrl = this.imagesUrl,
        userId = this.userId
    )
}
