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

    fun getBooks(
        search: String?,
        category: String?,
        universityId: Long?,
        facultyId: Long?,
        majorId: Long?,
        type: String?,
        sort: String?,
        page: Int,
        limit: Int
    ): List<BookResponseDto> {
        return bookRepository.findBooks(
            search = search,
            category = category,
            universityId = universityId,
            facultyId = facultyId,
            majorId = majorId,
            type = type,
            sort = sort,
            page = page,
            limit = limit
        ).map { it.toDto() }
    }

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
            coverImage = request.imagesUrl.firstOrNull(),
            publisherId = userId,
            universityId = request.universityId,
            facultyId = request.facultyId,
            majorId = request.majorId,
            category = request.category ?: "academic",
            type = request.type ?: "Used"
        )
        return bookRepository.save(book)
    }

    fun getBookById(bookId: Long): ListingResponseDto {
        val book = bookRepository.findById(bookId)
            .orElseThrow { RuntimeException("Book not found") }

        return ListingResponseDto(
            id = book.id ?: bookId,
            title = book.title,
            description = "Book listing",
            price = book.price,
            category = book.category ?: "academic",
            publisherId = book.publisherId ?: 1L,
            universityId = book.universityId ?: 1L,
            facultyId = book.facultyId ?: 1L,
            majorId = book.majorId ?: 1L,
            createdAt = book.publishedAt
        )
    }

    fun updateBook(bookId: Long, request: UpdateBookRequest, currentUserId: Long): ListingResponseDto {
        val book = bookRepository.findById(bookId)
            .orElseThrow { RuntimeException("Book not found") }

        if (book.publisherId != currentUserId) {
            throw ForbiddenException("Not authorized to edit this book.")
        }

        val updated = book.copy(
            title = request.title ?: book.title,
            price = request.price ?: book.price,
            category = request.category ?: book.category,
            universityId = request.universityId ?: book.universityId,
            facultyId = request.facultyId ?: book.facultyId,
            majorId = request.majorId ?: book.majorId
        )

        bookRepository.save(updated)

        return ListingResponseDto(
            id = updated.id ?: bookId,
            title = updated.title,
            description = request.description ?: "",
            price = updated.price,
            category = updated.category ?: "academic",
            publisherId = updated.publisherId ?: currentUserId,
            universityId = updated.universityId ?: 1L,
            facultyId = updated.facultyId ?: 1L,
            majorId = updated.majorId ?: 1L,
            createdAt = updated.publishedAt
        )
    }
}