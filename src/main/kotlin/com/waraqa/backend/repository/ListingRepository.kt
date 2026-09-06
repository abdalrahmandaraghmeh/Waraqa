package com.waraqa.backend.repository

import com.waraqa.backend.model.Listing
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Optional

@Repository
class ListingRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    private val rowMapper = RowMapper { rs, _ ->
        val sqlArray = rs.getArray("images_url")
        val imagesList = if (sqlArray != null) {
            (sqlArray.array as? Array<*>)?.mapNotNull { it?.toString() } ?: emptyList()
        } else {
            emptyList()
        }

        Listing(
            id = rs.getLong("id").takeUnless { rs.wasNull() },
            bookId = rs.getLong("book_id"),
            publisherId = rs.getLong("publisher_id"),
            description = rs.getString("description") ?: "",
            price = rs.getBigDecimal("price"),
            listingType = rs.getString("listing_type") ?: "for_sale",
            exchangeFor = rs.getString("exchange_for"),
            condition = rs.getString("condition") ?: "good",
            coverImage = rs.getString("cover_image"),
            imagesUrl = imagesList,
            viewsCount = rs.getInt("views_count"),
            savesCount = rs.getInt("saves_count"),
            universityId = rs.getLong("university_id").takeUnless { rs.wasNull() },
            facultyId = rs.getLong("faculty_id").takeUnless { rs.wasNull() },
            majorId = rs.getLong("major_id").takeUnless { rs.wasNull() },
            publishedAt = rs.getTimestamp("published_at")?.toLocalDateTime() ?: LocalDateTime.now()
        )
    }

    fun save(listing: Listing): Listing {
        val params = MapSqlParameterSource()
            .addValue("bookId", listing.bookId)
            .addValue("publisherId", listing.publisherId)
            .addValue("description", listing.description)
            .addValue("price", listing.price)
            .addValue("listingType", listing.listingType)
            .addValue("exchangeFor", listing.exchangeFor)
            .addValue("condition", listing.condition)
            .addValue("coverImage", listing.coverImage)
            .addValue("imagesUrl", listing.imagesUrl.toTypedArray())
            .addValue("viewsCount", listing.viewsCount)
            .addValue("savesCount", listing.savesCount)
            .addValue("universityId", listing.universityId)
            .addValue("facultyId", listing.facultyId)
            .addValue("majorId", listing.majorId)
            .addValue("publishedAt", Timestamp.valueOf(listing.publishedAt))

        if (listing.id == null) {
            val sql = """
                INSERT INTO listings (
                    book_id, publisher_id, description, price, listing_type, exchange_for,
                    condition, cover_image, images_url, views_count, saves_count,
                    university_id, faculty_id, major_id, published_at
                ) VALUES (
                    :bookId, :publisherId, :description, :price, :listingType, :exchangeFor,
                    :condition, :coverImage, :imagesUrl, :viewsCount, :savesCount,
                    :universityId, :facultyId, :majorId, :publishedAt
                )
            """.trimIndent()
            val keyHolder = GeneratedKeyHolder()
            jdbcTemplate.update(sql, params, keyHolder, arrayOf("id"))
            val generatedId = keyHolder.key?.toLong() ?: throw RuntimeException("فشل استرجاع الـ ID")
            return listing.copy(id = generatedId)
        } else {
            // كود الـ Update مشابه للـ Insert
            val sql = """
                UPDATE listings SET
                    description = :description, price = :price, listing_type = :listingType,
                    exchange_for = :exchangeFor, condition = :condition, cover_image = :coverImage,
                    images_url = :imagesUrl, views_count = :viewsCount, saves_count = :savesCount,
                    university_id = :universityId, faculty_id = :facultyId, major_id = :majorId
                WHERE id = :id
            """.trimIndent()
            params.addValue("id", listing.id)
            jdbcTemplate.update(sql, params)
            return listing
        }
    }

    // استعلام البحث المتقدم باستخدام JOIN بين جدول الإعلانات والكتب
    fun findListings(
        search: String?, category: String?, universityId: Long?,
        sort: String?, page: Int, limit: Int
    ): List<Listing> {
        val conditions = mutableListOf<String>()
        val params = MapSqlParameterSource()

        if (!search.isNullOrBlank()) {
            // البحث يتم في جدول الكتب (b)
            conditions.add("(LOWER(b.title) LIKE :search OR LOWER(b.author) LIKE :search)")
            params.addValue("search", "%${search.lowercase()}%")
        }

        if (!category.isNullOrBlank() && category != "all") {
            conditions.add("b.category = :category")
            params.addValue("category", category)
        }

        if (universityId != null) {
            // الجامعة موجودة في جدول الإعلانات (l)
            conditions.add("l.university_id = :universityId")
            params.addValue("universityId", universityId)
        }

        val whereClause = if (conditions.isNotEmpty()) "WHERE " + conditions.joinToString(" AND ") else ""

        val orderBy = when (sort) {
            "price_asc" -> "ORDER BY l.price ASC"
            "price_desc" -> "ORDER BY l.price DESC"
            "newest" -> "ORDER BY l.published_at DESC"
            else -> "ORDER BY l.published_at DESC"
        }

        val offset = page * limit
        val sql = """
            SELECT l.* 
            FROM listings l
            JOIN books b ON l.book_id = b.id
            $whereClause
            $orderBy
            LIMIT :limit OFFSET :offset
        """.trimIndent()

        params.addValue("limit", limit)
        params.addValue("offset", offset)

        return jdbcTemplate.query(sql, params, rowMapper)
    }
    fun findListingsWithFilters(
        search: String?,
        category: String?,
        universityId: Long?,
        facultyId: Long?,
        majorId: Long?,
        subType: String?,
        sort: String?,
        offset: Int,
        limit: Int
    ): List<Listing> {
        var sql = """
        SELECT l.* FROM listings l 
        JOIN books b ON l.book_id = b.id 
        WHERE 1=1
    """.trimIndent()

        val params = org.springframework.jdbc.core.namedparam.MapSqlParameterSource()

        if (!search.isNullOrBlank()) {
            sql += " AND (b.title ILIKE :search OR b.author ILIKE :search)"
            params.addValue("search", "%$search%")
        }
        if (!category.isNullOrBlank()) {
            sql += " AND b.category = :category"
            params.addValue("category", category)
        }
        if (universityId != null) {
            sql += " AND l.university_id = :universityId"
            params.addValue("universityId", universityId)
        }
        if (facultyId != null) {
            sql += " AND l.faculty_id = :facultyId"
            params.addValue("facultyId", facultyId)
        }
        if (majorId != null) {
            sql += " AND l.major_id = :majorId"
            params.addValue("majorId", majorId)
        }

        // الترتيب
        sql += when (sort) {
            "price_low" -> " ORDER BY l.price ASC"
            "price_high" -> " ORDER BY l.price DESC"
            "newest" -> " ORDER BY l.published_at DESC"
            else -> " ORDER BY l.views_count DESC"
        }

        sql += " LIMIT :limit OFFSET :offset"
        params.addValue("limit", limit)
        params.addValue("offset", offset)

        return jdbcTemplate.query(sql, params) { rs, _ ->
            Listing(
                id = rs.getLong("id"),
                bookId = rs.getLong("book_id"),
                publisherId = rs.getLong("publisher_id"),
                description = rs.getString("description"),
                price = rs.getBigDecimal("price"),
                listingType = rs.getString("listing_type"),
                exchangeFor = rs.getString("exchange_for"),
                condition = rs.getString("condition"),
                coverImage = rs.getString("cover_image"),
                universityId = rs.getObject("university_id") as Long?,
                facultyId = rs.getObject("faculty_id") as Long?,
                majorId = rs.getObject("major_id") as Long?
            )
        }
    }
}