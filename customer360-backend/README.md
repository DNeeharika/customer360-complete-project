# Customer360 Backend

This is the backend application for the Customer360 project.

It is built using **Java**, **Spring Boot**, **Spring Security**, **JWT**, **MongoDB**, **CSV**, and **JSON**.

The backend exposes REST APIs for:

```text
Authentication
Customer data consolidation
Customer dashboard
Customer profile details
Export APIs
AI summary
Admin CSV/JSON upload
Persistent source file storage
Reset to default data
Admin customer profile CRUD
```

## Backend Technology Stack

| Technology        | Purpose                                     |
| ----------------- | ------------------------------------------- |
| Java 25           | Backend runtime                             |
| Spring Boot 4.1.0 | Application framework                       |
| Spring Web MVC    | REST APIs                                   |
| Spring Security   | Authentication and role-based authorization |
| JWT               | Stateless authentication token              |
| MongoDB Atlas     | Customer profile database                   |
| CSV               | Customer orders source                      |
| JSON              | Customer preferences source                 |
| Maven             | Build and dependency management             |
| Swagger/OpenAPI   | API documentation and testing               |

## Backend URL

```text
http://localhost:8080
```

## Swagger URL

```text
http://localhost:8080/swagger-ui.html
```

## API Docs URL

```text
http://localhost:8080/v3/api-docs
```

## Backend Package

```text
com.customer360.backend
```

## Backend Folder Structure

```text
customer360-backend
├── src/main/java/com/customer360/backend
│   ├── config
│   ├── controller
│   ├── dto
│   ├── loader
│   ├── model
│   ├── repository
│   ├── security
│   └── service
│
├── src/main/resources
│   ├── application.properties
│   └── data
│       ├── customer_orders.csv
│       └── customer_preferences.json
│
├── uploads
├── pom.xml
└── README.md
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

The backend uses JWT authentication.

### Login API

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

### Current User API

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

| API / Feature                         | ADMIN | MANAGER | VIEWER |
| ------------------------------------- | ----: | ------: | -----: |
| Login                                 |   Yes |     Yes |    Yes |
| Current user                          |   Yes |     Yes |    Yes |
| Customer list                         |   Yes |     Yes |    Yes |
| Customer profile                      |   Yes |     Yes |    Yes |
| AI summary                            |   Yes |     Yes |     No |
| Export APIs                           |   Yes |     Yes |     No |
| Admin data upload                     |   Yes |      No |     No |
| Reset to default data                 |   Yes |      No |     No |
| Customer profile create/update/delete |   Yes |      No |     No |

## Authentication APIs

```http
POST /api/auth/login
GET  /api/auth/me
```

## Customer APIs

```http
GET /api/customers
GET /api/customers/page
GET /api/customers/{customerId}
GET /api/customers/{customerId}/summary
```

## Export APIs

```http
GET /api/customers/export/csv
GET /api/customers/export/excel
GET /api/customers/export/pdf
```

## Admin Data APIs

The backend provides ADMIN-only APIs for dynamic CSV/JSON upload, status check, persistent storage, and reset to default data.

```http
GET  /api/admin/data/status
POST /api/admin/data/upload/orders
POST /api/admin/data/upload/preferences
POST /api/admin/data/reset-defaults
```

All Admin Data APIs require:

```text
Authorization: Bearer <admin-token>
```

## Admin Customer Profile APIs

The backend provides ADMIN-only APIs to manage MongoDB customer profiles.

```http
POST   /api/admin/customers
PUT    /api/admin/customers/{customerId}
DELETE /api/admin/customers/{customerId}
```

These APIs manage only MongoDB customer profile data.

Orders continue to come from CSV, and preferences continue to come from JSON.

## Customer Profile Request Format

Used by:

```http
POST /api/admin/customers
PUT  /api/admin/customers/{customerId}
```

Example:

```json
{
  "customerId": "C3001",
  "name": "Demo Customer",
  "email": "demo.customer@example.com",
  "mobile": "9876543210",
  "city": "Hyderabad",
  "gender": "Female",
  "age": 28,
  "dateOfBirth": "1998-01-15",
  "address": "Hyderabad, Telangana",
  "customerType": "Retail"
}
```

Required fields:

```text
customerId - required for create
name
email
mobile
city
```

Optional fields:

```text
gender
age
dateOfBirth
address
customerType
```

## Dynamic CSV Upload

Admin can upload a new orders CSV file.

API:

```http
POST /api/admin/data/upload/orders
```

Request type:

```text
multipart/form-data
```

Form field:

```text
file
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

Validation rules:

```text
customerId cannot be blank
orderId cannot be blank
orderDate must be valid ISO date format yyyy-MM-dd
amount cannot be blank
amount cannot be negative
discountAmount cannot be negative
discountAmount cannot be greater than amount
Rows with invalid data are skipped
```

## Dynamic JSON Upload

Admin can upload a new preferences JSON file.

API:

```http
POST /api/admin/data/upload/preferences
```

Request type:

```text
multipart/form-data
```

Form field:

```text
file
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
  },
  {
    "customerId": "C1002",
    "membership": "Silver",
    "preferredChannel": "SMS",
    "preferredLanguage": "Hindi",
    "notificationOptIn": true,
    "marketingConsent": false,
    "preferredContactTime": "Morning"
  }
]
```

Validation rules:

```text
customerId is required
Records without customerId are skipped
```

## Persistent Upload Storage

Uploaded CSV and JSON files are saved in:

```text
customer360-backend/uploads
```

The backend uses fixed names:

```text
customer_orders.csv
customer_preferences.json
```

## Startup Data Loading Behavior

When the backend starts:

```text
1. It checks customer360-backend/uploads/customer_orders.csv
2. If found, it loads uploaded customer orders
3. If not found, it loads default src/main/resources/data/customer_orders.csv

4. It checks customer360-backend/uploads/customer_preferences.json
5. If found, it loads uploaded preferences
6. If not found, it loads default src/main/resources/data/customer_preferences.json
```

## Reset to Default Data

Admin can reset uploaded CSV/JSON data back to the default files.

API:

```http
POST /api/admin/data/reset-defaults
```

Reset behavior:

```text
1. Deletes uploads/customer_orders.csv
2. Deletes uploads/customer_preferences.json
3. Reloads default data/customer_orders.csv
4. Reloads default data/customer_preferences.json
5. Updates backend cache immediately
```

Expected default status:

```text
ordersCustomerCount = 6
preferencesCustomerCount = 5
```

## Data Status API

API:

```http
GET /api/admin/data/status
```

Example response:

```json
{
  "ordersCustomerCount": 6,
  "preferencesCustomerCount": 5,
  "message": "Dynamic CSV and JSON cache is active."
}
```

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

## Test Customer Page API

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

## Test Create Customer Profile

```powershell
$headers = @{
  Authorization = "Bearer $($login.token)"
  "Content-Type" = "application/json"
}

$newCustomer = @{
  customerId = "C3001"
  name = "Demo Customer"
  email = "demo.customer@example.com"
  mobile = "9876543210"
  city = "Hyderabad"
  gender = "Female"
  age = 28
  dateOfBirth = "1998-01-15"
  address = "Hyderabad, Telangana"
  customerType = "Retail"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/api/admin/customers" `
  -Method POST `
  -Headers $headers `
  -Body $newCustomer
```

## Test Update Customer Profile

```powershell
$updateCustomer = @{
  name = "Updated Demo Customer"
  email = "updated.demo@example.com"
  mobile = "9999999999"
  city = "Bangalore"
  gender = "Female"
  age = 29
  dateOfBirth = "1997-01-15"
  address = "Bangalore, Karnataka"
  customerType = "Premium"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/api/admin/customers/C3001" `
  -Method PUT `
  -Headers $headers `
  -Body $updateCustomer
```

## Test Delete Customer Profile

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/admin/customers/C3001" `
  -Method DELETE `
  -Headers $headers
```

## Test Reset to Default Data

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/admin/data/reset-defaults" `
  -Method POST `
  -Headers $headers
```

## MongoDB Atlas Setup Note

For development, MongoDB Atlas Network Access can allow:

```text
0.0.0.0/0
```

For production, restrict MongoDB access to trusted backend server IP addresses only.

## Git Ignore for Uploaded Files

Runtime uploaded CSV/JSON files should not be committed.

The `.gitignore` should contain:

```gitignore
customer360-backend/uploads/*.csv
customer360-backend/uploads/*.json
```

## Current Backend Status

The backend currently supports:

```text
JWT authentication
Role-Based Access Control
Customer data consolidation
MongoDB + CSV + JSON integration
Customer dashboard APIs
Customer details API
AI summary API
Export APIs
Admin data upload APIs
Persistent CSV/JSON upload storage
Reset to default data
Admin customer profile CRUD APIs
Swagger API testing
```
