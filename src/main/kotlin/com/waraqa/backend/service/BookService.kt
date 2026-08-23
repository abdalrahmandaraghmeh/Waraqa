package com.waraqa.backend.service

import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.dto.UpdateBookRequest
import com.waraqa.backend.exception.ForbiddenException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BookService {

    // 1. GET: جلب تفاصيل كتاب محدد
    fun getBookById(bookId: Long): ListingResponseDto {
        return ListingResponseDto(
            id = bookId,
            title = "كتاب هندسة البرمجيات",
            description = "نسخة ممتازة بدون علامات",
            price = 15.0.toBigDecimal(),
            category = "academic",
            publisherId = 100L, // ID صاحب الكتاب في الداتابيز
            universityId = 1L,
            facultyId = 2L,
            majorId = 3L,
            createdAt = LocalDateTime.now()
        )
    }

    // 2. PUT: تعديل كتاب مع فحص الملكية (Ownership Check)
    fun updateBook(bookId: Long, request: UpdateBookRequest, currentUserId: Long): ListingResponseDto {
        val bookPublisherId = 100L // محاكاة لـ book.publisher.getId() من الداتابيز

        // شرط الملكية: إذا كان ID المستخدم الحالي لا يطابق ID الناشر ترمي 403 Forbidden
        if (bookPublisherId != currentUserId) {
            throw ForbiddenException("غير مسموح لك بتعديل هذا الكتاب لأنك لست المالِك.")
        }

        return ListingResponseDto(
            id = bookId,
            title = request.title ?: "كتاب هندسة البرمجيات",
            description = request.description ?: "نسخة ممتازة بدون علامات",
            price = request.price ?: 15.0.toBigDecimal(),
            category = request.category ?: "academic",
            publisherId = bookPublisherId,
            universityId = request.universityId ?: 1L,
            facultyId = request.facultyId ?: 2L,
            majorId = request.majorId ?: 3L,
            createdAt = LocalDateTime.now()
        )
    }
}