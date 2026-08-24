package com.waraqa.repository

import com.waraqa.model.Book
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : CrudRepository<Book, Long> {
    // Used by Mays for "My Books" portfolio
    fun findAllByUserId(userId: Long): List<Book>
}
