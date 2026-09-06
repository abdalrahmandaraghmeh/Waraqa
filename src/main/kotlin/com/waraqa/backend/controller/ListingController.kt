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
import org.springframework.web.bind.annotation.*

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
    @GetMapping
    fun getListings(
        @RequestParam(name = "search", required = false) search: String?,
        @RequestParam(name = "category", required = false) category: String?,
        @RequestParam(name = "university_id", required = false) universityId: Long?,
        @RequestParam(name = "faculty_id", required = false) facultyId: Long?,
        @RequestParam(name = "major_id", required = false) majorId: Long?,
        @RequestParam(name = "sub_type", required = false) subType: String?,
        @RequestParam(name = "sort", defaultValue = "top_rated") sort: String?,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "limit", defaultValue = "8") limit: Int
    ): ResponseEntity<List<ListingResponseDto>> {
        val listings = listingService.getListings(
            search = search,
            category = category,
            universityId = universityId,
            facultyId = facultyId,
            majorId = majorId,
            subType = subType,
            sort = sort,
            page = page,
            limit = limit
        )
        return ResponseEntity.ok(listings)
    }
}