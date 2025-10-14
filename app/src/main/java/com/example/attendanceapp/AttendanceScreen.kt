package com.example.attendanceapp

import android.R
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.attendanceapp.DataModels.Student
import com.example.attendanceapp.APIService.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AttendanceScreen(
    navController: NavController,
    classId: String,
    subjectId: String,
    teacherId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var recognitionResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImages = uris
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val uri = Uri.parse(
                MediaStore.Images.Media.insertImage(context.contentResolver, it, "temp", null)
            )
            selectedImages = selectedImages + uri
        }
    }

    // Fetch students by class
    LaunchedEffect(classId) {
        try {
            val response = RetrofitClient.apiService.getStudentsByClass(classId)
            if (response.isSuccessful) {
                students = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Attendance for Class: $classId, Subject: $subjectId")

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Pick Images") }
                Button(onClick = { cameraLauncher.launch(null) }) { Text("Capture Photo") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            selectedImages.forEach { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(150.dp).padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedImages.isNotEmpty()) {
                Button(onClick = {
                    scope.launch {
                        val allResults = mutableListOf<String>()
                        selectedImages.forEach { uri ->
                            try {
                                val file = FileUtils.getFileFromUri(context, uri)
                                val names: List<String> = ApiService.uploadImage(file, classId, subjectId)
                                allResults.addAll(names)
                            } catch (e: Exception) {
                                // Capture error but continue
                                e.printStackTrace()
                            }
                        }
                        // Remove duplicates
                        recognitionResults = allResults.distinct()
                    }
                }) {
                    Text("Send All to Server")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (recognitionResults.isNotEmpty()) {
                Text("Recognition Results:", style = MaterialTheme.typography.titleMedium)
                Text(recognitionResults.joinToString(", "))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigate with recognized names
            Button(onClick = {
                val namesParam = if (recognitionResults.isNotEmpty()) {
                    recognitionResults.joinToString(",")
                } else {
                    "None" // fallback if no recognized names
                }
                navController.navigate("take_attendance/$classId/$subjectId/$namesParam/$teacherId")
            }) {
                Text("Go to Attendance")
            }
        }
    }
}
