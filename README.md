# Customer360 Complete Project

Customer360 is a full-stack customer data integration application built using **Spring Boot**, **React**, **MongoDB**, **CSV**, and **JSON**.

The application demonstrates how data from multiple sources can be consolidated into a single unified customer profile using a common `customerId`.

## Project Objective

The goal of this project is to build an industry-style Customer360 application that integrates customer information from different source systems.

The application consolidates:

| Data Type            | Source  | Purpose                                         |
| -------------------- | ------- | ----------------------------------------------- |
| Customer Profile     | MongoDB | Stores customer master/profile information      |
| Customer Orders      | CSV     | Stores customer order/transaction details       |
| Customer Preferences | JSON    | Stores membership and communication preferences |

The common key used to merge all three data sources is:

```text
customerId
```

## High-Level Application Flow

```text
Login
  ↓
JWT Authentication
  ↓
Role-Based Access
  ↓
Customer Dashboard
  ↓
MongoDB + CSV + JSON Consolidation
  ↓
View Profile / Export / AI Summary / Admin Actions
```

## Current Features

| Feature                              | Status    |
| ------------------------------------ | --------- |
| Customer profile data from MongoDB   | Completed |
| Customer order data from CSV         | Completed |
| Customer preference data from JSON   | Completed |
| Consolidated customer profile        | Completed |
| Search                               | Completed |
| Filter                               | Completed |
| Sort                                 | Completed |
| Backend pagination                   | Completed |
| Customer profile details             | Completed |
| Customer data quality warnings       | Completed |
| Export CSV                           | Completed |
| Export JSON                          | Completed |
| Export Excel                         | Completed |
| Export PDF                           | Completed |
| Customer profile CSV export          | Completed |
| Customer profile print/PDF           | Completed |
| AI-powered customer summary          | Completed |
| Login page                           | Completed |
| JWT authentication                   | Completed |
| Protected dashboard route            | Completed |
| Logout / switch user                 | Completed |
| Role-Based Access Control            | Completed |
| Admin CSV/JSON upload API            | Completed |
| Admin data upload UI                 | Completed |
| Persistent uploaded CSV/JSON storage | Completed |
| Reset to default CSV/JSON data       | Completed |
| Customer profile CRUD APIs           | Completed |
| Admin customer management UI         | Completed |

## User Roles

| Role    | Access                                                                                                                     |
| ------- | -------------------------------------------------------------------------------------------------------------------------- |
| ADMIN   | Full access including dashboard, exports, AI summary, CSV/JSON upload, reset default data, and customer profile management |
| MANAGER | Dashboard, customer profile view, AI summary, and exports                                                                  |
| VIEWER  | View-only access to customer dashboard and customer profile                                                                |

## Demo Users

| Username | Password    | Role    |
| -------- | ----------- | ------- |
| admin    | Admin@123   | ADMIN   |
| manager  | Manager@123 | MANAGER |
| viewer   | Viewer@123  | VIEWER  |

## Project Structure

```text
customer360
├── customer360-backend
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com/customer360/backend
│   │   │   └── resources
│   │   │       └── data
│   │   │           ├── customer_orders.csv
│   │   │           └── customer_preferences.json
│   │   └── test
│   ├── uploads
│   ├── pom.xml
│   └── README.md
│
├── customer360-frontend
│   ├── src
│   │   ├── api
│   │   ├── context
│   │   ├── pages
│   │   ├── routes
│   │   ├── App.jsx
│   │   └── App.css
│   ├── package.json
│   ├── vite.config.js
│   └── README.md
│
├── .gitignore
└── README.md
```

## Technology Stack

### Backend

| Technology        | Purpose                          |
| ----------------- | -------------------------------- |
| Java 25           | Backend runtime                  |
| Spring Boot 4.1.0 | Backend application framework    |
| Spring Web MVC    | REST APIs                        |
| Spring Security   | Authentication and authorization |
| JWT               | Token-based authentication       |
| MongoDB Atlas     | Customer profile database        |
| CSV               | Customer order data source       |
| JSON              | Customer preference data source  |
| Maven             | Build tool                       |
| Swagger/OpenAPI   | API documentation and testing    |

### Frontend

| Technology   | Purpose                           |
| ------------ | --------------------------------- |
| React        | Frontend UI                       |
| Vite         | Development server and build tool |
| React Router | Routing and protected routes      |
| Axios        | Backend API integration           |
| CSS          | Styling and responsive layout     |

## Backend URL

```text
http://localhost:8080
```

## Swagger URL

```text
http://localhost:8080/swagger-ui.html
```

## Frontend URL

```text
http://localhost:5174
```

Depending on Vite port availability, the frontend may also run on:

```text
http://localhost:5173
```

## Main Pages

| Route                | Page                           | Access                 |
| -------------------- | ------------------------------ | ---------------------- |
| `/login`             | Login page                     | Public                 |
| `/customers`         | Customer dashboard             | ADMIN, MANAGER, VIEWER |
| `/admin/data-upload` | Admin data upload page         | ADMIN only             |
| `/admin/customers`   | Admin customer management page | ADMIN only             |

## Data Source Design

Customer360 uses a multi-source data design.

```text
MongoDB Customer Profiles
        +
CSV Customer Orders
        +
JSON Customer Preferences
        ↓
Consolidated Customer360 Profile
```

### MongoDB Customer Profile

MongoDB stores customer master data such as:

```text
customerId
name
email
mobile
city
gender
age
dateOfBirth
address
customerType
```

### CSV Customer Orders

CSV stores order information such as:

```text
customerId
orderId
orderDate
amount
productCategory
orderStatus
paymentMode
discountAmount
```

### JSON Customer Preferences

JSON stores preference information such as:

```text
customerId
membership
preferredChannel
preferredLanguage
notificationOptIn
marketingConsent
preferredContactTime
```

## Dynamic CSV and JSON Upload

ADMIN users can upload new CSV and JSON files from the Admin Data Upload page.

| Upload Type      | API                                  | Purpose                 |
| ---------------- | ------------------------------------ | ----------------------- |
| Orders CSV       | `/api/admin/data/upload/orders`      | Refresh order data      |
| Preferences JSON | `/api/admin/data/upload/preferences` | Refresh preference data |

Uploaded files are saved persistently under:

```text
customer360-backend/uploads
```

The backend stores uploaded files using fixed file names:

```text
customer_orders.csv
customer_preferences.json
```

## Persistent Upload Behavior

On backend startup, the application follows this loading order:

```text
1. Check customer360-backend/uploads folder
2. If uploaded CSV/JSON files exist, load uploaded files
3. If uploaded files do not exist, load default files from src/main/resources/data
```

This means uploaded CSV and JSON files continue to work even after backend restart.

## Reset to Default Data

ADMIN users can reset the application back to default CSV and JSON source files.

Reset API:

```http
POST /api/admin/data/reset-defaults
```

Reset behavior:

```text
1. Deletes customer360-backend/uploads/customer_orders.csv
2. Deletes customer360-backend/uploads/customer_preferences.json
3. Reloads default customer_orders.csv from src/main/resources/data
4. Reloads default customer_preferences.json from src/main/resources/data
5. Refreshes backend cache immediately
6. Dashboard returns to default source data
```

Expected default count after reset:

| Source                          | Expected Count |
| ------------------------------- | -------------: |
| Orders CSV customer count       |              6 |
| Preferences JSON customer count |              5 |

## Admin Customer Management

ADMIN users can manage MongoDB customer profiles from:

```text
/admin/customers
```

Supported actions:

| Action          | Description                               |
| --------------- | ----------------------------------------- |
| Add Customer    | Creates a new customer profile in MongoDB |
| Edit Customer   | Updates existing MongoDB customer profile |
| Delete Customer | Deletes customer profile from MongoDB     |
| Refresh         | Reloads latest customer profile list      |

Important:

```text
This page manages MongoDB profile data only.
Orders still come from CSV.
Preferences still come from JSON.
```

If a newly added customer is not available in CSV or JSON, the dashboard will show missing data warnings. This is expected behavior.

## Role-Based Access Summary

| Feature                   | ADMIN | MANAGER | VIEWER |
| ------------------------- | ----: | ------: | -----: |
| Login                     |   Yes |     Yes |    Yes |
| Customer dashboard        |   Yes |     Yes |    Yes |
| Customer profile view     |   Yes |     Yes |    Yes |
| AI summary                |   Yes |     Yes |     No |
| Export CSV/Excel/PDF/JSON |   Yes |     Yes |     No |
| Customer profile export   |   Yes |     Yes |     No |
| Admin data upload         |   Yes |      No |     No |
| Reset to default data     |   Yes |      No |     No |
| Add customer profile      |   Yes |      No |     No |
| Edit customer profile     |   Yes |      No |     No |
| Delete customer profile   |   Yes |      No |     No |

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

## Run Frontend

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-frontend
npm run dev
```

## Build Frontend

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-frontend
npm run build
```

## Recommended Testing Flow

### 1. Start Backend

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-backend
.\mvnw.cmd spring-boot:run
```

### 2. Start Frontend

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-frontend
npm run dev
```

### 3. Login as Admin

```text
admin / Admin@123
```

### 4. Test Dashboard

Open:

```text
http://localhost:5174/customers
```

Verify:

```text
Customer list loads
Search works
Filters work
Pagination works
Customer profile opens
Exports work for ADMIN
AI summary works for ADMIN
```

### 5. Test Admin Data Upload

Open:

```text
http://localhost:5174/admin/data-upload
```

Verify:

```text
Upload Orders CSV
Upload Preferences JSON
Status cards update
Dashboard reflects uploaded data
Uploaded files persist after backend restart
Reset to Default Data restores default source data
```

### 6. Test Admin Customer Management

Open:

```text
http://localhost:5174/admin/customers
```

Verify:

```text
Add Customer
Edit Customer
Delete Customer
Dashboard refreshes with latest MongoDB profile data
```

### 7. Test Role-Based Access

Login as:

```text
manager / Manager@123
viewer / Viewer@123
```

Verify:

```text
Manager and Viewer cannot access admin pages
Manager can use AI summary and exports
Viewer can only view dashboard and profile
```

## MongoDB Atlas Note

For development, MongoDB Atlas Network Access can allow:

```text
0.0.0.0/0
```

For production, restrict MongoDB access to trusted IP addresses only.

## Git Ignore for Uploaded Data

Runtime uploaded CSV/JSON files should not be committed to Git.

The `.gitignore` should exclude:

```gitignore
customer360-backend/uploads/*.csv
customer360-backend/uploads/*.json
```

This prevents uploaded customer data from being pushed to GitHub.

## Current Project Status

Customer360 currently supports:

```text
Full-stack React + Spring Boot application
JWT login
Logout
Role-Based Access Control
Protected routes
MongoDB customer profile integration
CSV order integration
JSON preference integration
Consolidated Customer360 dashboard
Search/filter/sort/pagination
Exports
AI summary
Admin CSV/JSON upload
Persistent uploaded files
Reset to default data
MongoDB customer profile CRUD
Admin customer management UI
Detailed documentation
```

## Next Recommended Enhancements

Future improvements can include:

```text
1. Store login users in MongoDB
2. Add audit logs for upload/reset/customer changes
3. Add validation preview before CSV/JSON upload
4. Add customer profile form validation improvements
5. Add unit and integration tests
6. Add Docker setup
7. Add deployment guide
8. Add production environment configuration
```
