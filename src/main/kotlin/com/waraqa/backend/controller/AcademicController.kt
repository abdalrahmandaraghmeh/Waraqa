package com.waraqa.backend.controller

import com.waraqa.backend.model.University
import com.waraqa.backend.model.Faculty
import com.waraqa.backend.model.Major
import com.waraqa.backend.repository.AcademicRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AcademicController(private val academicRepository: AcademicRepository) {

    @GetMapping("/universities")
    fun getUniversities(): List<University> {
        return academicRepository.findAllUniversities()
    }

    @GetMapping("/faculties")
    fun getFaculties(
        @RequestParam(name = "university_id", required = false) universityId: Long?
    ): List<Faculty> {
        return academicRepository.findFaculties(universityId)
    }

    @GetMapping("/majors")
    fun getMajors(
        @RequestParam(name = "faculty_id", required = false) facultyId: Long?
    ): List<Major> {
        return academicRepository.findMajors(facultyId)
    }
}
