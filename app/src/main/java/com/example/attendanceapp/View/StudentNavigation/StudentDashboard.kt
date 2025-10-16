package com.example.attendanceapp.View.StudentNavigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.attendanceapp.DataModels.Student

// Main Dashboard Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboard(
    student: Student?,
    onLogout: () -> Unit,
    onNavigateToAttendance: (String) -> Unit,
    onNavigateToTimeTable:(String)->Unit,
    onNavigateToPasswordChange:(String)-> Unit
) {
    if (student == null) {
        // Show loading while fetching student details
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Student Dashboard") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Student Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Student Avatar
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Student",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Roll No: ${student.rollNo}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = student.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Student Actions
            Text(
                text = "My Dashboard",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(studentQuickActions) { action ->
                    SQuickActionCard(action) { route ->
                        when (route) {
                            "my_attendance" -> student.rollNo.let { onNavigateToAttendance(it) }
                            "timetable" ->student.classId.let{onNavigateToTimeTable(it)}
                            "passwordChange" ->student.id.let{onNavigateToPasswordChange(it)}
                        }
                    }
                }
            }
        }
    }
}

// Quick Action Data Model
data class StudentQuickActionCard(
    val title: String,
    val icon: ImageVector,
    val route: String
)

// Quick Action List
val studentQuickActions = listOf(
    StudentQuickActionCard("My Attendance", Icons.Default.CheckCircle, "my_attendance"),
    StudentQuickActionCard("Timetable", Icons.Default.Schedule, "timetable"),
    StudentQuickActionCard("Change Password", Icons.Default.Lock, "passwordChange")
)

// Quick Action Card Composable
@Composable
fun SQuickActionCard(action: StudentQuickActionCard, onClick: (String) -> Unit) {
    Card(
        onClick = { onClick(action.route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
