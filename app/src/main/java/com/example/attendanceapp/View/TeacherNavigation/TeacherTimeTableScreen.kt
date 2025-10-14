package com.example.attendanceapp.View.TeacherNavigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.attendanceapp.APIService.AttendanceApiService
import com.example.attendanceapp.ViewModel.TeacherTimeTableViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherTimeTableScreen(
    teacherId: String,
    viewModel: TeacherTimeTableViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    val currentDay = remember {
        LocalDate.now().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            .uppercase(Locale.ENGLISH)
    }
    var selectedDay by remember { mutableStateOf(currentDay) }

    LaunchedEffect(selectedDay) {
        coroutineScope.launch {
            viewModel.fetchTeacherTimeTable(teacherId, selectedDay)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Teacher Timetable - $selectedDay") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Teacher ID: $teacherId", fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))

            DaySelector(selectedDay) { newDay ->
                selectedDay = newDay
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                uiState.error != null -> Text(
                    "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error
                )

                uiState.timeTables.isEmpty() -> Text("No classes found for $selectedDay")

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.timeTables) { timetable ->
                        ClassTimeTableCard(timetable)
                    }
                }
            }
        }
    }
}

@Composable
fun ClassTimeTableCard(timetable: AttendanceApiService.TimeTableResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Class: ${timetable.classId}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            timetable.slots.forEach { slot ->
                Text(
                    text = "${slot.startTime} - ${slot.endTime}",
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Subject: ${slot.subjectId}")
                slot.activity?.takeIf { it.isNotBlank() }?.let {
                    Text("Activity: $it", color = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DaySelector(selectedDay: String, onDaySelected: (String) -> Unit) {
    val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT")
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Day: $selectedDay")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            days.forEach { day ->
                DropdownMenuItem(
                    text = { Text(day) },
                    onClick = {
                        onDaySelected(day)
                        expanded = false
                    }
                )
            }
        }
    }
}
