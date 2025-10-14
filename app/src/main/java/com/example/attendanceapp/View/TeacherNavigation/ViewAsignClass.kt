package com.example.attendanceapp.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.attendanceapp.ViewModel.SubjectAlertViewModel
import com.example.attendanceapp.DataModels.SubjectAlert
import com.example.attendanceapp.APIService.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAssignClass(
    teacherId: String,
    navController: NavController,
    viewModel: SubjectAlertViewModel = viewModel()
) {
    val alerts by viewModel.subjectAlerts.collectAsState()
    val loading by viewModel.isLoading.collectAsState()

    LaunchedEffect(teacherId) {
        viewModel.fetchSubjectAlertsByTeacher(teacherId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("My Classes") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                alerts.isEmpty() -> Text(
                    "No classes assigned",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alerts) { alert ->
                        ClassSubjectInfoCard(
                            alert = alert,
                            onViewReport = { classId, subjectId ->
                                navController.navigate("view_report/$classId/$subjectId")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClassSubjectInfoCard(
    alert: SubjectAlert,
    onViewReport: (String, String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var className by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var subjectName by remember { mutableStateOf("") }

    LaunchedEffect(alert.classId, alert.subjectId) {
        scope.launch {
            try {
                RetrofitClient.apiService.getClassById(alert.classId).body()?.let {
                    className = it.className
                    section = it.section
                    semester = it.semester
                }

                RetrofitClient.apiService.getSubjectById(alert.subjectId).body()?.let {
                    subjectName = it.subjectName
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Class: $className")
            Text(text = "Section: $section")
            Text(text = "Semester: $semester")
            Text(text = "Subject: $subjectName")

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onViewReport(alert.classId, alert.subjectId) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("View Report")
            }
        }
    }
}
