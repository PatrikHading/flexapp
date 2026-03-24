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

- User authentication (HTTP Basic)
- Role-based authorization
- Check in / Check out
- Lunch tracking
- Manual time entries
- Flexible work schedules
- Flex balance calculation
- Time history view
- User profile page
- Password change functionality
- Admin management of employees
- Web frontend built with React

---

## Current Status

**Backend:**
- ✔ Spring Boot backend fully implemented
- ✔ PostgreSQL persistence configured
- ✔ Spring Security with Basic Auth enabled
- ✔ Role-based access control implemented
- ✔ Work schedule management implemented
- ✔ Full time tracking flow implemented
- ✔ Manual time registration supported
- ✔ Flex balance calculation implemented
- ✔ User profile management implemented
- ✔ Password change endpoint implemented

**Frontend:**
- ✔ React frontend created with Vite
- ✔ Frontend login integrated with backend
- ✔ Protected routes implemented in frontend
- ✔ Schedule page connected to backend
- ✔ Time registration page connected to backend
- ✔ History page connected to backend
- ✔ Profile page with password change implemented

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

## Frontend Setup

The project includes a React frontend located in the `/frontend` directory.

### Install dependencies

```bash
cd frontend
npm install
```

### Run the frontend

```bash
npm run dev
```

Frontend will be available at: `http://localhost:5173`

The frontend communicates with the backend at: `http://localhost:8080`

---

## Authentication

The API uses Spring Security with HTTP Basic authentication. The React frontend stores the Basic Auth header locally and sends it with each request.

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
├── backend/          # Spring Boot application
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