package com.waraqa.backend.repository

import com.waraqa.backend.model.University
import com.waraqa.backend.model.Faculty
import com.waraqa.backend.model.Major
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class AcademicRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    private val universityMapper = RowMapper { rs, _ ->
        University(
            id = rs.getLong("id"),
            name = rs.getString("name")
        )
    }

    private val facultyMapper = RowMapper { rs, _ ->
        Faculty(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            universityId = rs.getLong("university_id")
        )
    }

    private val majorMapper = RowMapper { rs, _ ->
        Major(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            facultyId = rs.getLong("faculty_id")
        )
    }

    fun findAllUniversities(): List<University> {
        val sql = "SELECT * FROM universities ORDER BY name ASC"
        return jdbcTemplate.query(sql, universityMapper)
    }

    fun findFaculties(universityId: Long?): List<Faculty> {
        return if (universityId != null) {
            val sql = "SELECT * FROM faculties WHERE university_id = :universityId ORDER BY name ASC"
            val params = MapSqlParameterSource("universityId", universityId)
            jdbcTemplate.query(sql, params, facultyMapper)
        } else {
            val sql = "SELECT * FROM faculties ORDER BY name ASC"
            jdbcTemplate.query(sql, facultyMapper)
        }
    }

    fun findMajors(facultyId: Long?): List<Major> {
        return if (facultyId != null) {
            val sql = "SELECT * FROM majors WHERE faculty_id = :facultyId ORDER BY name ASC"
            val params = MapSqlParameterSource("facultyId", facultyId)
            jdbcTemplate.query(sql, params, majorMapper)
        } else {
            val sql = "SELECT * FROM majors ORDER BY name ASC"
            jdbcTemplate.query(sql, majorMapper)
        }
    }
}
