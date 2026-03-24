# FlexApp

> Web-based flex time tracking system — built with Java Spring Boot, PostgreSQL, and React.

---

## Overview

FlexApp is a full-stack web application for tracking working hours, flexible schedules, and flex balance. The system supports both employees and administrators and provides secure time tracking with role-based access control.

---

## Architecture

```
Frontend (React + Vite)
        │
        ▼
Spring Boot REST API
        │
        ▼
  PostgreSQL Database
```

---

## Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Backend     | Spring Boot (Java 21+)              |
| Persistence | Spring Data JPA / Hibernate         |
| Database    | PostgreSQL                          |
| Security    | Spring Security (HTTP Basic)        |
| Frontend    | React + Vite                        |
| Build Tool  | Maven + npm                         |

---

## Features

### Time Tracking
- Check in / Check out
- Lunch tracking (out/in)
- Manual time entries
- Daily worked minutes calculation
- Flex balance calculation

### Scheduling
- Planned work schedules per user
- Schedule storage and updates

### User Management
- User authentication
- Role-based authorization (`ADMIN` / `USER`)
- Password change
- Admin access to all users

### Frontend Application
- React SPA with routing
- Login with HTTP Basic Auth
- Persistent session via LocalStorage
- Protected routes
- Dashboard and navigation
- Profile page
- Placeholder pages for upcoming features

---

## Current Status

**Backend:**
- ✔ Spring Boot project initialized
- ✔ PostgreSQL connection configured
- ✔ Domain model implemented (User, Schedule, TimeEntry)
- ✔ Full time tracking flow implemented
- ✔ Flex balance calculation implemented
- ✔ Security and role-based access implemented
- ✔ Global exception handling implemented

**Frontend:**
- ✔ React application created with Vite
- ✔ Login functionality implemented
- ✔ Persistent authentication
- ✔ Protected routes
- ✔ Layout with navigation
- ✔ Dashboard + placeholder pages

---

## Local Setup

### 1. Create the database

```sql
CREATE DATABASE flexapp;
```

### 2. Configure environment variables

```
DB_URL=jdbc:postgresql://localhost:5432/flexapp
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

### 3. Run Backend

From the project root:

```bash
./mvnw spring-boot:run
```

Backend runs at: `http://localhost:8080`

### 4. Run Frontend

Navigate to the frontend directory:

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at: `http://localhost:5173`

---

## Authentication

The API uses Spring Security with HTTP Basic authentication.

### Test Users

| Role  | Email               | Password  |
|-------|---------------------|-----------|
| Admin | admin@flexapp.com   | temp123   |
| User  | user@flexapp.com    | temp123   |

### Authorization

| Role  | Access                                     |
|-------|--------------------------------------------|
| USER  | Own schedules and time entries only        |
| ADMIN | All users' data                            |

---

## API Overview

### Time Tracking

```
POST /api/time/{userId}/check-in
POST /api/time/{userId}/lunch-out
POST /api/time/{userId}/lunch-in
POST /api/time/{userId}/check-out
POST /api/time/{userId}/manual

GET  /api/time/{userId}/today
GET  /api/time/{userId}/history
GET  /api/time/{userId}/flex-balance
```

### Schedules

```
GET  /api/schedules/{userId}
POST /api/schedules/{userId}
```

### User

```
GET /api/users/me
PUT /api/users/me/password
```

---

## Project Structure

```
flexapp/
├── src/
└── frontend/
    └── src/
        ├── components/
        ├── pages/
        └── services/
```

---

## Security Notes

Authentication uses HTTP Basic Auth. The frontend stores the `Authorization` header under the key `authHeader` in LocalStorage, which is then included in all API requests.

---

## License

Private project — all rights reserved.