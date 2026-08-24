package com.waraqa.backend.controller

import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.dto.UpdateBookRequest
import com.waraqa.backend.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService
) {

    // GET /api/books/{bookId} (عام للجميع)
    @GetMapping("/{bookId}")
    fun getBookDetails(@PathVariable bookId: Long): ResponseEntity<ListingResponseDto> {
        val book = bookService.getBookById(bookId)
        return ResponseEntity.ok(book)
    }

    // PUT /api/books/{bookId} (يتطلب التحقق من الملكية)
    @PutMapping("/{bookId}")
    fun updateBook(
        @PathVariable bookId: Long,
        @RequestBody request: UpdateBookRequest
    ): ResponseEntity<ListingResponseDto> {
        val mockCurrentUserId = 100L // محاكاة لـ userId المستخرج مستقبلاً من JWT
        val updatedBook = bookService.updateBook(bookId, request, mockCurrentUserId)
        return ResponseEntity.ok(updatedBook)
    }
}