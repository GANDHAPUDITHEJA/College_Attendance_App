package com.example.attendanceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.attendanceapp.View.*
import com.example.attendanceapp.View.StudentNavigation.SeeStudentAttendanceScreen
import com.example.attendanceapp.View.StudentNavigation.StudentChangePasswordScreen
import com.example.attendanceapp.View.StudentNavigation.StudentDashboard
import com.example.attendanceapp.View.StudentNavigation.TimeTableScreen
import com.example.attendanceapp.View.TeacherNavigation.TakeAttendanceScreen
import com.example.attendanceapp.View.TeacherNavigation.TeacherDashboard
import com.example.attendanceapp.View.TeacherNavigation.TeacherTimeTableScreen
import com.example.attendanceapp.ViewModel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AttendanceAppTheme {
                val navController = rememberNavController()
                val userViewModel: UserViewModel = viewModel()

                NavHost(navController = navController, startDestination = "login") {

                    // Login Screen
                    composable("login") {
                        LoginScreen(
                            onTeacherLoginSuccess = { teacher ->
                                userViewModel.fetchTeacherById(teacher.email)
                                navController.navigate("teacher_dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onStudentLoginSuccess = { student ->
                                userViewModel.fetchStudentById(student.email)
                                navController.navigate("student_dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    // Teacher Dashboard
                    composable("teacher_dashboard") {
                        TeacherDashboard(
                            teacher = userViewModel.teacher.collectAsState().value,
                            onLogout = {
                                userViewModel.clearUserData()
                                navController.navigate("login") {
                                    popUpTo("teacher_dashboard") { inclusive = true }
                                }
                            },
                            onNavigateToAttendance = { teacherId ->
                                navController.navigate("teacher_subjects/$teacherId")  // Take Attendance
                            },
                            onNavigateToClasses = { teacherId ->
                                navController.navigate("view_assign_class/$teacherId") // My Classes
                            },
                            onNavigateToChangePassword = { teacherId ->
                                navController.navigate("change_password/$teacherId")   // ✅ Correct route
                            },
                            onNavigateToTimeTable = { teacherId ->
                                navController.navigate("teacher_time_table/$teacherId")   // ✅ Correct route
                            }
                        )
                    }



                    composable("student_dashboard") {
                        StudentDashboard(
                            student = userViewModel.student.collectAsState().value,
                            onLogout = {
                                userViewModel.clearUserData()
                                navController.navigate("login") {
                                    popUpTo("student_dashboard") { inclusive = true }
                                }
                            },
                            onNavigateToAttendance = { rollNo ->
                                navController.navigate("my_attendance/$rollNo")
                            },
                            onNavigateToTimeTable = { classId ->
                                navController.navigate("timetable/$classId")  // Take Attendance
                            },
                            onNavigateToPasswordChange = { studentId ->
                                navController.navigate("passwordChange/$studentId")  // Take Attendance
                            }
                        )
                    }
                    composable("timetable/{classId}") { backStackEntry ->
                        val classId = backStackEntry.arguments?.getString("classId") ?: ""
                        TimeTableScreen(classId)
                    }
                    composable("passwordChange/{studentId}") { backStackEntry ->
                        val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                        StudentChangePasswordScreen(studentId, navController)
                    }

                    composable("my_attendance/{rollNo}") { backStackEntry ->
                            val rollNo = backStackEntry.arguments?.getString("rollNo") ?: ""
                        SeeStudentAttendanceScreen(studentRollNo = rollNo)
                        }

                    // Teacher Subjects Screen
                    composable(
                        route = "teacher_subjects/{teacherId}",
                        arguments = listOf(navArgument("teacherId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val teacherId = backStackEntry.arguments?.getString("teacherId") ?: ""
                        TeacherSubjectsScreen(
                            teacherId = teacherId,
                            navController = navController
                        )
                    }

                    // Attendance Screen
                    composable(
                        route = "attendance/{classId}/{subjectId}/{teacherId}",
                        arguments = listOf(
                            navArgument("classId") { type = NavType.StringType },
                            navArgument("subjectId") { type = NavType.StringType },
                                    navArgument("teacherId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val classId = backStackEntry.arguments?.getString("classId") ?: ""
                        val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                        val teacherId = backStackEntry.arguments?.getString("teacherId") ?: ""
                        AttendanceScreen(
                            navController=navController,
                            classId = classId,
                            subjectId = subjectId,
                            teacherId=teacherId
                        )
                    }

                    composable(
                        route = "take_attendance/{classId}/{subjectId}/{recognizedNames}/{teacherId}",
                        arguments = listOf(
                            navArgument("classId") { type = NavType.StringType },
                            navArgument("subjectId") { type = NavType.StringType },
                            navArgument("recognizedNames") { type = NavType.StringType },
                            navArgument("teacherId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val classId = backStackEntry.arguments?.getString("classId") ?: ""
                        val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                        val recognizedNamesString = backStackEntry.arguments?.getString("recognizedNames") ?: ""
                        val recognizedNames = if (recognizedNamesString.isEmpty()) emptyList()
                        else recognizedNamesString.split(",")
                        val teacherId = backStackEntry.arguments?.getString("teacherId") ?: ""

                        TakeAttendanceScreen(
                            navController = navController,
                            classId = classId,
                            subjectId = subjectId,
                            recognizedNames = recognizedNames,
                            teacherId = teacherId
                        )
                    }
                    composable(
                        route = "view_assign_class/{teacherId}",
                        arguments = listOf(navArgument("teacherId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val teacherId = backStackEntry.arguments?.getString("teacherId") ?: ""
                        ViewAssignClass(teacherId = teacherId, navController = navController)
                    }
                    composable(
                        route = "teacher_time_table/{teacherId}",
                        arguments = listOf(navArgument("teacherId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val teacherId = backStackEntry.arguments?.getString("teacherId") ?: ""
                        TeacherTimeTableScreen(teacherId = teacherId)
                    }
                    composable(
                        route = "view_report/{classId}/{subjectId}",
                        arguments = listOf(
                            navArgument("classId") { type = NavType.StringType },
                            navArgument("subjectId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val classId = backStackEntry.arguments?.getString("classId") ?: ""
                        val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                        ViewReportScreen(classId = classId, subjectId = subjectId)
                    }
                    composable("change_password/{teacherId}") { backStackEntry ->
                        val teacherId = backStackEntry.arguments?.getString("teacherId") ?: ""
                        ChangePasswordScreen(teacherId = teacherId, navController = navController)
                    }


                }
            }
        }
    }
}

@Composable
fun AttendanceAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF2196F3),
            secondary = androidx.compose.ui.graphics.Color(0xFF03A9F4),
            tertiary = androidx.compose.ui.graphics.Color(0xFF00BCD4)
        ),
        content = content
    )
}
