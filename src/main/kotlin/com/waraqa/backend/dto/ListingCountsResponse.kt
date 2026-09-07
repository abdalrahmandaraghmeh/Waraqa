package com.waraqa.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ListingCountsResponse(
    @JsonProperty("active_count")
    val activeCount: Int,

    @JsonProperty("sold_count")
    val soldCount: Int
)
