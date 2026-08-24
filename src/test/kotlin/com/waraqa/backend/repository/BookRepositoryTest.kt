package com.waraqa.backend.repository

import com.waraqa.backend.model.Book
import com.waraqa.backend.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@SpringBootTest
@Transactional
class BookRepositoryTest @Autowired constructor(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) {

    @Test
    fun `should save and retrieve book`() {
        // Create and save user (owner)
        val user = User(
            name = "Test Publisher",
            email = "publisher@test.com",
            phoneNumber = "+1234567890",
            password = "SecurePassword123"
        )
        val savedUser = userRepository.save(user)
        val publisherId = savedUser.userId ?: throw AssertionError("User ID must not be null")

        // Create and save book referencing the user
        val book = Book(
            title = "Kotlin in Action",
            author = "Dmitry Jemerov",
            price = BigDecimal("39.99"),
            publishedAt = LocalDateTime.now(),
            coverImage = "https://example.com/cover.jpg",
            category = "academic",
            publisherId = publisherId
        )
        val savedBook = bookRepository.save(book)
        assertNotNull(savedBook.id)

        // Retrieve book by id
        val retrievedBookOpt = bookRepository.findById(savedBook.id!!)
        assertEquals(true, retrievedBookOpt.isPresent)
        
        val retrievedBook = retrievedBookOpt.get()
        assertEquals("Kotlin in Action", retrievedBook.title)
        assertEquals("Dmitry Jemerov", retrievedBook.author)
        assertEquals(0, retrievedBook.price.compareTo(BigDecimal("39.99")))
        assertEquals(publisherId, retrievedBook.publisherId)
        assertEquals("academic", retrievedBook.category)
    }

    @Test
    fun `should find books by publisherId`() {
        // Create and save user
        val user = User(
            name = "Test Author",
            email = "author@test.com",
            phoneNumber = "+0987654321",
            password = "Password123"
        )
        val savedUser = userRepository.save(user)
        val publisherId = savedUser.userId ?: throw AssertionError("User ID must not be null")

        // Create and save multiple books for this user
        val book1 = Book(
            title = "Book 1",
            author = "Test Author",
            price = BigDecimal("19.99"),
            publishedAt = LocalDateTime.now(),
            category = "academic",
            publisherId = publisherId
        )
        val book2 = Book(
            title = "Book 2",
            author = "Test Author",
            price = BigDecimal("29.99"),
            publishedAt = LocalDateTime.now(),
            category = "general",
            publisherId = publisherId
        )
        bookRepository.save(book1)
        bookRepository.save(book2)

        // Retrieve books by publisherId
        val books = bookRepository.findByPublisherId(publisherId)
        assertEquals(2, books.size)
        assertEquals(true, books.any { it.title == "Book 1" })
        assertEquals(true, books.any { it.title == "Book 2" })
    }
}



