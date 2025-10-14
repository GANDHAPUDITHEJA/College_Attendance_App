package com.example.attendanceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.APIService.RetrofitClient
import com.example.attendanceapp.APIService.AttendanceApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TimeTableUiState(
    val isLoading: Boolean = false,
    val slots: List<AttendanceApiService.TimeSlotModel> = emptyList(),
    val error: String? = null
)

class TimeTableViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TimeTableUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchTimeTable(classId: String, day: String) {
        viewModelScope.launch {
            _uiState.value = TimeTableUiState(isLoading = true)
            try {
                val response = RetrofitClient.apiService.getTimeTableByClassAndDay(classId, day)
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val slots = response.body()!![0].slots.toMutableList()

                    // For each slot, fetch teacher name and subject name
                    slots.forEach { slot ->

                        // Fetch teacher name
                        if (slot.teacherId.isNotEmpty()) {
                            val teacherResp = RetrofitClient.apiService.getTeacherById(slot.teacherId)
                            slot.teacherName = if (teacherResp.isSuccessful && teacherResp.body() != null) {
                                teacherResp.body()!!.name
                            } else "Unknown"
                        }

                        // Fetch subject name
                        if (slot.subjectId.isNotEmpty()) {
                            val subjectResp = RetrofitClient.apiService.getSubjectById(slot.subjectId)
                            slot.subjectName = if (subjectResp.isSuccessful && subjectResp.body() != null) {
                                subjectResp.body()!!.subjectName
                            } else "Unknown"
                        }
                    }

                    _uiState.value = TimeTableUiState(slots = slots)
                } else {
                    _uiState.value = TimeTableUiState(error = "No timetable data for $day")
                }
            } catch (e: Exception) {
                _uiState.value = TimeTableUiState(error = e.message)
            }
        }
    }


}
