package com.waraqa.backend.repository

import com.waraqa.backend.model.Book
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : CrudRepository<Book, Long> {
    fun findByUserId(userId: Long): List<Book>
}
