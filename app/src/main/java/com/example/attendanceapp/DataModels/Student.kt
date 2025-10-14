package com.example.attendanceapp.DataModels

data class AttendanceRecord(
    val date: String,
    val present: Boolean
)

data class Student(
    val id: String,
    val rollNo: String,
    val name: String,
    val email: String,
    val password: String,
    val imageUrl: String? = null,
    val classId: String,
    val attendanceMap: Map<String, List<AttendanceRecord>> = emptyMap(),
    val tempPresent: Boolean = false
)
