package com.example.attendanceapp.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.attendanceapp.APIService.RetrofitClient
import com.example.attendanceapp.DataModels.Student
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewReportScreen(
    classId: String,
    subjectId: String
) {
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // ✅ Fetch students
    LaunchedEffect(classId, subjectId) {
        scope.launch {
            try {
                val response = RetrofitClient.apiService.getStudentsByClass(classId)
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Attendance Report") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                students.isEmpty() -> Text(
                    "No students found",
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // ✅ Header Row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Roll", modifier = Modifier.weight(1.6f))
                            Text("Name", modifier = Modifier.weight(2f))
                            Text("Total", modifier = Modifier.weight(0.8f))
                            Text("Present", modifier = Modifier.weight(0.8f))
                            Text("%", modifier = Modifier.weight(0.8f))
                        }
                        Divider(Modifier.padding(vertical = 4.dp))
                    }

                    // ✅ Data Rows
                    items(students) { student ->
                        val attendanceList = student.attendanceMap[subjectId] ?: emptyList()
                        val totalClasses = attendanceList.size
                        val presentCount = attendanceList.count { it.present }
                        val percentage =
                            if (totalClasses > 0) (presentCount * 100 / totalClasses) else 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(student.rollNo, modifier = Modifier.weight(1.6f))
                            Text(student.name, modifier = Modifier.weight(2f))
                            Text(totalClasses.toString(), modifier = Modifier.weight(0.8f))
                            Text(presentCount.toString(), modifier = Modifier.weight(0.8f))
                            Text("$percentage%", modifier = Modifier.weight(0.8f))
                        }
                        Divider(Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }
    }
}
