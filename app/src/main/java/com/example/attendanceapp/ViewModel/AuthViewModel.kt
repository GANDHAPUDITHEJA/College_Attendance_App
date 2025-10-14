package com.example.attendanceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.APIService.AttendanceApiService.LoginRequest
import com.example.attendanceapp.APIService.RetrofitClient
import com.example.attendanceapp.DataModels.LoginResponse
import com.example.attendanceapp.DataModels.Student
import com.example.attendanceapp.DataModels.Teacher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _teacherLoginState = MutableStateFlow<LoginResponse<Teacher>?>(null)
    val teacherLoginState: StateFlow<LoginResponse<Teacher>?> = _teacherLoginState.asStateFlow()

    private val _studentLoginState = MutableStateFlow<LoginResponse<Student>?>(null)
    val studentLoginState: StateFlow<LoginResponse<Student>?> = _studentLoginState.asStateFlow()

    private val _userType = MutableStateFlow<UserType>(UserType.TEACHER)
    val userType: StateFlow<UserType> = _userType.asStateFlow()

    fun setUserType(type: UserType) {
        _userType.value = type
    }

    fun loginTeacher(email: String, password: String) {
        viewModelScope.launch {
            _teacherLoginState.value = LoginResponse.Loading
            try {
                val response = RetrofitClient.apiService.teacherLogin(email, password)
                if (response.isSuccessful && response.body() != null) {
                    _teacherLoginState.value = LoginResponse.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    _teacherLoginState.value =
                        LoginResponse.Error("Login failed: ${errorBody ?: response.message()}")
                }
            } catch (e: Exception) {
                _teacherLoginState.value = LoginResponse.Error("Network error: ${e.message}")
            }
        }
    }

    fun loginStudent(email: String, password: String) {
        viewModelScope.launch {
            _studentLoginState.value = LoginResponse.Loading
            try {
                // Step 1: Login and get student id
                val loginRequest = LoginRequest(email = email, password = password)
                val loginResponse = RetrofitClient.apiService.studentLogin(loginRequest)
                if (loginResponse.isSuccessful && loginResponse.body() != null) {
                    val studentId = loginResponse.body()!!.rollNo

                    // Step 2: Fetch full student details using email (or id)
                    val detailResponse = RetrofitClient.apiService.getStudentByEmail(email)
                    if (detailResponse.isSuccessful && detailResponse.body() != null) {
                        _studentLoginState.value = LoginResponse.Success(detailResponse.body()!!)
                    } else {
                        val errorBody = detailResponse.errorBody()?.string()
                        _studentLoginState.value =
                            LoginResponse.Error("Failed to fetch student details: ${errorBody ?: detailResponse.message()}")
                    }
                } else {
                    val errorBody = loginResponse.errorBody()?.string()
                    _studentLoginState.value =
                        LoginResponse.Error("Login failed: ${errorBody ?: loginResponse.message()}")
                }
            } catch (e: Exception) {
                _studentLoginState.value = LoginResponse.Error("Network error: ${e.message}")
            }
        }
    }

    fun resetLoginState() {
        _teacherLoginState.value = null
        _studentLoginState.value = null
    }
}

enum class UserType {
    TEACHER, STUDENT
}
