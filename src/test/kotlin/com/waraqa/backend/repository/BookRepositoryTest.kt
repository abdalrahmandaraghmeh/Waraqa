package com.waraqa.backend.repository

import com.waraqa.backend.model.Book
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class BookRepositoryTest @Autowired constructor(
    private val bookRepository: BookRepository
) {

    @Test
    fun `should save and retrieve book`() {
        val book = Book(
            title = "Kotlin in Action",
            author = "Dmitry Jemerov",
            category = "academic",
            isAcademic = true,
            edition = "2nd"
        )
        val savedBook = bookRepository.save(book)
        assertNotNull(savedBook.id)

        val retrievedBookOpt = bookRepository.findById(savedBook.id!!)
        assertTrue(retrievedBookOpt.isPresent)
        
        val retrievedBook = retrievedBookOpt.get()
        assertEquals("Kotlin in Action", retrievedBook.title)
        assertEquals("Dmitry Jemerov", retrievedBook.author)
        assertEquals("academic", retrievedBook.category)
        assertTrue(retrievedBook.isAcademic)
        assertEquals("2nd", retrievedBook.edition)
    }

    @Test
    fun `should find book by title and author`() {
        val book = Book(
            title = "Clean Architecture",
            author = "Robert C. Martin",
            category = "academic",
            isAcademic = true,
            edition = "1st"
        )
        val saved = bookRepository.save(book)
        assertNotNull(saved.id)

        val foundOpt = bookRepository.findByTitleAndAuthor("clean architecture", "robert c. martin")
        assertTrue(foundOpt.isPresent)
        assertEquals(saved.id, foundOpt.get().id)
        assertEquals("Clean Architecture", foundOpt.get().title)
    }

    @Test
    fun `should find all books`() {
        val book1 = Book(title = "Test Book 1", author = "Author 1", category = "general", isAcademic = false)
        val book2 = Book(title = "Test Book 2", author = "Author 2", category = "academic", isAcademic = true)
        bookRepository.save(book1)
        bookRepository.save(book2)

        val allBooks = bookRepository.findAll()
        assertTrue(allBooks.any { it.title == "Test Book 1" })
        assertTrue(allBooks.any { it.title == "Test Book 2" })
    }
}
