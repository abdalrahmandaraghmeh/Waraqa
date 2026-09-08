package com.waraqa.backend.repository

import com.waraqa.backend.dto.ListingCountsResponse
import com.waraqa.backend.dto.ListingResponseDto
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
            publishedAt = rs.getTimestamp("published_at")?.toLocalDateTime() ?: LocalDateTime.now(),
            status = rs.getString("status") ?: "active"
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
            .addValue("status", listing.status)

        if (listing.id == null) {
            val sql = """
                INSERT INTO listings (
                    book_id, publisher_id, description, price, listing_type, exchange_for,
                    condition, cover_image, images_url, views_count, saves_count,
                    university_id, faculty_id, major_id, published_at, status
                ) VALUES (
                    :bookId, :publisherId, :description, :price, :listingType, :exchangeFor,
                    :condition, :coverImage, :imagesUrl, :viewsCount, :savesCount,
                    :universityId, :facultyId, :majorId, :publishedAt, :status
                )
            """.trimIndent()
            val keyHolder = GeneratedKeyHolder()
            jdbcTemplate.update(sql, params, keyHolder, arrayOf("id"))
            val generatedId = keyHolder.key?.toLong() ?: throw RuntimeException("فشل استرجاع الـ ID")
            return listing.copy(id = generatedId)
        } else {
            val sql = """
                UPDATE listings SET
                    description = :description, price = :price, listing_type = :listingType,
                    exchange_for = :exchangeFor, condition = :condition, cover_image = :coverImage,
                    images_url = :imagesUrl, views_count = :viewsCount, saves_count = :savesCount,
                    university_id = :universityId, faculty_id = :facultyId, major_id = :majorId,
                    status = :status
                WHERE id = :id
            """.trimIndent()
            params.addValue("id", listing.id)
            jdbcTemplate.update(sql, params)
            return listing
        }
    }

    // =========================================================================
    // Shared DTO column list for all listing queries
    // =========================================================================

    private val dtoSelectColumns = """
        l.id,
        l.publisher_id,
        l.description,
        l.price,
        l.listing_type,
        l.exchange_for,
        l.condition,
        l.cover_image,
        l.university_id,
        l.faculty_id,
        l.major_id,
        l.published_at,
        l.views_count,
        l.saves_count,
        l.status,
        b.title AS book_title,
        b.author AS book_author,
        b.category AS book_category,
        u.name AS university_name,
        f.name AS faculty_name
    """.trimIndent()

    private val dtoJoins = """
        FROM listings l
        JOIN books b ON l.book_id = b.id
        LEFT JOIN universities u ON l.university_id = u.id
        LEFT JOIN faculties f ON l.faculty_id = f.id
    """.trimIndent()

    /**
     * Maps a ResultSet row to ListingResponseDto.
     * Shared by all DTO-returning queries.
     */
    private fun mapRowToDto(rs: java.sql.ResultSet, subType: String? = null): ListingResponseDto {
        val listingType = rs.getString("listing_type") ?: "for_sale"
        val publishedAt = rs.getTimestamp("published_at")?.toLocalDateTime() ?: LocalDateTime.now()
        return ListingResponseDto(
            id = rs.getLong("id"),
            title = rs.getString("book_title") ?: "",
            author = rs.getString("book_author"),
            description = rs.getString("description"),
            price = rs.getBigDecimal("price"),
            listingType = listingType,
            exchangeFor = rs.getString("exchange_for"),
            isExchange = listingType == "for_sale_and_exchange",
            condition = rs.getString("condition") ?: "good",
            category = rs.getString("book_category") ?: "general",
            subType = subType,
            image = rs.getString("cover_image") ?: "",
            publisherId = rs.getLong("publisher_id"),
            universityId = rs.getLong("university_id").takeUnless { rs.wasNull() },
            universityName = rs.getString("university_name"),
            facultyId = rs.getLong("faculty_id").takeUnless { rs.wasNull() },
            facultyName = rs.getString("faculty_name"),
            majorId = rs.getLong("major_id").takeUnless { rs.wasNull() },
            viewsCount = rs.getInt("views_count"),
            savesCount = rs.getInt("saves_count"),
            status = rs.getString("status") ?: "active",
            postedAt = publishedAt,
            createdAt = publishedAt
        )
    }

    // =========================================================================
    // Public feed queries (only active listings)
    // =========================================================================

    /**
     * Single unified query joining listings with books.
     * Only returns ACTIVE listings — sold listings are hidden from public feeds.
     */
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
    ): List<ListingResponseDto> {
        var sql = "SELECT $dtoSelectColumns $dtoJoins WHERE l.status = 'active'"

        val params = MapSqlParameterSource()

        if (!search.isNullOrBlank()) {
            sql += " AND (b.title ILIKE :search OR b.author ILIKE :search)"
            params.addValue("search", "%$search%")
        }
        if (!category.isNullOrBlank() && category != "all") {
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

        sql += when (sort) {
            "price_low", "price_asc" -> " ORDER BY l.price ASC"
            "price_high", "price_desc" -> " ORDER BY l.price DESC"
            "newest" -> " ORDER BY l.published_at DESC"
            else -> " ORDER BY l.views_count DESC"
        }

        sql += " LIMIT :limit OFFSET :offset"
        params.addValue("limit", limit)
        params.addValue("offset", offset)

        return jdbcTemplate.query(sql, params) { rs, _ -> mapRowToDto(rs, subType) }
    }

    /**
     * Fetches ACTIVE listings for a specific publisher — used for public profile page.
     * Sold listings are hidden from other users' views.
     */
    fun findListingsByPublisherId(
        publisherId: Long,
        offset: Int,
        limit: Int
    ): List<ListingResponseDto> {
        val sql = """
            SELECT $dtoSelectColumns $dtoJoins
            WHERE l.publisher_id = :publisherId AND l.status = 'active'
            ORDER BY l.published_at DESC
            LIMIT :limit OFFSET :offset
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("publisherId", publisherId)
            .addValue("limit", limit)
            .addValue("offset", offset)

        return jdbcTemplate.query(sql, params) { rs, _ -> mapRowToDto(rs) }
    }

    // =========================================================================
    // Owner-only queries (supports active/sold filtering)
    // =========================================================================

    /**
     * Fetches listings for the owner filtered by status (active or sold).
     * Used by the "My Listings" page tabs.
     */
    fun findOwnerListingsByStatus(
        publisherId: Long,
        status: String,
        offset: Int,
        limit: Int
    ): List<ListingResponseDto> {
        val sql = """
            SELECT $dtoSelectColumns $dtoJoins
            WHERE l.publisher_id = :publisherId AND l.status = :status
            ORDER BY l.published_at DESC
            LIMIT :limit OFFSET :offset
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("publisherId", publisherId)
            .addValue("status", status)
            .addValue("limit", limit)
            .addValue("offset", offset)

        return jdbcTemplate.query(sql, params) { rs, _ -> mapRowToDto(rs) }
    }

    /**
     * Returns active and sold counts for a publisher's listings.
     * Used for rendering tab labels like "Active (4)" and "Sold (3)".
     */
    fun countByPublisherAndStatus(publisherId: Long): ListingCountsResponse {
        val sql = """
            SELECT
                COUNT(*) FILTER (WHERE status = 'active') AS active_count,
                COUNT(*) FILTER (WHERE status = 'sold') AS sold_count
            FROM listings
            WHERE publisher_id = :publisherId
        """.trimIndent()

        val params = MapSqlParameterSource("publisherId", publisherId)

        return jdbcTemplate.queryForObject(sql, params) { rs, _ ->
            ListingCountsResponse(
                activeCount = rs.getInt("active_count"),
                soldCount = rs.getInt("sold_count")
            )
        }
    }

    // =========================================================================
    // Single-listing operations
    // =========================================================================

    /**
     * Finds a single listing as a DTO (with joined book/university details).
     * Used for the single-listing detail page.
     */
    fun findListingDtoById(listingId: Long): Optional<ListingResponseDto> {
        val sql = "SELECT $dtoSelectColumns $dtoJoins WHERE l.id = :id"
        val params = MapSqlParameterSource("id", listingId)
        val results = jdbcTemplate.query(sql, params) { rs, _ -> mapRowToDto(rs) }
        return Optional.ofNullable(results.firstOrNull())
    }

    /**
     * Finds a listing by ID (raw model, for ownership checks).
     */
    fun findById(listingId: Long): Optional<Listing> {
        val sql = "SELECT * FROM listings WHERE id = :id"
        val params = MapSqlParameterSource("id", listingId)
        val results = jdbcTemplate.query(sql, params, rowMapper)
        return Optional.ofNullable(results.firstOrNull())
    }

    /**
     * Updates the status of a listing (e.g. mark as sold).
     */
    fun updateStatus(listingId: Long, status: String): Boolean {
        val sql = "UPDATE listings SET status = :status WHERE id = :id"
        val params = MapSqlParameterSource()
            .addValue("id", listingId)
            .addValue("status", status)
        return jdbcTemplate.update(sql, params) > 0
    }

    /**
     * Deletes a listing by ID.
     */
    fun deleteById(listingId: Long): Boolean {
        val sql = "DELETE FROM listings WHERE id = :id"
        val params = MapSqlParameterSource("id", listingId)
        return jdbcTemplate.update(sql, params) > 0
    }

    /**
     * Updates listing fields for the edit form.
     * Only updates fields that are non-null.
     */
    fun updateListingFields(
        listingId: Long,
        description: String?,
        price: java.math.BigDecimal?,
        listingType: String?,
        exchangeFor: String?,
        condition: String?,
        coverImage: String?,
        imagesUrl: List<String>?,
        universityId: Long?,
        facultyId: Long?,
        majorId: Long?
    ): Boolean {
        val setClauses = mutableListOf<String>()
        val params = MapSqlParameterSource("id", listingId)

        if (description != null) { setClauses.add("description = :description"); params.addValue("description", description) }
        if (price != null) { setClauses.add("price = :price"); params.addValue("price", price) }
        if (listingType != null) { setClauses.add("listing_type = :listingType"); params.addValue("listingType", listingType) }
        if (exchangeFor != null) { setClauses.add("exchange_for = :exchangeFor"); params.addValue("exchangeFor", exchangeFor) }
        if (condition != null) { setClauses.add("condition = :condition"); params.addValue("condition", condition) }
        if (coverImage != null) { setClauses.add("cover_image = :coverImage"); params.addValue("coverImage", coverImage) }
        if (imagesUrl != null) { setClauses.add("images_url = :imagesUrl"); params.addValue("imagesUrl", imagesUrl.toTypedArray()) }

        // Academic fields — allow explicit null-setting via sentinel
        setClauses.add("university_id = :universityId"); params.addValue("universityId", universityId)
        setClauses.add("faculty_id = :facultyId"); params.addValue("facultyId", facultyId)
        setClauses.add("major_id = :majorId"); params.addValue("majorId", majorId)

        if (setClauses.isEmpty()) return false

        val sql = "UPDATE listings SET ${setClauses.joinToString(", ")} WHERE id = :id"
        return jdbcTemplate.update(sql, params) > 0
    }
}