package com.waraqa.backend.controller

import com.waraqa.backend.dto.BookDetailDto
import com.waraqa.backend.dto.BookResponseDto
import com.waraqa.backend.dto.CreateBookRequest
import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.dto.UpdateBookRequest
import com.waraqa.backend.service.BookService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService
) {

    @GetMapping
    fun getAllBooks(
        @RequestParam(name = "search", required = false) search: String?,
        @RequestParam(name = "category", required = false) category: String?,
        @RequestParam(name = "university_id", required = false) universityId: Long?,
        @RequestParam(name = "faculty_id", required = false) facultyId: Long?,
        @RequestParam(name = "major_id", required = false) majorId: Long?,
        @RequestParam(name = "type", required = false) type: String?,
        @RequestParam(name = "sort", defaultValue = "top_rated") sort: String?,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "limit", defaultValue = "8") limit: Int
    ): ResponseEntity<List<BookResponseDto>> {
        val books = bookService.getBooks(
            search = search,
            category = category,
            universityId = universityId,
            facultyId = facultyId,
            majorId = majorId,
            type = type,
            sort = sort,
            page = page,
            limit = limit
        )
        return ResponseEntity.ok(books)
    }

    @GetMapping("/my-books")
    fun getMyBooks(authentication: Authentication): ResponseEntity<List<BookResponseDto>> {
        val userEmail = authentication.name
        val myBooks = bookService.getMyBooks(userEmail)
        return ResponseEntity.ok(myBooks)
    }

    @PostMapping
    fun createBook(
        @Valid @RequestBody request: CreateBookRequest
    ): ResponseEntity<*> {
        val book = bookService.createBook(request)
        return ResponseEntity.ok(book)
    }

    @GetMapping("/{bookId}")
    fun getBookDetails(@PathVariable bookId: Long): ResponseEntity<BookDetailDto> {
        val book = bookService.getBookById(bookId)
        return ResponseEntity.ok(book)
    }

    @PutMapping("/{bookId}")
    fun updateBook(
        @PathVariable bookId: Long,
        @RequestBody request: UpdateBookRequest
    ): ResponseEntity<ListingResponseDto> {
        val mockCurrentUserId = 100L
        val updatedBook = bookService.updateBook(bookId, request, mockCurrentUserId)
        return ResponseEntity.ok(updatedBook)
    }
}