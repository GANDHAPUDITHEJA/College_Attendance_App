package com.example.attendanceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.APIService.RetrofitClient
import com.example.attendanceapp.DataModels.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AttendanceViewModel : ViewModel() {

    /**
     * Submit attendance for all students in a subject.
     * Uses rollNo instead of studentId.
     */
    fun submitAttendance(
        subjectId: String,
        students: List<Student>,
        onResult: ((successCount: Int, total: Int) -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var successCount = 0
            students.forEach { student ->
                try {
                    val response = RetrofitClient.apiService.addAttendance(
                        rollNo = student.rollNo,
                        subjectId = subjectId,
                        present = student.tempPresent
                    )

                    // Check if response is successful and body is not null
                    if (response.isSuccessful && response.body() != null) {
                        successCount++
                    } else {
                        // Log error response
                        println("Failed to submit attendance for ${student.rollNo}: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Exception for ${student.rollNo}: ${e.message}")
                }
            }
            // Switch to Main thread for callback to avoid UI issues
            withContext(Dispatchers.Main) {
                onResult?.invoke(successCount, students.size)
            }
        }
    }
}