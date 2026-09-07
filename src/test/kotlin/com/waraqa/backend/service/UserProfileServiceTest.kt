package com.waraqa.backend.service

import com.waraqa.backend.dto.ProfileResponse
import com.waraqa.backend.dto.UpdateProfileRequest
import com.waraqa.backend.exception.ForbiddenException
import com.waraqa.backend.exception.ValidationException
import com.waraqa.backend.repository.UserProfileRepository
import com.waraqa.backend.repository.UserRepository
import com.waraqa.backend.security.SecurityUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDateTime

class UserProfileServiceTest {

    private val userProfileRepository = mock(UserProfileRepository::class.java)
    private val userRepository = mock(UserRepository::class.java)
    private val securityUtils = mock(SecurityUtils::class.java)

    private val userProfileService = UserProfileService(
        userProfileRepository, userRepository, securityUtils
    )

    @Test
    fun `should throw ForbiddenException when editing another users profile`() {
        `when`(securityUtils.getCurrentUserId()).thenReturn(1L)
        val request = UpdateProfileRequest(fullName = "Test")
        assertThrows<ForbiddenException> {
            userProfileService.updateProfile(2L, request)
        }
    }

    @Test
    fun `should throw ValidationException when email is taken`() {
        `when`(securityUtils.getCurrentUserId()).thenReturn(1L)
        `when`(userProfileRepository.isEmailTakenByOther("test@test.com", 1L)).thenReturn(true)

        val request = UpdateProfileRequest(email = "test@test.com")

        val exception = assertThrows<ValidationException> {
            userProfileService.updateProfile(1L, request)
        }
        assertEquals("Email is already in use", exception.errors["email"])
    }

    @Test
    fun `should throw ValidationException when phone number is taken`() {
        `when`(securityUtils.getCurrentUserId()).thenReturn(1L)
        `when`(userProfileRepository.isPhoneNumberTakenByOther("123456789", 1L)).thenReturn(true)

        val request = UpdateProfileRequest(phoneNumber = "123456789")

        val exception = assertThrows<ValidationException> {
            userProfileService.updateProfile(1L, request)
        }
        assertEquals("Phone number is already in use", exception.errors["phone_number"])
    }

    @Test
    fun `should successfully update profile when valid data is provided`() {
        `when`(securityUtils.getCurrentUserId()).thenReturn(1L)
        `when`(userProfileRepository.isEmailTakenByOther("valid@test.com", 1L)).thenReturn(false)
        `when`(userProfileRepository.isPhoneNumberTakenByOther("987654321", 1L)).thenReturn(false)
        `when`(userProfileRepository.updateProfile(1L, "New Name", "987654321", "img.jpg", "valid@test.com", "My bio")).thenReturn(true)
        
        val mockResponse = ProfileResponse(
            userId = 1L,
            fullName = "New Name",
            email = "valid@test.com",
            phoneNumber = "987654321",
            profileImage = "img.jpg",
            memberSince = LocalDateTime.now(),
            listingsCount = 0,
            salesCount = 0,
            rating = 5.0,
            bio = "My bio"
        )
        `when`(userProfileRepository.findProfileById(1L)).thenReturn(mockResponse)

        val request = UpdateProfileRequest(
            fullName = "New Name",
            email = "valid@test.com",
            phoneNumber = "987654321",
            profileImage = "img.jpg",
            bio = "My bio"
        )

        val result = userProfileService.updateProfile(1L, request)
        
        assertNotNull(result)
        assertEquals("New Name", result.fullName)
        assertEquals("valid@test.com", result.email)
    }
}
