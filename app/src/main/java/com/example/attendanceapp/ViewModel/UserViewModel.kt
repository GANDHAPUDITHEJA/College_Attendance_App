package com.example.attendanceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.APIService.RetrofitClient
import com.example.attendanceapp.DataModels.Student
import com.example.attendanceapp.DataModels.Teacher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel : ViewModel() {

    private val _teacher = MutableStateFlow<Teacher?>(null)
    val teacher: StateFlow<Teacher?> = _teacher.asStateFlow()

    private val _student = MutableStateFlow<Student?>(null)
    val student: StateFlow<Student?> = _student.asStateFlow()

    fun fetchTeacherById(id: String) {
        viewModelScope.launch {
            try {
                val response: Response<Teacher> = RetrofitClient.apiService.getTeacherByEmail(id)
                if (response.isSuccessful) {
                    _teacher.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchStudentById(rollNo: String) {
        viewModelScope.launch {
            try {
                val response: Response<Student> = RetrofitClient.apiService.getStudentByEmail(rollNo)
                if (response.isSuccessful) {
                    _student.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearUserData() {
        _teacher.value = null
        _student.value = null
    }
}
