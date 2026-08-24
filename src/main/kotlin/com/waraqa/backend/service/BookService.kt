package com.waraqa.backend.service

import com.waraqa.backend.dto.BookResponseDto
import com.waraqa.backend.dto.CreateBookRequest
import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.dto.UpdateBookRequest
import com.waraqa.backend.dto.toDto
import com.waraqa.backend.exception.ForbiddenException
import com.waraqa.backend.model.Book
import com.waraqa.backend.repository.BookRepository
import com.waraqa.backend.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) {

    fun getAllBooks(): List<BookResponseDto> {
        return bookRepository.findAll().map { it.toDto() }
    }

    fun getMyBooks(userEmail: String): List<BookResponseDto> {
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { RuntimeException("User not found") }

        val userId = user.userId ?: throw RuntimeException("User ID is missing")

        return bookRepository.findAllByUserId(userId).map { it.toDto() }
    }

    fun createBook(request: CreateBookRequest): Book {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw ForbiddenException("Unauthenticated user")
        val userEmail = authentication.name

        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { RuntimeException("User not found") }

        val userId = user.userId
            ?: throw RuntimeException("User ID is missing")

        val book = Book(
            title = request.title,
            price = request.price,
            imagesUrl = request.imagesUrl,
            userId = userId
        )

        return bookRepository.save(book)
    }

    fun getBookById(bookId: Long): ListingResponseDto {
        return ListingResponseDto(
            id = bookId,
            title = "كتاب هندسة البرمجيات",
            description = "نسخة ممتازة بدون علامات",
            price = 15.0.toBigDecimal(),
            category = "academic",
            publisherId = 100L,
            universityId = 1L,
            facultyId = 2L,
            majorId = 3L,
            createdAt = LocalDateTime.now()
        )
    }

    fun updateBook(bookId: Long, request: UpdateBookRequest, currentUserId: Long): ListingResponseDto {
        val bookPublisherId = 100L

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