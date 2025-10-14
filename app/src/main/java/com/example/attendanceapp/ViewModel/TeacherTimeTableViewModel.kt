package com.example.attendanceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.APIService.AttendanceApiService
import com.example.attendanceapp.APIService.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherTimeTableViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherTimeTableUiState())
    val uiState: StateFlow<TeacherTimeTableUiState> = _uiState

    fun fetchTeacherTimeTable(teacherId: String, day: String) {
        viewModelScope.launch {
            _uiState.value = TeacherTimeTableUiState(isLoading = true)
            try {
                val response = RetrofitClient.apiService.getTimeTableByTeacherAndDay(teacherId, day)
                if (response.isSuccessful) {
                    _uiState.value = TeacherTimeTableUiState(
                        timeTables = response.body() ?: emptyList()
                    )
                } else {
                    _uiState.value = TeacherTimeTableUiState(
                        error = "Failed: ${response.code()} ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = TeacherTimeTableUiState(error = e.message)
            }
        }
    }
}

data class TeacherTimeTableUiState(
    val isLoading: Boolean = false,
    val timeTables: List<AttendanceApiService.TimeTableResponse> = emptyList(),
    val error: String? = null
)
