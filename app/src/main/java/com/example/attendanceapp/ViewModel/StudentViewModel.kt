package com.example.attendanceapp.ViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.DataModels.AttendanceSummary
import com.example.attendanceapp.APIService.AttendanceApiService
import com.example.attendanceapp.APIService.RetrofitClient

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StudentViewModel : ViewModel() {

    private val _attendanceSummary = MutableStateFlow<List<AttendanceSummary>>(emptyList())
    val attendanceSummary: StateFlow<List<AttendanceSummary>> = _attendanceSummary


    // Fetch attendance summary from API
    fun getAttendanceSummary(rollNo: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAttendanceSummary(rollNo)
                if (response.isSuccessful) {
                    _attendanceSummary.value = response.body() ?: emptyList()
                } else {
                    _attendanceSummary.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _attendanceSummary.value = emptyList()
            }
        }
    }
}
