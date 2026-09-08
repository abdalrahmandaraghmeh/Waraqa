package com.waraqa.backend.controller

import com.waraqa.backend.dto.CreateListingRequest
import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.dto.ListingStatusRequest
import com.waraqa.backend.dto.UpdateListingRequest
import com.waraqa.backend.service.ListingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/listings")
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
        @RequestParam(name = "limit", defaultValue = "20") limit: Int
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

    @GetMapping("/{id}")
    fun getListing(@PathVariable id: Long): ResponseEntity<ListingResponseDto> {
        val listing = listingService.getListingById(id)
        return ResponseEntity.ok(listing)
    }

    /**
     * PUT /api/v1/listings/{id}
     * Edits a listing. Only the listing owner can edit.
     */
    @PutMapping("/{id}")
    fun updateListing(
        @PathVariable id: Long,
        @RequestBody request: UpdateListingRequest
    ): ResponseEntity<ListingResponseDto> {
        val updatedListing = listingService.updateListing(id, request)
        return ResponseEntity.ok(updatedListing)
    }

    /**
     * PATCH /api/v1/listings/{id}/status
     * Marks a listing as sold. Only the listing owner can do this.
     */
    @PatchMapping("/{id}/status")
    fun updateListingStatus(
        @PathVariable id: Long,
        @RequestBody request: ListingStatusRequest
    ): ResponseEntity<ListingResponseDto> {
        val updatedListing = listingService.updateListingStatus(id, request)
        return ResponseEntity.ok(updatedListing)
    }

    /**
     * DELETE /api/v1/listings/{id}
     * Deletes a listing permanently. Only the listing owner can delete.
     */
    @DeleteMapping("/{id}")
    fun deleteListing(@PathVariable id: Long): ResponseEntity<Void> {
        listingService.deleteListing(id)
        return ResponseEntity.noContent().build()
    }
}