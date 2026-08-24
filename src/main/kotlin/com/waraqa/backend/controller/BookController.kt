package com.waraqa.backend.controller

import com.waraqa.backend.dto.BookResponseDto
import com.waraqa.backend.dto.CreateBookRequest
import com.waraqa.backend.service.BookService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService
) {

    @GetMapping
    fun getAllBooks(): ResponseEntity<List<BookResponseDto>> {
        val books = bookService.getAllBooks()
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
}
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