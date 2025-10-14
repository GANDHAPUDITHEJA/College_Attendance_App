package com.example.attendanceapp.DataModels

sealed class LoginResponse<out T> {
    object Loading : LoginResponse<Nothing>()
    data class Success<out T>(val data: T) : LoginResponse<T>()
    data class Error(val message: String) : LoginResponse<Nothing>()
}