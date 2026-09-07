package com.waraqa.backend.controller

import com.waraqa.backend.dto.*
import com.waraqa.backend.repository.UserRepository
import com.waraqa.backend.security.SecurityUtils
import com.waraqa.backend.service.ListingService
import com.waraqa.backend.service.UserProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val securityUtils: SecurityUtils,
    private val userRepository: UserRepository,
    private val userProfileService: UserProfileService,
    private val listingService: ListingService
) {

    /**
     * GET /api/v1/users/profile
     * Returns the current authenticated user's basic profile (legacy endpoint).
     */
    @GetMapping("/profile")
    fun getProfile(): ResponseEntity<Any> {
        val email = securityUtils.getCurrentUserEmail()
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Unauthorized"))

        val user = userRepository.findByEmail(email).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "User not found"))

        val profileResponse = UserProfileResponse(
            userId = user.userId!!,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber
        )

        return ResponseEntity.ok(profileResponse)
    }

    /**
     * GET /api/v1/users/{id}/profile
     * Returns the full profile card for any user (public).
     * Includes: profile_image, full_name, phone_number, member_since, listings_count, sales_count, rating.
     */
    @GetMapping("/{id}/profile")
    fun getUserProfile(@PathVariable id: Long): ResponseEntity<ProfileResponse> {
        val profile = userProfileService.getProfile(id)
        return ResponseEntity.ok(profile)
    }

    /**
     * PUT /api/v1/users/{id}/profile
     * Edits the authenticated user's profile (owner only).
     * Editable fields: full_name, phone_number, profile_image.
     */
    @PutMapping("/{id}/profile")
    fun updateUserProfile(
        @PathVariable id: Long,
        @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<ProfileResponse> {
        val updatedProfile = userProfileService.updateProfile(id, request)
        return ResponseEntity.ok(updatedProfile)
    }

    /**
     * GET /api/v1/users/{id}/listings
     * Without status param: returns ACTIVE listings for the public profile page.
     * With status param (active/sold): returns owner's listings by status (auth required).
     */
    @GetMapping("/{id}/listings")
    fun getUserListings(
        @PathVariable id: Long,
        @RequestParam(name = "status", required = false) status: String?,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "limit", defaultValue = "8") limit: Int
    ): ResponseEntity<List<ListingResponseDto>> {
        val listings = if (status != null) {
            // Owner-only: filtered by status (auth enforced in service)
            listingService.getOwnerListings(id, status, page, limit)
        } else {
            // Public: only active listings
            listingService.getListingsByPublisher(id, page, limit)
        }
        return ResponseEntity.ok(listings)
    }

    /**
     * GET /api/v1/users/{id}/listings/counts
     * Returns active and sold counts for the owner's listing tabs.
     * Auth required — only the listing owner can see their counts.
     */
    @GetMapping("/{id}/listings/counts")
    fun getUserListingCounts(@PathVariable id: Long): ResponseEntity<ListingCountsResponse> {
        val counts = listingService.getListingCounts(id)
        return ResponseEntity.ok(counts)
    }
}
