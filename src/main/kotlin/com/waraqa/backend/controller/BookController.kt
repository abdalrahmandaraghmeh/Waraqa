package com.waraqa.backend.controller

import com.waraqa.backend.dto.BookResponseDto
import com.waraqa.backend.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
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
}
