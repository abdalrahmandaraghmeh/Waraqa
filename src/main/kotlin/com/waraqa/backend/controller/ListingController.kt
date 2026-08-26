package com.waraqa.backend.controller

import com.waraqa.backend.dto.CreateListingRequest
import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.service.ListingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/listings")
class ListingController(
    private val listingService: ListingService
) {

    @PostMapping
    fun createListing(@RequestBody request: CreateListingRequest): ResponseEntity<ListingResponseDto> {
        val response = listingService.createListing(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}