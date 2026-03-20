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
Status: Project initialized with Spring Boot and database connectivity.
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

---

## License

```
Private project — all rights reserved.
```