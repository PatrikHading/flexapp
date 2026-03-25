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

| Layer       | Technology                   |
|-------------|------------------------------|
| Backend     | Spring Boot (Java 21+)       |
| Persistence | Spring Data JPA / Hibernate  |
| Database    | PostgreSQL                   |
| Security    | Spring Security (HTTP Basic) |
| Frontend    | React (Vite) + React Router  |
| Build Tool  | Maven + npm                  |

---

## Features

- Check in / Check out
- Lunch tracking
- Flexible work schedules
- Flex balance calculation
- Manual time entries (including retroactive entries)
- User profile management
- Password change
- Admin management of employees
- Admin user creation
- User authentication and roles
- Role-based access control

---

## Current Status

**Backend:**
- ✔ Spring Boot backend fully implemented
- ✔ PostgreSQL persistence configured
- ✔ User authentication with Spring Security (HTTP Basic)
- ✔ Role-based authorization (USER / ADMIN)
- ✔ Work schedules with expected work time and paid lunch
- ✔ Live time tracking (check-in, lunch-out, lunch-in, check-out)
- ✔ Manual time entry for missed registrations
- ✔ Historical time data retrieval
- ✔ Flex balance calculation
- ✔ User profile management
- ✔ Password change functionality
- ✔ Admin API for managing users
- ✔ Admin can view all users
- ✔ Admin can create new users
- ✔ Global exception handling implemented
- ✔ React frontend created using Vite
- ✔ Login flow integrated with backend
- ✔ Protected routes implemented
- ✔ Dashboard with user overview
- ✔ Schedule page
- ✔ Time reporting page
- ✔ History page
- ✔ Profile page
- ✔ Admin page with user list and creation form
- ✔ Flex balance visible in frontend

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

Role-based access control is implemented:

USER:
- Can access own profile, schedules and time entries
- Can update own profile information
- Can change password
- Can register work time manually

ADMIN:
- All USER permissions
- Can view all users
- Can create new users

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
PUT /api/users/me
PUT /api/users/me/password
```

### Admin

```
GET /api/admin/users
POST /api/admin/users
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