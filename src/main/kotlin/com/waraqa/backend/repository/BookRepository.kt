package com.waraqa.backend.repository

import com.waraqa.backend.model.Book
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.util.Optional
import java.sql.Timestamp
import java.time.LocalDateTime

@Repository
class BookRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    private val rowMapper = RowMapper { rs, _ ->
        val sqlArray = rs.getArray("images_url")
        val imagesList = if (sqlArray != null) {
            (sqlArray.array as? Array<*>)?.mapNotNull {it?.toString()} ?: emptyList()
        } else {
            emptyList()
        }
        Book(
            id = rs.getLong("id").takeUnless { rs.wasNull() },
            title = rs.getString("title"),
            author = rs.getString("author") ?: "",
            description = rs.getString("description") ?: "",
            price = rs.getBigDecimal("price"),
            listingType = rs.getString("listing_type") ?: "for_sale",
            exchangeFor = rs.getString("exchange_for"),
            condition = rs.getString("condition") ?: "good",
            category = rs.getString("category") ?: "general",
            type = rs.getString("type"),
            subType = rs.getString("sub_type"),
            edition = rs.getString("edition"),
            coverImage = rs.getString("cover_image"),
            imagesUrl = imagesList,
            viewsCount = rs.getInt("views_count"),
            savesCount = rs.getInt("saves_count"),
            publisherId = rs.getLong("publisher_id"),
            universityId = rs.getLong("university_id").takeUnless { rs.wasNull() },
            facultyId = rs.getLong("faculty_id").takeUnless { rs.wasNull() },
            majorId = rs.getLong("major_id").takeUnless { rs.wasNull() },
            publishedAt = rs.getTimestamp("published_at")?.toLocalDateTime() ?: LocalDateTime.now()
        )
    }

    fun save(book: Book): Book {
        val params = MapSqlParameterSource()
            .addValue("title", book.title)
            .addValue("author", book.author)
            .addValue("description", book.description)
            .addValue("price", book.price)
            .addValue("listingType", book.listingType)
            .addValue("exchangeFor", book.exchangeFor)
            .addValue("condition", book.condition)
            .addValue("category", book.category)
            .addValue("type", book.type)
            .addValue("subType", book.subType)
            .addValue("edition", book.edition)
            .addValue("coverImage", book.coverImage)
            .addValue("imagesUrl", book.imagesUrl.toTypedArray())
            .addValue("viewsCount", book.viewsCount)
            .addValue("savesCount", book.savesCount)
            .addValue("publisherId", book.publisherId)
            .addValue("universityId", book.universityId)
            .addValue("facultyId", book.facultyId)
            .addValue("majorId", book.majorId)
            .addValue("publishedAt", Timestamp.valueOf(book.publishedAt))


        if (book.id == null) {
            val sql = """
                INSERT INTO books (
                title, author, description, price, listing_type, exchange_for,
                 condition, category, type, sub_type, edition, cover_image,
                 images_url, views_count, saves_count, publisher_id, university_id, faculty_id, major_id, published_at
                 )
                VALUES (
                :title, :author, :description, :price, :listingType, :exchangeFor,
                 :condition, :category, :type, :subType, :edition, :coverImage,
                 :imagesUrl, :viewsCount, :savesCount, :publisherId, :universityId, :facultyId, :majorId, :publishedAt
                 )
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
                    description = :description,
                    price = :price,
                    listing_type = :listingType,
                    exchange_for = :exchangeFor,
                    condition = :condition,
                    category = :category,
                    type = :type,
                    sub_type = :subType,
                    edition = :edition,
                    cover_image = :coverImage,
                    images_url = :imagesUrl,
                    views_count = :viewsCount,
                    saves_count = :savesCount,
                    publisher_id = :publisherId,
                    university_id = :universityId,
                    faculty_id = :facultyId,
                    major_id = :majorId,
                    published_at = :publishedAt
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

    fun findAllByUserId(userId: Long): List<Book> {
        return findByPublisherId(userId)
    }

    fun findAll(): List<Book> {
        val sql = "SELECT * FROM books"
        return jdbcTemplate.query(sql, MapSqlParameterSource(), rowMapper)
    }
    fun incrementViewsCount (id: Long) {
        val sql = "UPDATE books SET views_count = views_count + 1 WHERE id = :id"
        val params = MapSqlParameterSource("id", id)
        jdbcTemplate.update(sql, params)
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
            "price_asc" -> "ORDER BY price ASC"
            "price_desc" -> "ORDER BY price DESC"
            "most_viewed" -> "ORDER BY views_count DESC"
            "newest" -> "ORDER BY published_at DESC"
            else -> "ORDER BY published_at DESC"
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
