package com.waraqa.backend.dto

import com.waraqa.backend.model.Book
import java.math.BigDecimal

data class BookResponseDto(
    val bookId: Long?,
    val title: String,
    val price: BigDecimal?,
    val listingType: String,
    val category: String,
    val coverImage: String?,
    val imagesUrl: List<String>,
    val userId: Long
)

fun Book.toDto(): BookResponseDto {
    return BookResponseDto(
        bookId = this.id,
        title = this.title,
        price = this.price,
        listingType = this.listingType,
        category = this.category,
        coverImage = this.coverImage,
        imagesUrl = this.imagesUrl,
        userId = this.publisherId
    )
}