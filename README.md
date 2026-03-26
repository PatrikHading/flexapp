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

**Frontend:**
- ✔ React frontend created with Vite
- ✔ Login flow integrated with backend
- ✔ Protected routes implemented
- ✔ Dashboard with user overview
- ✔ Schedule page connected to backend
- ✔ Time reporting page connected to backend
- ✔ History page connected to backend
- ✔ Profile page with password change implemented
- ✔ Admin page with user list and creation form
- ✔ Flex balance visible in frontend

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

### 3. Run the backend

From the project root:

```bash
./mvnw spring-boot:run
```

Backend runs at: `http://localhost:8080`

### 4. Run the frontend

Navigate to the frontend directory:

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at: `http://localhost:5173`

---

## Authentication

The API uses Spring Security with HTTP Basic authentication. The React frontend stores the Basic Auth header in LocalStorage under the key `authHeader` and sends it with every request.

### Roles

| Role  | Access                                                  |
|-------|---------------------------------------------------------|
| USER  | Own profile, schedules, and time entries only           |
| ADMIN | All USER permissions, plus full user management         |

### Test Users

| Role  | Email               | Password |
|-------|---------------------|----------|
| Admin | admin@flexapp.com   | temp123  |
| User  | user@flexapp.com    | temp123  |

---

## User Profile

Users can manage their own account from the profile page:

- View profile information
- Update name and email
- Change password

---

## Admin Functionality

Administrators can manage users and time entries via the admin panel. All admin functionality is restricted to users with the `ADMIN` role.

### User Management

Admins can:

- View all users
- Create new users
- Edit existing users
- Activate or deactivate accounts
- Change user roles (`USER` / `ADMIN`)
- Reset user passwords

### Manual Time Registration

Admins can register work time on behalf of users retroactively — for example, if a user forgot to log their time.

Supported fields:

| Field         | Description                        |
|---------------|------------------------------------|
| Work date     | The date the work was performed    |
| Check-in time | Start of the work day              |
| Lunch out/in  | Start and end of the lunch break   |
| Check-out time| End of the work day                |
| Comment       | Optional note about the entry      |

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
GET  /api/admin/users
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

## License

Private project — all rights reserved.