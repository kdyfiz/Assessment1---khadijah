# Cards IT — Java Spring Boot Assignment

**Department:** Delivery & Ops CFS & Core Banking  
**Section:** Cards IT

Spring Boot card management API with MSSQL, request/response logging, pagination, and a nested 3rd-party call.

---

## Requirements coverage

| # | Requirement | Where |
|---|-------------|--------|
| 1 | Java Spring Boot application | Spring Boot 3.2.5, Java 17 |
| 2 | Maintainable project structure | `controller` → `service` → `repository` → `entity` + `dto` / `config` / `exception` / `interceptor` |
| 3 | Client APIs + Postman collection | `/api/v1/cards` + `postman/Cards-IT-Assessment.postman_collection.json` |
| 4 | Log REQUEST & RESPONSE to file | `logs/api-request-response.log` |
| 5 | MSSQL `TESTDB` + `@Transactional` (INSERT / UPDATE / GET) | Docker MSSQL + `@Transactional` on service methods |
| 6 | GET with pagination (10/page) | `GET /api/v1/cards?page=0` |
| 7 | Nested 3rd-party API call | `GET /api/v1/cards/{id}/enrich` → JSONPlaceholder |

---

## Project structure

```
Assessment1---khadijah/
├── pom.xml
├── README.md
├── docker-compose.yml
├── sql/schema.sql
├── postman/
│   └── Cards-IT-Assessment.postman_collection.json
└── src/main/java/com/cardsit/assessment/
    ├── controller/
    ├── service/
    ├── repository/
    ├── entity/
    ├── dto/
    ├── config/
    ├── exception/
    └── interceptor/
```

---

## Prerequisites

- Docker Desktop (MSSQL on `localhost:1433`)
- JDK 17+
- Maven 3.8+
- Postman

### Database

| Setting | Value |
|---------|-------|
| Host | `localhost:1433` |
| Database | `TESTDB` |
| Username | `sa` |
| Password | `YourStrong@Passw0rd` |

---

## How to run

### 1. Start MSSQL (`TESTDB`)

```bash
cd Assessment1---khadijah
docker compose up -d mssql mssql-init
```

### 2. Start the application

```bash
mvn clean spring-boot:run
```

App URL: `http://localhost:8080`

### 3. Test APIs

Import `postman/Cards-IT-Assessment.postman_collection.json` into Postman.

Quick GET check in browser:

- http://localhost:8080/api/v1/cards?page=0
- http://localhost:8080/api/v1/cards/1/enrich?externalUserId=1

### Optional — run DB + app in Docker

```bash
docker compose --profile full up -d --build
```

Stop:

```bash
docker compose --profile full down
```

---

## API endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/cards` | Create card (INSERT) |
| `PUT` | `/api/v1/cards/{id}` | Update card |
| `GET` | `/api/v1/cards/{id}` | Get card by id |
| `GET` | `/api/v1/cards?page=0` | List cards (10 per page) |
| `GET` | `/api/v1/cards/{id}/enrich?externalUserId=1` | Card + 3rd-party profile |

### Create card example

```http
POST /api/v1/cards
Content-Type: application/json

{
  "cardNumber": "4111111111111111",
  "cardholderName": "Ahmad Bin Ali",
  "cardType": "CREDIT",
  "creditLimit": 10000.00,
  "currency": "MYR"
}
```

```json
{
  "success": true,
  "description": "Card created successfully",
  "data": {
    "id": 1,
    "cardNumber": "****-****-****-1111",
    "cardholderName": "Ahmad Bin Ali",
    "cardType": "CREDIT",
    "status": "ACTIVE",
    "creditLimit": 10000.00,
    "currency": "MYR"
  },
  "timestamp": "2026-03-24T19:00:00"
}
```

### Nested 3rd-party flow

```
Client → GET /api/v1/cards/{id}/enrich
      → local MSSQL (card)
      → JSONPlaceholder /users/{id}
      ← combined response
```

---

## Logging

API request/response audit file:

```
logs/api-request-response.log
```

Application log:

```
logs/application.log
```

---

## Tech stack

- Java 17, Spring Boot 3.2.5
- Spring Web, Spring Data JPA, Validation
- MSSQL (JDBC)
- RestTemplate
- Logback
