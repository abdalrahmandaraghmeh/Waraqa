package com.waraqa.backend.service

import com.waraqa.backend.dto.BookResponseDto
import com.waraqa.backend.dto.toDto
import com.waraqa.repository.BookRepository
import com.waraqa.repository.UserRepository
import org.springframework.stereotype.Service

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
}
