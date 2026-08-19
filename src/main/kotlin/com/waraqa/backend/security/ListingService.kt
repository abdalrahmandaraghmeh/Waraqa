package com.waraqa.backend.service

import com.waraqa.backend.dto.CreateListingRequest
import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.exception.BadRequestException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ListingService {

    fun createListing(request: CreateListingRequest, publisherId: Long): ListingResponseDto {
        val category = request.category.lowercase()

        val (univId, facId, majId) = when (category) {
            "general" -> Triple(null, null, null)
            "academic" -> {
                if (request.universityId == null || request.facultyId == null || request.majorId == null) {
                    throw BadRequestException("الكتب الأكاديمية تتطلب تحديد الجامعة، الكلية، والتخصص.")
                }
                Triple(request.universityId, request.facultyId, request.majorId)
            }
            else -> throw BadRequestException("الفئة غير صالحة، يجب أن تكون 'academic' أو 'general'.")
        }

        return ListingResponseDto(
            id = 101L,
            title = request.title,
            description = request.description,
            price = request.price,
            category = category,
            publisherId = publisherId,
            universityId = univId,
            facultyId = facId,
            majorId = majId,
            createdAt = LocalDateTime.now()
        )
    }
}