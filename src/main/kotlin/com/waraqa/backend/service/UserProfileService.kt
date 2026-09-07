package com.waraqa.backend.service

import com.waraqa.backend.dto.ProfileResponse
import com.waraqa.backend.dto.UpdateProfileRequest
import com.waraqa.backend.exception.ForbiddenException
import com.waraqa.backend.exception.NotFoundException
import com.waraqa.backend.exception.ValidationException
import com.waraqa.backend.repository.UserProfileRepository
import com.waraqa.backend.repository.UserRepository
import com.waraqa.backend.security.SecurityUtils
import org.springframework.stereotype.Service

@Service
class UserProfileService(
    private val userProfileRepository: UserProfileRepository,
    private val userRepository: UserRepository,
    private val securityUtils: SecurityUtils
) {

    /**
     * Gets the public profile for any user by ID.
     */
    fun getProfile(userId: Long): ProfileResponse {
        return userProfileRepository.findProfileById(userId)
            ?: throw NotFoundException("User not found")
    }

    /**
     * Updates the profile for the authenticated user.
     * Enforces ownership: only the profile owner can edit their own profile.
     */
    fun updateProfile(userId: Long, request: UpdateProfileRequest): ProfileResponse {
        // Ownership check: the JWT user must match the path user ID
        val currentUserId = securityUtils.getCurrentUserId()
            ?: throw ForbiddenException("Authentication required")

        if (currentUserId != userId) {
            throw ForbiddenException("You can only edit your own profile")
        }

        // Validate phone number uniqueness if it's being changed
        if (!request.phoneNumber.isNullOrBlank()) {
            if (userProfileRepository.isPhoneNumberTakenByOther(request.phoneNumber, userId)) {
                throw ValidationException(mapOf("phone_number" to "Phone number is already in use"))
            }
        }

        // Validate email uniqueness if it's being changed
        if (!request.email.isNullOrBlank()) {
            if (!request.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$"))) {
                throw ValidationException(mapOf("email" to "Invalid email format"))
            }
            if (userProfileRepository.isEmailTakenByOther(request.email, userId)) {
                throw ValidationException(mapOf("email" to "Email is already in use"))
            }
        }

        // Validate full_name is not blank if provided
        if (request.fullName != null && request.fullName.isBlank()) {
            throw ValidationException(mapOf("full_name" to "Name cannot be empty"))
        }

        val updated = userProfileRepository.updateProfile(
            userId = userId,
            name = request.fullName?.trim(),
            phoneNumber = request.phoneNumber?.trim(),
            avatarUrl = request.profileImage?.trim(),
            email = request.email?.trim(),
            bio = request.bio?.trim()
        )

        if (!updated) {
            throw NotFoundException("User not found")
        }

        // Return the refreshed profile
        return getProfile(userId)
    }
}
