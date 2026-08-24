package com.waraqa.backend.model

data class University(
    val id: Long?,
    val name: String
)

data class Faculty(
    val id: Long?,
    val name: String,
    val universityId: Long
)

data class Major(
    val id: Long?,
    val name: String,
    val facultyId: Long
)
