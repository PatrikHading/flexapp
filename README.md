# FlexApp – Time & Flex Tracking System

FlexApp är en fullstack webbapplikation för att hantera arbetstid, flexsaldo och scheman inom en organisation.

Systemet är byggt för att användas i verklig drift och fokuserar på enkel daglig användning, tydlig översikt och administrativ kontroll.

---

## Funktioner

### Användare
- Checka in / ut arbetsdag
- Registrera lunch (ut/in)
- Registrera manuell tidrapport
- Se arbetshistorik
- Se aktuellt flexsaldo
- Uppdatera profil och byta lösenord

### Administratörer
- Skapa, uppdatera och inaktivera användare
- Hantera användarroller (USER / ADMIN)
- Återställa användares lösenord
- Skapa enskilda och återkommande arbetsscheman per användare

### Schemahantering
- Skapa/uppdatera schema per datum och användare
- Hämta dagens schema
- Skapa återkommande scheman för ett datumintervall

---

## Arkitektur

Applikationen är uppdelad i tydliga lager:

```
Controller → Service → Repository → Database
```

- **Controller** – hanterar HTTP-anrop
- **Service** – affärslogik och regler
- **Repository** – databasåtkomst
- **DTOs** – säker och tydlig dataöverföring mellan lager

---

## Säkerhet

- JWT lagras i en **HttpOnly-cookie** (ej tillgänglig via JavaScript)
- Cookie-baserad autentisering med 8 timmars giltighetstid
- Role-based access control: `USER` / `ADMIN`
- Krypterade lösenord med BCrypt
- CORS konfigurerat per miljö

---

## Tech Stack

### Backend
- Java
- Spring Boot 4
- Spring Security
- JPA / Hibernate
- PostgreSQL
- Lombok

### Frontend
- React
- React Router
- Fetch API (cookie-baserad autentisering)

---

## API-översikt

Autentisering sker via en HttpOnly JWT-cookie som sätts vid inloggning och rensas vid utloggning. Alla skyddade endpoints kräver en giltig cookie.

### Autentisering
| Metod | Endpoint | Beskrivning |
|-------|----------|-------------|
| `POST` | `/api/auth/login` | Logga in, sätter JWT-cookie |
| `POST` | `/api/auth/logout` | Logga ut, rensar JWT-cookie |

### Användarprofil
| Metod | Endpoint | Beskrivning |
|-------|----------|-------------|
| `GET` | `/api/users/me` | Hämta inloggad användares profil |
| `PUT` | `/api/users/me` | Uppdatera profil (namn, e-post) |
| `PUT` | `/api/users/me/password` | Byt lösenord |

### Tidsregistrering
| Metod | Endpoint | Beskrivning |
|-------|----------|-------------|
| `POST` | `/api/time/{userId}/check-in` | Checka in |
| `POST` | `/api/time/{userId}/lunch-out` | Stämpla ut till lunch |
| `POST` | `/api/time/{userId}/lunch-in` | Stämpla in från lunch |
| `POST` | `/api/time/{userId}/check-out` | Checka ut |
| `POST` | `/api/time/{userId}/manual` | Registrera manuell tidrapport |
| `GET` | `/api/time/{userId}/today` | Hämta dagens tidrapport |
| `GET` | `/api/time/{userId}/history` | Hämta arbetshistorik |
| `GET` | `/api/time/{userId}/flex-balance` | Hämta aktuellt flexsaldo |

### Scheman
| Metod | Endpoint | Beskrivning |
|-------|----------|-------------|
| `POST` | `/api/schedules/{userId}` | Skapa eller uppdatera schema för ett datum |
| `GET` | `/api/schedules/{userId}/today` | Hämta dagens schema |
| `GET` | `/api/schedules/{userId}` | Hämta alla scheman för användare |

### Admin – Användare
| Metod | Endpoint | Beskrivning |
|-------|----------|-------------|
| `GET` | `/api/admin/users` | Lista alla användare |
| `POST` | `/api/admin/users` | Skapa ny användare |
| `PUT` | `/api/admin/users/{id}` | Uppdatera användare |
| `PUT` | `/api/admin/users/{id}/password` | Återställ användares lösenord |
| `DELETE` | `/api/admin/users/{id}` | Inaktivera användare |

### Admin – Scheman
| Metod | Endpoint | Beskrivning |
|-------|----------|-------------|
| `POST` | `/api/admin/users/{userId}/schedules/recurring` | Skapa återkommande schema för datumintervall |

---

## Installation (lokal utveckling)

### 1. Klona repo
```bash
git clone https://github.com/PatrikHading/flexapp.git
cd flexapp
```

### 2. Databas
Skapa en PostgreSQL-databas med namnet:
```
flexapp
```

### 3. Miljövariabler
Applikationen läser konfiguration från miljövariabler. Sätt dessa innan du startar:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/flexapp
export DB_USERNAME=your_user
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-key
export CORS_ORIGIN=http://localhost:5173
```

> I dev-läge (`application-dev.properties`) är `cookie.secure=false` och CORS tillåter `http://localhost:5173` som standard.

### 4. Starta backend
```bash
mvn spring-boot:run
```

### 5. Starta frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend körs på `http://localhost:5173` och kommunicerar med backend på `http://localhost:8080`.

---

## Roadmap

Planerade förbättringar:

- [ ] Dockerisering
- [ ] Deploy till moln (AWS)
- [ ] Refresh tokens
- [ ] Förbättrad loggning och audit
- [ ] Integrationstester
- [ ] CI/CD pipeline

---

## Status

Aktiv utveckling — Målsättning: stabil drift i organisationsmiljö

---

## Syfte

FlexApp är utvecklad som en praktisk lösning för att:

- Förenkla tidsrapportering
- Ge bättre översikt över flexsaldo
- Minska administrativt arbete
- Ersätta manuella eller spridda system

---

## Kontakt

GitHub: [https://github.com/PatrikHading](https://github.com/PatrikHading)

___

## License
© 2026 Patrik Hading. All rights reserved.
This software is proprietary and may not be used, copied, modified, or distributed without explicit permission.