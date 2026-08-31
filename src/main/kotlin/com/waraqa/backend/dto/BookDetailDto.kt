package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class SellerDto(
    @JsonProperty("user_id")
    val userId: Long,
    val name: String,

    @JsonProperty("avatar_url")
    val avatarUrl: String?,
    val rating: Double,

    @JsonProperty("total_sales")
    val totalSales: Int,

    @JsonProperty("is_active_today")
    val isActiveToday: Boolean
)

data class BookDetailDto(
    val id: Long,
    val title: String,
    val author: String,
    val description: String?,
    val price: BigDecimal?,

    @JsonProperty("listing_type")
    val listingType: String,

    @JsonProperty("exchange_for")
    val exchangeFor: String?,
    val condition: String,
    val category: String,
    val type: String?,

    @JsonProperty("sub_type")
    val subType: String?,
    val edition: String?,

    @JsonProperty("cover_image")
    val coverImage: String?,

    @JsonProperty("images_url")
    val imagesUrl: List<String> = emptyList(),

    @JsonProperty("views_count")
    val viewsCount: Int,

    @JsonProperty("saves_count")
    val savesCount: Int,

    @JsonProperty("published_at")
    val publishedAt: LocalDateTime?,

    @JsonProperty("university_name")
    val universityName: String?,

    @JsonProperty("faculty_name")
    val facultyName: String?,

    @JsonProperty("major_name")
    val majorName: String?,

    val seller: SellerDto

)