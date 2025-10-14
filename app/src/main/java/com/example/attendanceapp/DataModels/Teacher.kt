package com.example.attendanceapp.DataModels

data class Teacher(
    val id: String? = null,
    val name: String,
    val email: String,
    val password: String,
    val imageUrl: String? = null
)