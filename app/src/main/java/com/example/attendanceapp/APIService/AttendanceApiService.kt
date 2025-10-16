package com.example.attendanceapp.APIService

import com.example.attendanceapp.DataModels.AttendanceSummary
import com.example.attendanceapp.DataModels.ClassModel
import com.example.attendanceapp.DataModels.Student
import com.example.attendanceapp.DataModels.SubjectAlert
import com.example.attendanceapp.DataModels.SubjectModel
import com.example.attendanceapp.DataModels.Teacher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface AttendanceApiService {

    @FormUrlEncoded
    @POST("teachers/login")
    suspend fun teacherLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<Teacher>

    data class LoginRequest(
        val email: String,
        val password: String
    )
    @POST("students/login")
    suspend fun studentLogin(@Body request: LoginRequest): Response<Student>

    @GET("teachers/email/{email}")
    suspend fun getTeacherByEmail(@Path("email") email: String): Response<Teacher>
    @GET("teachers/{id}")
    suspend fun getTeacherById(@Path("id") id: String): Response<Teacher>


    @GET("students/email/{email}")
    suspend fun getStudentByEmail(@Path("email") email: String): Response<Student>

    @GET("subject-alerts/teacher/{teacherId}")
    suspend fun getSubjectAlertsByTeacher(@Path("teacherId") teacherId: String): retrofit2.Response<List<SubjectAlert>>

    @GET("classes/{classId}")
    suspend fun getClassById(@Path("classId") classId: String): retrofit2.Response<ClassModel>

    @GET("subjects/{subjectId}")
    suspend fun getSubjectById(@Path("subjectId") subjectId: String): retrofit2.Response<SubjectModel>
    @GET("students/class/{classId}")
    suspend fun getStudentsByClass(@Path("classId") classId: String): Response<List<Student>>

    @POST("students/rollNo/{rollNo}/subject/{subjectId}")
    suspend fun addAttendance(

        @Path("rollNo") rollNo: String,
        @Path("subjectId") subjectId: String,
        @Query("present") present: Boolean
    ): Response<String>

    data class ChangePasswordResponse(
        val status: String,
        val message: String
    )
    @FormUrlEncoded
    @POST("teachers/change-password/{id}")
    suspend fun changePassword(
        @Path("id") teacherId: String,
        @Field("oldPassword") oldPassword: String,
        @Field("newPassword") newPassword: String
    ): Response<ChangePasswordResponse>
    @GET("students/{rollNo}/attendance-summary")
    suspend fun getAttendanceSummary(
        @Path("rollNo") rollNo: String
    ): retrofit2.Response<List<AttendanceSummary>>

    @GET("timetables/class/{classId}/day/{day}")
    suspend fun getTimeTableByClassAndDay(
        @Path("classId") classId: String,
        @Path("day") day: String
    ): Response<List<TimeTableResponse>>

    // Data models for timetable
    data class TimeSlotModel(
        val startTime: String,
        val endTime: String,
        val subjectId: String,
        var subjectName: String? = null,   // add this
        val teacherId: String,
        var teacherName: String? = null,
        val activity: String?
    )


    data class TimeTableResponse(
        val id: String?,
        val classId: String,
        val day: String,
        val slots: List<TimeSlotModel>
    )
    @GET("timetables/teacher/{teacherId}/day/{day}")
    suspend fun getTimeTableByTeacherAndDay(
        @Path("teacherId") teacherId: String,
        @Path("day") day: String
    ): Response<List<TimeTableResponse>>

    @FormUrlEncoded
    @POST("students/change-password/{id}")
    suspend fun changeStudentPassword(
        @Path("id") studentId: String,
        @Field("oldPassword") oldPassword: String,
        @Field("newPassword") newPassword: String
    ): retrofit2.Response<ChangePasswordResponse>

}
object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.155:8088/api/" // For emulator

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: AttendanceApiService = retrofit.create(AttendanceApiService::class.java)
}