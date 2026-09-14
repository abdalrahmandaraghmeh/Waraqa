package com.waraqa.backend.repository

import com.waraqa.backend.model.Book
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class BookRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    private val rowMapper = RowMapper { rs, _ ->
        Book(
            id = rs.getLong("id").takeUnless { rs.wasNull() },
            title = rs.getString("title"),
            author = rs.getString("author") ?: "",
            category = rs.getString("category") ?: "general",
            isAcademic = rs.getBoolean("is_academic"),
            subType = rs.getString("sub_type"),
            edition = rs.getString("edition")
        )
    }

    fun save(book: Book): Book {
        val params = MapSqlParameterSource()
            .addValue("title", book.title)
            .addValue("author", book.author)
            .addValue("category", book.category)
            .addValue("isAcademic", book.isAcademic)
            .addValue("subType", book.subType)
            .addValue("edition", book.edition)

        if (book.id == null) {
            val sql = """
                INSERT INTO books (title, author, category, is_academic, sub_type, edition)
                VALUES (:title, :author, :category, :isAcademic, :subType, :edition)
            """.trimIndent()
            val keyHolder = GeneratedKeyHolder()
            jdbcTemplate.update(sql, params, keyHolder, arrayOf("id"))
            val generatedId = keyHolder.key?.toLong() ?: throw RuntimeException("فشل استرجاع الـ ID")
            return book.copy(id = generatedId)
        } else {
            val sql = """
                UPDATE books SET
                    title = :title, author = :author, category = :category, 
                    is_academic = :isAcademic, sub_type = :subType, edition = :edition
                WHERE id = :id
            """.trimIndent()
            params.addValue("id", book.id)
            jdbcTemplate.update(sql, params)
            return book
        }
    }

    fun findById(id: Long): Optional<Book> {
        val sql = "SELECT * FROM books WHERE id = :id"
        val params = MapSqlParameterSource("id", id)
        val books = jdbcTemplate.query(sql, params, rowMapper)
        return if (books.isEmpty()) Optional.empty() else Optional.of(books[0])
    }

    // دالة جديدة ومهمة للبحث عن الكتاب قبل إنشاء إعلان جديد لتجنب التكرار
    fun findByTitleAndAuthor(title: String, author: String): Optional<Book> {
        val sql = "SELECT * FROM books WHERE LOWER(title) = LOWER(:title) AND LOWER(author) = LOWER(:author) LIMIT 1"
        val params = MapSqlParameterSource()
            .addValue("title", title)
            .addValue("author", author)
        val books = jdbcTemplate.query(sql, params, rowMapper)
        return if (books.isEmpty()) Optional.empty() else Optional.of(books[0])
    }
    fun findAll(): List<Book> {
        val sql = "SELECT * FROM books"
        return jdbcTemplate.jdbcOperations.query(sql) { rs, _ ->
            Book(
                id = rs.getLong("id"),
                title = rs.getString("title"),
                author = rs.getString("author"),
                category = rs.getString("category"),
                isAcademic = rs.getBoolean("is_academic"),
                subType = rs.getString("sub_type")
            )
        }
    }
}