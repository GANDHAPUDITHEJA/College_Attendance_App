package com.example.attendanceapp.View.TeacherNavigation

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
import com.example.attendanceapp.DataModels.Teacher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboard(
    teacher: Teacher?,
    onLogout: () -> Unit,
    onNavigateToAttendance: (String) -> Unit,
    onNavigateToClasses: (String) -> Unit,
    onNavigateToChangePassword: (String) -> Unit,
    onNavigateToTimeTable:(String)-> Unit// ✅ Added
) {
    if (teacher == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Teacher Dashboard") },
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
            // 🟢 Teacher Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            imageVector = Icons.Default.Person,
                            contentDescription = "Teacher",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = teacher.name, style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = teacher.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                items(teacherQuickActions) { action ->
                    QuickActionCard(action) { route ->
                        when (route) {
                            "take_attendance" -> teacher?.id?.let { onNavigateToAttendance(it) }
                            "view_assign_class" -> teacher?.id?.let { onNavigateToClasses(it) }
                            "change_password" -> teacher?.id?.let { onNavigateToChangePassword(it) }
                            "teacher_time_table" -> teacher?.id?.let { onNavigateToTimeTable(it) }// ✅ Fixed

                        }
                    }
                }
            }
        }
    }
}

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val teacherQuickActions = listOf(
    QuickAction("Take Attendance", Icons.Default.CheckCircle, "take_attendance"),
    QuickAction("My Classes", Icons.Default.Class, "view_assign_class"),
    QuickAction("Time Table", Icons.Default.TableView, "teacher_time_table"),
    QuickAction("Change Password", Icons.Default.Lock, "change_password")
)

@Composable
fun QuickActionCard(action: QuickAction, onClick: (String) -> Unit) {
    Card(
        onClick = { onClick(action.route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(4.dp)
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
            Text(action.title, textAlign = TextAlign.Center)
        }
    }
}
