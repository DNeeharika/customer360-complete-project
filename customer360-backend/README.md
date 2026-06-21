# Customer360 Backend

This is the backend application for the Customer360 project. It is built using Java, Spring Boot, Spring Security, MongoDB, CSV, and JSON.

The backend exposes REST APIs for authentication, customer data consolidation, export, AI summary, and Admin data upload.

## Technology Stack

| Technology        | Usage                         |
| ----------------- | ----------------------------- |
| Java 25           | Backend runtime               |
| Spring Boot 4.1.0 | Application framework         |
| Spring Web MVC    | REST APIs                     |
| Spring Security   | JWT authentication and RBAC   |
| MongoDB Atlas     | Customer profile data         |
| CSV               | Customer order source         |
| JSON              | Customer preference source    |
| Maven             | Build tool                    |
| Swagger/OpenAPI   | API documentation and testing |

## Backend URL

```text
http://localhost:8080
```

## Swagger URL

```text
http://localhost:8080/swagger-ui.html
```

## Data Source Design

The backend consolidates three different data sources:

| Data                 | Source  |
| -------------------- | ------- |
| Customer Profile     | MongoDB |
| Customer Orders      | CSV     |
| Customer Preferences | JSON    |

The common field used to merge data is:

```text
customerId
```

## Authentication

The backend supports JWT-based authentication.

Login API:

```http
POST /api/auth/login
```

Request:

```json
{
  "username": "admin",
  "password": "Admin@123"
}
```

Response:

```json
{
  "token": "jwt-token",
  "username": "admin",
  "fullName": "Admin User",
  "role": "ADMIN"
}
```

Current user API:

```http
GET /api/auth/me
```

Header:

```text
Authorization: Bearer <token>
```

## Demo Users

| Username | Password    | Role    |
| -------- | ----------- | ------- |
| admin    | Admin@123   | ADMIN   |
| manager  | Manager@123 | MANAGER |
| viewer   | Viewer@123  | VIEWER  |

## Role-Based Access Control

| API / Feature     | ADMIN | MANAGER | VIEWER |
| ----------------- | ----: | ------: | -----: |
| Login             |   Yes |     Yes |    Yes |
| Customer list     |   Yes |     Yes |    Yes |
| Customer profile  |   Yes |     Yes |    Yes |
| AI summary        |   Yes |     Yes |     No |
| Export APIs       |   Yes |     Yes |     No |
| Admin data upload |   Yes |      No |     No |

## Main APIs

### Customer APIs

```http
GET /api/customers
GET /api/customers/page
GET /api/customers/{customerId}
GET /api/customers/{customerId}/summary
```

### Export APIs

```http
GET /api/customers/export/csv
GET /api/customers/export/excel
GET /api/customers/export/pdf
```

### Admin Data Upload APIs

```http
GET  /api/admin/data/status
POST /api/admin/data/upload/orders
POST /api/admin/data/upload/preferences
```

## Dynamic CSV and JSON Upload

Admin can upload new CSV/JSON files using the Admin APIs.

Orders CSV upload:

```http
POST /api/admin/data/upload/orders
```

Preferences JSON upload:

```http
POST /api/admin/data/upload/preferences
```

Both APIs require:

```text
Authorization: Bearer <admin-token>
```

## Orders CSV Format

The orders CSV should contain the following columns:

```text
customerId,orderId,orderDate,amount,productCategory,orderStatus,paymentMode,discountAmount
```

Example:

```csv
customerId,orderId,orderDate,amount,productCategory,orderStatus,paymentMode,discountAmount
C1001,O9001,2026-06-20,9999,Electronics,Completed,UPI,500
C1002,O9002,2026-06-20,2500,Fashion,Completed,Credit Card,100
```

## Preferences JSON Format

The preferences JSON should be an array of preference objects.

Example:

```json
[
  {
    "customerId": "C1001",
    "membership": "Gold",
    "preferredChannel": "Email",
    "preferredLanguage": "English",
    "notificationOptIn": true,
    "marketingConsent": true,
    "preferredContactTime": "Evening"
  }
]
```

## Dynamic Cache Behavior

Uploaded CSV and JSON files refresh backend memory/cache immediately.

Current behavior:

| Action          | Result                                     |
| --------------- | ------------------------------------------ |
| Upload CSV/JSON | Dashboard updates immediately              |
| Browser refresh | Data remains                               |
| Backend restart | Default files reload from resources folder |

## Run Backend

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-backend
.\mvnw.cmd spring-boot:run
```

## Compile Backend

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-backend
.\mvnw.cmd clean compile
```

## Test Login Using PowerShell

```powershell
$body = @{
  username = "admin"
  password = "Admin@123"
} | ConvertTo-Json

$login = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

$login.token
```

## Test Customer API Using Token

```powershell
$headers = @{
  Authorization = "Bearer $($login.token)"
}

Invoke-RestMethod `
  -Uri "http://localhost:8080/api/customers/page?page=0&size=5" `
  -Headers $headers
```

## Test Admin Data Status

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/admin/data/status" `
  -Headers $headers
```

## MongoDB Atlas Setup

The backend uses MongoDB Atlas for customer profile data.

For development, Atlas Network Access can allow:

```text
0.0.0.0/0
```

For production, this should be restricted to trusted IP addresses only.

## Current Backend Status

The backend supports:

```text
Authentication
JWT Token
RBAC
Customer data consolidation
MongoDB + CSV + JSON integration
Export APIs
AI summary API
Admin dynamic CSV/JSON upload
Swagger API testing
```
