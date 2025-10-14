package com.example.attendanceapp.View.StudentNavigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.attendanceapp.DataModels.AttendanceSummary
import com.example.attendanceapp.ViewModel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeeStudentAttendanceScreen(
    studentRollNo: String,
    studentViewModel: StudentViewModel = viewModel()
) {
    val attendanceSummary by studentViewModel.attendanceSummary.collectAsState()

    // Fetch data when the screen is composed
    LaunchedEffect(studentRollNo) {
        studentViewModel.getAttendanceSummary(studentRollNo)
    }

    // Calculate total attendance
    val totalPresent = attendanceSummary.sumOf { it.presentCount }
    val totalClasses = attendanceSummary.sumOf { it.totalCount }
    val totalPercentage = if (totalClasses > 0) (totalPresent * 100 / totalClasses) else 0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("My Attendance") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Total Attendance at top
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Total Attendance",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "$totalPresent / $totalClasses classes",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Overall Percentage: $totalPercentage%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subject-wise Attendance
            if (attendanceSummary.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No attendance data available.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(attendanceSummary) { summary ->
                        AttendanceCard(summary)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceCard(summary: AttendanceSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Subject: ${summary.subjectName}", style = MaterialTheme.typography.titleMedium)
            Text("Teacher: ${summary.teacherName}", style = MaterialTheme.typography.bodyMedium)
            Text("Present: ${summary.presentCount} / ${summary.totalCount}", style = MaterialTheme.typography.bodyMedium)
            Text("Percentage: ${summary.percentage}%", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
