package com.waraqa.repository

import com.waraqa.model.Book
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : CrudRepository<Book, Long> {
    fun findAllByUserId(userId: Long): List<Book>
}
