package com.example.attendanceapp.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.attendanceapp.DataModels.LoginResponse
import com.example.attendanceapp.ViewModel.AuthViewModel
import com.example.attendanceapp.DataModels.Teacher
import com.example.attendanceapp.DataModels.Student
import com.example.attendanceapp.ViewModel.UserType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onTeacherLoginSuccess: (Teacher) -> Unit,
    onStudentLoginSuccess: (Student) -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val userType by viewModel.userType.collectAsState()
    val teacherLoginState by viewModel.teacherLoginState.collectAsState()
    val studentLoginState by viewModel.studentLoginState.collectAsState()

    val scrollState = rememberScrollState()

    // Handle login responses
    LaunchedEffect(teacherLoginState) {
        when (teacherLoginState) {
            is LoginResponse.Success -> {
                onTeacherLoginSuccess((teacherLoginState as LoginResponse.Success).data)
            }
            else -> {}
        }
    }

    LaunchedEffect(studentLoginState) {
        when (studentLoginState) {
            is LoginResponse.Success -> {
                onStudentLoginSuccess((studentLoginState as LoginResponse.Success).data)
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Header
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector =Icons.Default.School,
                contentDescription = "App Logo",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Attendance App",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // User Type Selection
        UserTypeSelector(
            selectedType = userType,
            onTypeSelected = { viewModel.setUserType(it) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Password")
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = {
                        when (userType) {
                            UserType.TEACHER -> viewModel.loginTeacher(email, password)
                            UserType.STUDENT -> viewModel.loginStudent(email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = email.isNotBlank() && password.isNotBlank() &&
                            (teacherLoginState !is LoginResponse.Loading &&
                                    studentLoginState !is LoginResponse.Loading)
                ) {
                    if ((userType == UserType.TEACHER && teacherLoginState is LoginResponse.Loading) ||
                        (userType == UserType.STUDENT && studentLoginState is LoginResponse.Loading)) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        // Error Message
        when {
            teacherLoginState is LoginResponse.Error && userType == UserType.TEACHER -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (teacherLoginState as LoginResponse.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            studentLoginState is LoginResponse.Error && userType == UserType.STUDENT -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (studentLoginState as LoginResponse.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun UserTypeSelector(
    selectedType: UserType,
    onTypeSelected: (UserType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            UserTypeChip(
                type = UserType.TEACHER,
                isSelected = selectedType == UserType.TEACHER,
                onSelected = onTypeSelected,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            UserTypeChip(
                type = UserType.STUDENT,
                isSelected = selectedType == UserType.STUDENT,
                onSelected = onTypeSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTypeChip(
    type: UserType,
    isSelected: Boolean,
    onSelected: (UserType) -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, text) = when (type) {
        UserType.TEACHER -> Pair(Icons.Default.Person, "Teacher")
        UserType.STUDENT -> Pair(Icons.Default.School, "Student")
    }

    FilterChip(
        selected = isSelected,
        onClick = { onSelected(type) },
        label = { Text(text) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = modifier
    )
}