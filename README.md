# FlexApp 

> Web-based flex time tracking — built with Java Spring Boot & PostgreSQL.

---

## Tech Stack

```
Layer          Technology
─────────────────────────────────────
Language       Java 21+
Framework      Spring Boot
Persistence    Spring Data JPA
Database       PostgreSQL
Security       Spring Security 
Frontend       React 
```

---

## Features

```
Check in / Check out
Lunch tracking
Flexible work schedules
Flex balance calculation
Manual time entries
Admin management of employees
User authentication and roles
```

---

## Current Status

```
Spring Boot project initialized
PostgreSQL connection configured
User entity and role enum created
User repository verified with startup test data
Initial domain model created for users, work schedules, and time entries
Work schedules can now be stored and updated for users
Planned work schedules can be stored
Initial check-in logic is implemented and persisted to the database
Initial time tracking flow now supports check-in, lunch-out and lunch-in
Initial time tracking flow now supports check-in, lunch-out, lunch-in and check-out
Worked minutes and lunch minutes are now calculated and stored in the database
Flex balance calculation is now implemented
```

---

## Local Setup

### 1. Create the database

```sql
CREATE DATABASE flexapp;
```

### 2. Configure environment variables

```env
DB_URL=jdbc:postgresql://localhost:5432/flexapp
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The application starts at:

```
http://localhost:8080
```

## Authentication
The API now uses Spring Security with HTTP Basic authentication for protected endpoints.
```
Current test user:
email: `admin@flexapp.com`
password: `temp123`
```

## Current API
```bash
POST `/api/time/{userId}/check-in`
POST `/api/time/{userId}/lunch-out`
POST `/api/time/{userId}/lunch-in`
POST `/api/time/{userId}/check-out}`
GET `/api/time/{userId}/today`
```
---

## License

```
Private project — all rights reserved.
```