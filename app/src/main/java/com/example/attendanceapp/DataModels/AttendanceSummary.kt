package com.example.attendanceapp.DataModels

data class AttendanceSummary(
    val subjectId: String,
    val subjectName: String,
    val teacherName: String,
    val presentCount: Int,
    val totalCount: Int,
    val percentage: Int
)
