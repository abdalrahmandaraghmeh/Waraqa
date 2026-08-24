package com.waraqa.backend.repository

import com.waraqa.backend.model.Book
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.util.Optional
import java.sql.Timestamp

@Repository
class BookRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    private val rowMapper = RowMapper { rs, _ ->
        Book(
            id = rs.getLong("id").takeUnless { rs.wasNull() },
            title = rs.getString("title"),
            author = rs.getString("author"),
            price = rs.getBigDecimal("price"),
            publishedAt = rs.getTimestamp("published_at").toLocalDateTime(),
            coverImage = rs.getString("cover_image"),
            category = rs.getString("category"),
            type = rs.getString("type"),
            rating = rs.getDouble("rating"),
            publisherId = rs.getLong("publisher_id"),
            universityId = rs.getLong("university_id").takeUnless { rs.wasNull() },
            facultyId = rs.getLong("faculty_id").takeUnless { rs.wasNull() },
            majorId = rs.getLong("major_id").takeUnless { rs.wasNull() }
        )
    }

    fun save(book: Book): Book {
        val params = MapSqlParameterSource()
            .addValue("title", book.title)
            .addValue("author", book.author)
            .addValue("price", book.price)
            .addValue("publishedAt", Timestamp.valueOf(book.publishedAt))
            .addValue("coverImage", book.coverImage)
            .addValue("category", book.category)
            .addValue("type", book.type)
            .addValue("rating", book.rating)
            .addValue("publisherId", book.publisherId)
            .addValue("universityId", book.universityId)
            .addValue("facultyId", book.facultyId)
            .addValue("majorId", book.majorId)

        if (book.id == null) {
            val sql = """
                INSERT INTO books (title, author, price, published_at, cover_image, category, type, rating, publisher_id, university_id, faculty_id, major_id)
                VALUES (:title, :author, :price, :publishedAt, :coverImage, :category, :type, :rating, :publisherId, :universityId, :facultyId, :majorId)
            """.trimIndent()
            val keyHolder = GeneratedKeyHolder()
            jdbcTemplate.update(sql, params, keyHolder, arrayOf("id"))
            val generatedId = keyHolder.key?.toLong() ?: throw RuntimeException("Failed to retrieve generated ID")
            return book.copy(id = generatedId)
        } else {
            val sql = """
                UPDATE books SET
                    title = :title,
                    author = :author,
                    price = :price,
                    published_at = :publishedAt,
                    cover_image = :coverImage,
                    category = :category,
                    type = :type,
                    rating = :rating,
                    publisher_id = :publisherId,
                    university_id = :universityId,
                    faculty_id = :facultyId,
                    major_id = :majorId
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

    fun deleteById(id: Long) {
        val sql = "DELETE FROM books WHERE id = :id"
        val params = MapSqlParameterSource("id", id)
        jdbcTemplate.update(sql, params)
    }

    fun findByPublisherId(publisherId: Long): List<Book> {
        val sql = "SELECT * FROM books WHERE publisher_id = :publisherId"
        val params = MapSqlParameterSource("publisherId", publisherId)
        return jdbcTemplate.query(sql, params, rowMapper)
    }

    fun findBooks(
        search: String?,
        category: String?,
        universityId: Long?,
        facultyId: Long?,
        majorId: Long?,
        type: String?,
        sort: String?,
        page: Int,
        limit: Int
    ): List<Book> {
        val conditions = mutableListOf<String>()
        val params = MapSqlParameterSource()

        if (!search.isNullOrBlank()) {
            conditions.add("(LOWER(title) LIKE :search OR LOWER(author) LIKE :search)")
            params.addValue("search", "%${search.lowercase()}%")
        }

        if (!category.isNullOrBlank() && category != "all") {
            conditions.add("category = :category")
            params.addValue("category", category)
        }

        if (universityId != null) {
            conditions.add("university_id = :universityId")
            params.addValue("universityId", universityId)
        }

        if (facultyId != null) {
            conditions.add("faculty_id = :facultyId")
            params.addValue("facultyId", facultyId)
        }

        if (majorId != null) {
            conditions.add("major_id = :majorId")
            params.addValue("majorId", majorId)
        }

        if (!type.isNullOrBlank()) {
            conditions.add("type = :type")
            params.addValue("type", type)
        }

        val whereClause = if (conditions.isNotEmpty()) {
            "WHERE " + conditions.joinToString(" AND ")
        } else {
            ""
        }

        val orderBy = when (sort) {
            "top_rated" -> "ORDER BY rating DESC"
            else -> "ORDER BY rating DESC"
        }

        val offset = page * limit
        val sql = """
            SELECT * FROM books
            $whereClause
            $orderBy
            LIMIT :limit OFFSET :offset
        """.trimIndent()

        params.addValue("limit", limit)
        params.addValue("offset", offset)

        return jdbcTemplate.query(sql, params, rowMapper)
    }
}

