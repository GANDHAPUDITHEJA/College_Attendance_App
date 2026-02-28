# 📌 Smart Attendance System Using Face Recognition

## 📖 Overview

The **Smart Attendance System** is an automated classroom attendance solution that leverages **Face Recognition Technology** to efficiently detect and mark student attendance.

The system integrates:

- 📱 **Android Application (Frontend)**
- 🧠 **Face Recognition using `face_recognition` (dlib)**
- 🖥 **Spring Boot Backend**
- 🗄 **MS SQL Server Database**

This solution reduces manual effort, saves classroom time, and improves accuracy through AI-driven automation.

---

## ❗ Problems in Traditional Attendance System

Traditional attendance systems face several challenges:

- ⏳ Consumes valuable classroom time  
- ✍ Manual data entry leads to human errors  
- 👥 Proxy attendance is possible  
- 📂 Difficult to manage large classroom records  
- 📝 Paper-based records are inefficient  
- 🔄 Time-consuming data maintenance  

---

## 💡 Proposed Solution

The Smart Attendance System addresses these problems by:

- 📸 Capturing classroom images via Android application  
- 🧠 Automatically recognizing multiple student faces  
- 📊 Digitally marking attendance  
- ✏ Providing manual correction options  
- 🗃 Securely storing attendance records in a database  

---

## 🛠 Technologies Used

### 📱 Frontend
- Android  
- Jetpack Compose  

### 🧠 Face Recognition
- Python `face_recognition` library  
- Built on `dlib` deep learning model  

### 🖥 Backend
- Spring Boot  

### 🗄 Database
- MS SQL Server  

---

## 👥 Types of Users & Functionalities

### 👩‍🎓 Student
- View attendance records  
- View assigned periods  
- Check timetable  

### 👨‍🏫 Teacher
- Take attendance using face recognition  
- Manual attendance option  
- View attendance of assigned classes  
- View student records  
- Check timetable  

---

## 🔄 Workflow of the System

### 1️⃣ Teacher Login
- Teacher logs into the Android application.
- Selects assigned class.

### 2️⃣ Model Preparation
- Student images of the selected class are loaded into the face recognition model.

### 3️⃣ Capture Image
- Teacher captures classroom image (multiple images supported).

### 4️⃣ Backend Processing
- Image is sent to backend.
- Face detection is performed.
- Face embeddings are generated.
- Faces are matched with stored dataset.

### 5️⃣ Attendance Marking
- Recognized student roll numbers are returned.
- Attendance is marked automatically.
- Manual correction is allowed if required.

---

## 🚀 Advantages

- ✅ Saves classroom time  
- ✅ Reduces manual errors  
- ✅ Minimizes proxy attendance  
- ✅ Digital record management  
- ✅ Scalable for large classrooms  
- ✅ AI-powered modern solution  

---

## 📌 Conclusion

The Smart Attendance System demonstrates how **Mobile Applications, Deep Learning, and Backend Technologies** can be integrated to modernize classroom attendance management.

By automating attendance tracking, the system improves efficiency, ensures accuracy, and provides a scalable digital solution for educational institutions.
