package com.example.attendanceapp.View.StudentNavigation

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
import com.example.attendanceapp.ViewModel.TimeTableViewModel
import com.example.attendanceapp.APIService.AttendanceApiService
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeTableScreen(
    classId: String,
    viewModel: TimeTableViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    // Auto-detect current day (e.g., FRI)
    val currentDay = remember {
        LocalDate.now()
            .dayOfWeek
            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            .uppercase(Locale.ENGLISH)
    }
    var selectedDay by remember { mutableStateOf(currentDay) }

    LaunchedEffect(selectedDay) {
        coroutineScope.launch {
            viewModel.fetchTimeTable(classId, selectedDay)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Timetable - $selectedDay") },
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
            Text(text=classId)
            DaySelector(selectedDay) { newDay ->
                selectedDay = newDay
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                uiState.error != null -> Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)

                uiState.slots.isEmpty() -> Text("No classes found for $selectedDay")

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.slots) { slot ->
                        TimeSlotCard(slot)
                    }
                }
            }
        }
    }
}

// Dropdown for day selection
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

// Timetable slot UI card
@Composable
fun TimeSlotCard(slot: AttendanceApiService.TimeSlotModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Time
            Text(
                text = "${slot.startTime} - ${slot.endTime}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )

            // Subject and Teacher
            Text(
                text = "${slot.subjectName ?: slot.subjectId} - ${slot.teacherName ?: slot.teacherId}",
                style = MaterialTheme.typography.bodyLarge
            )

            // Activity (optional)
            slot.activity?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = "Activity: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

