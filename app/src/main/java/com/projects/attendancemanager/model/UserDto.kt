package com.projects.attendancemanager.model

data class UserDto(
    val _id: String,
    val username: String,
    val email: String,
    val password: String,
    val role: String
)

