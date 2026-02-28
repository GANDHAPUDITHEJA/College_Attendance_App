

📌 Smart Attendance System Using Face Recognition
📖 Overview

The Smart Attendance System is an automated classroom attendance solution that uses Face Recognition Technology to detect and mark student attendance efficiently.

The system integrates:

📱 Android Application (Frontend)

🧠 Face Recognition using face_recognition (dlib)

🖥 Spring Boot Backend

🗄 MS SQL Server Database

This solution reduces manual effort, saves classroom time, and improves accuracy.

❗ Problems in Traditional Attendance System

Traditional attendance systems face several issues:

⏳ Consumes valuable classroom time

✍ Manual data entry leads to human errors

👥 Proxy attendance is possible

📂 Difficult to manage large classroom records

📝 Paper-based records are inefficient

🔄 Time-consuming data maintenance

💡 Proposed Solution

The Smart Attendance System solves these problems by:

📸 Capturing classroom images via Android app

🧠 Automatically recognizing multiple student faces

📊 Marking attendance digitally

✏ Providing manual correction option

🗃 Storing attendance securely in database

🛠 Technologies Used
📱 Frontend

Android

Jetpack Compose

🧠 Face Recognition

Python face_recognition library

Built on dlib deep learning model

🖥 Backend

Spring Boot

🗄 Database

MS SQL Server

👥 Types of Users & Functionalities
👩‍🎓 Student

View attendance records

View assigned periods

Check timetable

👨‍🏫 Teacher

Take attendance using face recognition

Manual attendance option

View attendance of assigned classes

View student records

Check timetable

🔄 Workflow of the System
1️⃣ Teacher Login

Teacher logs into Android application.

Selects assigned class.

2️⃣ Model Preparation

Student images of selected class are loaded into the face recognition model.

3️⃣ Capture Image

Teacher captures classroom image (can take multiple images).

4️⃣ Backend Processing

Image is sent to backend.

Face detection is performed.

Face embeddings are generated.

Faces are matched with stored dataset.

5️⃣ Attendance Marking

Recognized student roll numbers are returned.

Attendance is marked automatically.

Manual correction is allowed if needed.

🚀 Advantages

✅ Saves classroom time

✅ Reduces manual errors

✅ Minimizes proxy attendance

✅ Digital record management

✅ Scalable for large classrooms

✅ AI-powered modern solution
