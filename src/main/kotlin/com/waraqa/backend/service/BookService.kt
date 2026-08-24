package com.waraqa.backend.service

import com.waraqa.backend.dto.BookResponseDto
import com.waraqa.backend.dto.CreateBookRequest
import com.waraqa.backend.model.Book
import com.waraqa.backend.repository.BookRepository
import com.waraqa.backend.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) {

    fun publishBook(request: CreateBookRequest, publisherId: Long): BookResponseDto {
        val book = Book(
            title = request.title,
            author = request.author,
            price = request.price,
            publishedAt = request.publishedAt,
            coverImage = request.coverImage,
            category = request.category,
            type = request.type,
            rating = 0.0,
            publisherId = publisherId,
            universityId = request.universityId,
            facultyId = request.facultyId,
            majorId = request.majorId
        )
        val savedBook = bookRepository.save(book)
        
        val publisherName = userRepository.findById(publisherId)
            .map { it.name }
            .orElse("Unknown")

        return BookResponseDto(
            id = savedBook.id!!,
            coverImage = savedBook.coverImage,
            title = savedBook.title,
            publisherName = publisherName,
            price = savedBook.price
        )
    }

    fun getBooksFeed(
        search: String?,
        category: String?,
        universityId: Long?,
        facultyId: Long?,
        majorId: Long?,
        type: String?,
        sort: String?,
        page: Int,
        limitStr: String
    ): List<BookResponseDto> {
        val limit = if (limitStr.lowercase() == "all") {
            Int.MAX_VALUE
        } else {
            limitStr.toIntOrNull() ?: 8
        }
        
        val actualPage = if (limit == Int.MAX_VALUE) 0 else page

        val books = bookRepository.findBooks(
            search = search,
            category = category,
            universityId = universityId,
            facultyId = facultyId,
            majorId = majorId,
            type = type,
            sort = sort,
            page = actualPage,
            limit = limit
        )

        // Bulk load publisher names to avoid N+1 queries
        val publisherIds = books.map { it.publisherId }.distinct()
        val publisherMap = publisherIds.associateWith { id ->
            userRepository.findById(id).map { it.name }.orElse("Unknown")
        }

        return books.map { book ->
            BookResponseDto(
                id = book.id!!,
                coverImage = book.coverImage,
                title = book.title,
                publisherName = publisherMap[book.publisherId] ?: "Unknown",
                price = book.price
            )
        }
    }
}
