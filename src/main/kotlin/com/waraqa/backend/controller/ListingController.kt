package com.waraqa.backend.controller

import com.waraqa.backend.dto.CreateListingRequest
import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.service.ListingService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/listings")
class ListingController(
    private val listingService: ListingService
) {

    @PostMapping
    fun createListing(@Valid @RequestBody request: CreateListingRequest): ResponseEntity<ListingResponseDto> {
        val mockPublisherId = 1L
        val response = listingService.createListing(request, mockPublisherId)
        return ResponseEntity(response, HttpStatus.CREATED)
    }
}