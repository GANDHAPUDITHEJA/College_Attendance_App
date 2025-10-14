package com.example.attendanceapp.View.TeacherNavigation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.attendanceapp.APIService.RetrofitClient
import com.example.attendanceapp.DataModels.Student
import com.example.attendanceapp.ViewModel.AttendanceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeAttendanceScreen(
    navController: NavController,
    classId: String,
    subjectId: String,
    recognizedNames: List<String>,
    teacherId: String,
    viewModel: AttendanceViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Load students from API
    LaunchedEffect(classId) {
        try {
            val response = RetrofitClient.apiService.getStudentsByClass(classId)
            if (response.isSuccessful) {
                students = response.body()?.map { student ->
                    val isPresent = recognizedNames.any {
                        it.equals(student.name, true) || it.equals(student.rollNo, true)
                    }
                    student.copy(tempPresent = isPresent) // temporary UI flag
                } ?: emptyList()
            } else {
                Toast.makeText(context, "Failed to load students", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error loading students", Toast.LENGTH_SHORT).show()
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Roll No", modifier = Modifier.weight(1.2f))
            Text("Name", modifier = Modifier.weight(2f))
            Text("Present", modifier = Modifier.weight(1f))
        }
        Divider(color = Color.Black, thickness = 1.dp)

        // Student List
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (students.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No students found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                items(students) { student ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(student.rollNo, modifier = Modifier.weight(1.2f))
                        Text(student.name, modifier = Modifier.weight(2f).padding(start = 12.dp))
                        Checkbox(
                            checked = student.tempPresent,
                            onCheckedChange = { checked ->
                                students = students.map {
                                    if (it.rollNo == student.rollNo) it.copy(tempPresent = checked)
                                    else it
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { students = students.map { it.copy(tempPresent = true) } }) {
                Text("All Present")
            }
            Button(onClick = { students = students.map { it.copy(tempPresent = false) } }) {
                Text("All Absent")
            }
            Button(onClick = { showConfirmDialog = true }) {
                Text("Submit")
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Attendance Submission") },
            text = {
                val absentList = students.filter { !it.tempPresent }
                if (absentList.isEmpty()) {
                    Text("All students are present.\nDo you want to submit?")
                } else {
                    val absentText = absentList.joinToString("\n") { "${it.rollNo} - ${it.name}" }
                    Text("The following students are absent:\n$absentText\n\nDo you want to submit?")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    scope.launch {
                        viewModel.submitAttendance(subjectId, students) { successCount, total ->
                            Toast.makeText(
                                context,
                                "Attendance submitted",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                            navController.popBackStack()// remove only the TakeAttendance screen
                        }
                    }
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
