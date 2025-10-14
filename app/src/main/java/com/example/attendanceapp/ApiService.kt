package com.example.attendanceapp

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.util.concurrent.TimeUnit

object ApiService {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)   // ⏳ increase connection timeout
        .readTimeout(60, TimeUnit.SECONDS)      // ⏳ increase read timeout
        .writeTimeout(60, TimeUnit.SECONDS)     // ⏳ increase write timeout
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.155:8000/") // your PC IP
        .client(OkHttpClient.Builder().build())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(FaceApi::class.java)

    suspend fun uploadImage(file: File, classId: String, subjectId: String): List<String> {
        val requestFile = file.asRequestBody("image/*".toMediaType())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val classIdPart = MultipartBody.Part.createFormData("classId", classId)

        return api.recognize(body, classIdPart)
    }

}

interface FaceApi {
    @Multipart
    @POST("recognize/")
    suspend fun recognize(
        @Part file: MultipartBody.Part,
        @Part classId: MultipartBody.Part
    ): List<String>
}


data class FaceResponse(
    val results: List<Map<String, String>>
)

