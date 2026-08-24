package com.waraqa.backend.controller

import com.waraqa.backend.dto.BookResponseDto
import com.waraqa.backend.dto.CreateBookRequest
import com.waraqa.backend.service.BookService
import com.waraqa.backend.security.SecurityUtils
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService,
    private val securityUtils: SecurityUtils
) {

    @GetMapping
    fun getBooks(
        @RequestParam(name = "search", required = false) search: String?,
        @RequestParam(name = "category", required = false, defaultValue = "all") category: String?,
        @RequestParam(name = "university_id", required = false) universityId: Long?,
        @RequestParam(name = "faculty_id", required = false) facultyId: Long?,
        @RequestParam(name = "major_id", required = false) majorId: Long?,
        @RequestParam(name = "type", required = false) type: String?,
        @RequestParam(name = "sort", required = false, defaultValue = "top_rated") sort: String?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "limit", required = false, defaultValue = "8") limit: String
    ): ResponseEntity<List<BookResponseDto>> {
        val books = bookService.getBooksFeed(
            search = search,
            category = category,
            universityId = universityId,
            facultyId = facultyId,
            majorId = majorId,
            type = type,
            sort = sort,
            page = page,
            limitStr = limit
        )
        return ResponseEntity.ok(books)
    }

    @PostMapping
    fun publishBook(
        @Valid @RequestBody request: CreateBookRequest
    ): ResponseEntity<Any> {
        val publisherId = securityUtils.getCurrentUserId()
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Unauthorized"))

        val response = bookService.publishBook(request, publisherId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
