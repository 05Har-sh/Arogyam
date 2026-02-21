# Arogyam 🩺 - Smart Rural Health Monitoring System

**Arogyam** is an AI-powered early warning system designed for rural healthcare workers (ASHA workers) in Northeast India to detect disease outbreaks before they spread. Built with Spring Boot , PostgreSQL.

## ✨ Features

### 🏥 Core Features
- **Offline-First Mobile App** - Works without internet connectivity
- **Voice-Guided Interface** - Local language support (Hindi, Assamese, Bengali, English)
- **GPS-Tagged Health Data** - Precise disease mapping
- **Multi-Language Support** - Accessible to low-literacy users
- **Automatic Data Sync** - Seamless background synchronization

### 🤖 AI-Powered Early Detection
- **Symptom Pattern Analysis** - Detects clustering of similar symptoms
- **Water Quality Correlation** - Links contamination with health reports
- **Risk Score Calculation** - Automatic outbreak probability assessment
- **Predictive Analytics** - Early warning before outbreaks spread

### 📊 Real-Time Analytics & Dashboards
- **Role-Based Dashboards** - ASHA workers, Health Officials, Administrators
- **Interactive Visualizations** - Charts, graphs, heat maps
- **Real-Time Health Trends** - Live monitoring across villages
- **Automated Reports** - Government-ready exportable documentation

### 🚨 Multi-Channel Alert System
- **SMS Alerts** - Works on basic feature phones
- **WhatsApp Integration** - Broad reach to field workers
- **Email Notifications** - Detailed reports for officials
- **Audio Alerts** - Voice messages in local languages
- **Push Notifications** - Instant mobile alerts

### 🏗️ Enterprise Architecture
- **Spring Boot Microservices** - Scalable and maintainable
- **PostgreSQL Database** - Advanced analytics capabilities
- **JWT Authentication** - Role-based access control
- **Docker Containerization** - Easy deployment and scaling
- **RESTful APIs** - Comprehensive API documentation

## 🎯 Tech stack

Backend: Spring Boot 3.2.0, Java 17, Spring Security, JPA/Hibernate
Database: PostgreSQL 16
Mobile: Flutter 3.16.0 (Android/iOS)
AI/ML: Python-based prediction models (future integration)
Infrastructure: Docker, JWT, REST APIs

## 📖 API Documentation

### 🔐 Authentication

#### Login
POST /api/auth/login

text
**Request:**
```json
{
  "username": "asha_worker_001",
  "password": "password123"
}
Response (200):

json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "asha_worker_001",
    "role": "ASHA_WORKER",
    "villageId": 101
  }
}
```
Register New User
text
POST /api/auth/register
Request:

```json
{
  "username": "asha_worker_002",
  "password": "securepass123",
  "phoneNumber": "+919876543210",
  "role": "ASHA_WORKER",
  "villageId": 101,
  "district": "Kamrup"
}
```

🩺 Health Reports
Submit Health Report
text
POST /api/health-reports
Headers: Authorization: Bearer <jwt-token>
Request:

```json
{
  "villageId": 101,
  "patientAge": 35,
  "patientGender": "MALE",
  "symptoms": "fever,diarrhea,vomiting",
  "severity": "HIGH",
  "latitude": 26.1445,
  "longitude": 91.7362
}
Response (201):

json
{
  "success": true,
  "message": "Health report submitted successfully",
  "reportId": 123,
  "aiAnalysis": {
    "outbreakRisk": 0.85,
    "alertGenerated": true,
    "riskLevel": "CRITICAL"
  }
}
```
Get My Recent Reports
text
GET /api/health-reports/my-reports?limit=10&page=0
Response:

```json
{
  "reports": [
    {
      "id": 123,
      "villageName": "Village A",
      "symptoms": "fever,diarrhea",
      "severity": "HIGH",
      "submittedAt": "2026-02-21T14:30:00Z",
      "aiRiskScore": 0.85
    }
  ],
  "totalPages": 5,
  "currentPage": 0
}
```
📊 Dashboard & Analytics
Overview Statistics
text
GET /api/dashboard/stats/overview
Headers: Authorization: Bearer <jwt-token>
Response:

```json
{
  "totalReports": 1250,
  "activeAlerts": 3,
  "todayReports": 45,
  "outbreakRisk": 0.72,
  "waterQualityIndex": 0.65,
  "healthTrends": {
    "fever": 28,
    "diarrhea": 15,
    "vomiting": 8
  }
}
```
Health Trends Chart Data
text
GET /api/dashboard/charts/health-trends?days=7
Response:

```json
{
  "chartData": {
    "labels": ["2026-02-14", "2026-02-15", "2026-02-16", "..."],
    "datasets": {
      "fever": ,
      "diarrhea": ,
      "other": 
    }
  },
  "riskTrend": [0.3, 0.45, 0.6, 0.72, 0.8, 0.85, 0.9]
}
```
District-wise Analytics
text
GET /api/dashboard/stats/district/{districtId}
Response:

```json
{
  "district": "Kamrup",
  "villagesMonitored": 45,
  "totalPopulation": 125000,
  "reportsThisWeek": 320,
  "outbreakRisk": 0.68,
  "waterContamination": 0.42,
  "topSymptoms": ["fever", "diarrhea", "headache"]
}
```
🚨 Alerts
Get Active Alerts
text
GET /api/alerts/active?district=kamrup
Response:

```json
{
  "alerts": [
    {
      "id": 456,
      "title": "CRITICAL: Potential Outbreak in Village A",
      "message": "High fever cases detected with 85% risk score",
      "priority": "CRITICAL",
      "villageId": 101,
      "createdAt": "2026-02-21T14:45:00Z",
      "acknowledged": false
    }
  ]
}
```
Acknowledge Alert
text
POST /api/alerts/{alertId}/acknowledge
Request:

```json
{
  "actionTaken": "Team dispatched",
  "notes": "Water testing underway"
}
```
💧 Water Quality
Submit Water Test Results
text
POST /api/water-quality
Request:

```json
{
  "villageId": 101,
  "phLevel": 7.2,
  "turbidity": 5.1,
  "contaminants": ["E.coli", "Nitrate"],
  "qualityStatus": "CONTAMINATED",
  "latitude": 26.1445,
  "longitude": 91.7362
}
```
Recent Water Quality Trends
text
GET /api/water-quality/trends?villageId=101&days=30
🗺️ Villages & Locations
Get Villages by District
text
GET /api/villages?district=kamrup&limit=50
Response:

```json
{
  "villages": [
    {
      "id": 101,
      "name": "Village A",
      "district": "Kamrup",
      "population": 2500,
      "latitude": 26.1445,
      "longitude": 91.7362,
      "currentRisk": 0.85
    }
  ]
}
```
🔐 Authentication & Authorization
Roles & Permissions
text
ASHA_WORKER:
  ✅ Submit health reports
  ✅ View own village data
  ✅ Receive alerts

CHW (Community Health Worker):
  ✅ Submit reports (any village)
  ✅ View district data
  ✅ Acknowledge alerts

HEALTH_OFFICIAL:
  ✅ View district-wide analytics
  ✅ Manage alerts
  ✅ Generate reports

ADMIN:
  ✅ All permissions + user management
JWT Token Usage
text
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
🧪 Error Responses
401 Unauthorized
```json
{
  "timestamp": "2026-02-21T14:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "path": "/api/health-reports"
}
```
403 Forbidden
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Insufficient permissions for this action"
}
```

400 Bad Request
```json

{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid symptoms format. Use comma-separated values."
}
```
🛠️ Testing with Postman
Import Collection: Download arogyam-api.postman_collection.json

Set Base URL: http://localhost:8080/api

Authentication: Use Bearer token from login response


