# Postman Collection

**Import:** `Cards-IT-Assessment.postman_collection.json`

Collection name: **Cards IT Assessment**

## What’s included (maps to requirements)

| Folder | Request | Method | Requirement |
|--------|---------|--------|-------------|
| Cards | Create Card | `POST` | INSERT + `@Transactional` |
| Cards | Get Card by ID | `GET` | GET + `@Transactional` |
| Cards | Update Card | `PUT` | UPDATE + `@Transactional` |
| Cards | List Cards (Paginated) | `GET` | Pagination (10/page) |
| Cards | Enrich Card (3rd Party) | `GET` | Nested 3rd-party API |
| Negative Cases | Create Card - Invalid Payload | `POST` | Validation / error description |

## Variables

| Variable | Default |
|----------|---------|
| `baseUrl` | `http://localhost:8080` |
| `cardId` | `1` (auto-updated after Create Card) |
| `externalUserId` | `1` |

## How to import

Postman → **Import** → Upload Files → select `Cards-IT-Assessment.postman_collection.json`
