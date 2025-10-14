package com.example.attendanceapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.APIService.RetrofitClient
import com.example.attendanceapp.DataModels.SubjectAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubjectAlertViewModel : ViewModel() {

    private val _subjectAlerts = MutableStateFlow<List<SubjectAlert>>(emptyList())
    val subjectAlerts: StateFlow<List<SubjectAlert>> = _subjectAlerts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchSubjectAlertsByTeacher(teacherId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getSubjectAlertsByTeacher(teacherId)
                if (response.isSuccessful) {
                    _subjectAlerts.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
