package com.waraqa.backend.service

import com.waraqa.backend.dto.ListingStatusRequest
import com.waraqa.backend.dto.UpdateListingRequest
import com.waraqa.backend.exception.ForbiddenException
import com.waraqa.backend.model.Listing
import com.waraqa.backend.repository.BookRepository
import com.waraqa.backend.repository.ListingRepository
import com.waraqa.backend.repository.UserRepository
import com.waraqa.backend.security.SecurityUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Optional

class ListingServiceTest {

    private val listingRepository = mock(ListingRepository::class.java)
    private val bookRepository = mock(BookRepository::class.java)
    private val userRepository = mock(UserRepository::class.java)
    private val securityUtils = mock(SecurityUtils::class.java)

    private val listingService = ListingService(
        listingRepository, bookRepository, userRepository, securityUtils
    )

    private fun mockListing(): Listing {
        return Listing(
            id = 100L,
            publisherId = 1L, // Owner is user 1
            bookId = 200L,
            condition = "new",
            price = null,
            status = "active",
            listingType = "FOR_SALE"
        )
    }

    @Test
    fun `should throw ForbiddenException when editing another users listing`() {
        `when`(securityUtils.getCurrentUserId()).thenReturn(2L) // Requester is user 2
        `when`(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing()))

        val request = UpdateListingRequest(condition = "good")

        assertThrows<ForbiddenException> {
            listingService.updateListing(100L, request)
        }
    }

    @Test
    fun `should throw ForbiddenException when marking another users listing as sold`() {
        `when`(securityUtils.getCurrentUserId()).thenReturn(2L)
        `when`(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing()))

        val request = ListingStatusRequest(status = "sold")

        assertThrows<ForbiddenException> {
            listingService.updateListingStatus(100L, request)
        }
    }

    @Test
    fun `should throw ForbiddenException when deleting another users listing`() {
        `when`(securityUtils.getCurrentUserId()).thenReturn(2L)
        `when`(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing()))

        assertThrows<ForbiddenException> {
            listingService.deleteListing(100L)
        }
    }
}
