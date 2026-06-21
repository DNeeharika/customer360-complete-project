# Customer360 Complete Project

Customer360 is a full-stack customer data integration application built using Spring Boot, React, MongoDB, CSV, and JSON. The application consolidates customer profile data, customer order data, and customer preference data using a common `customerId` and presents a unified Customer360 dashboard.

## Project Objective

The purpose of this project is to demonstrate how different data sources can be integrated into one application.

The application reads data from:

| Data Source | Purpose                  |
| ----------- | ------------------------ |
| MongoDB     | Customer profile data    |
| CSV File    | Customer order data      |
| JSON File   | Customer preference data |

The system matches all data using:

```text
customerId
```

and displays a consolidated customer profile with customer details, orders, total order amount, membership details, preferred communication channel, warnings, and data quality status.

## Current Features

| Feature                            | Status    |
| ---------------------------------- | --------- |
| Customer profile data from MongoDB | Completed |
| Customer order data from CSV       | Completed |
| Customer preference data from JSON | Completed |
| Consolidated customer profile      | Completed |
| Search, filter, sort, pagination   | Completed |
| Export CSV, Excel, PDF, JSON       | Completed |
| Customer profile export            | Completed |
| AI-powered customer summary        | Completed |
| Login page                         | Completed |
| JWT authentication                 | Completed |
| Protected dashboard route          | Completed |
| Logout / switch user               | Completed |
| Role-Based Access Control          | Completed |
| Admin CSV/JSON upload API          | Completed |
| Admin data upload UI               | Completed |
| Dynamic CSV/JSON refresh           | Completed |

## User Roles

| Role    | Access                                                   |
| ------- | -------------------------------------------------------- |
| ADMIN   | Full access, dashboard, AI summary, exports, data upload |
| MANAGER | Dashboard, AI summary, exports                           |
| VIEWER  | View-only customer dashboard                             |

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
│   ├── src/main/java
│   ├── src/main/resources/data
│   └── pom.xml
│
├── customer360-frontend
│   ├── src
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

## Backend

Technology stack:

| Technology      | Version / Usage             |
| --------------- | --------------------------- |
| Java            | 25                          |
| Spring Boot     | 4.1.0                       |
| Spring Security | JWT authentication and RBAC |
| MongoDB Atlas   | Customer profile data       |
| CSV             | Customer orders             |
| JSON            | Customer preferences        |
| Maven           | Build tool                  |
| Swagger/OpenAPI | API testing                 |

Backend URL:

```text
http://localhost:8080
```

Swagger URL:

```text
http://localhost:8080/swagger-ui.html
```

## Frontend

Technology stack:

| Technology   | Usage                      |
| ------------ | -------------------------- |
| React        | Frontend UI                |
| Vite         | Development server         |
| React Router | Login and protected routes |
| Axios        | API integration            |
| CSS          | Dashboard styling          |

Frontend URL:

```text
http://localhost:5174
```

## Application Flow

```text
Login Page
    ↓
JWT Authentication
    ↓
Protected Customer Dashboard
    ↓
Customer List / Profile / AI Summary / Export
    ↓
Admin Data Upload for CSV and JSON
```

## Dynamic Data Upload

The Admin user can upload:

| File Type | API                                  | Purpose                          |
| --------- | ------------------------------------ | -------------------------------- |
| CSV       | `/api/admin/data/upload/orders`      | Refresh customer order data      |
| JSON      | `/api/admin/data/upload/preferences` | Refresh customer preference data |

The uploaded data is refreshed in backend memory/cache and immediately reflected in the dashboard.

Important:

```text
Uploaded CSV/JSON data is currently stored in memory.
If the backend restarts, default files from src/main/resources/data are loaded again.
```

## Run Backend

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-backend
.\mvnw.cmd spring-boot:run
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

## Compile Backend

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-backend
.\mvnw.cmd clean compile
```

## MongoDB Atlas Note

For development, MongoDB Atlas Network Access can allow:

```text
0.0.0.0/0
```

For production, restrict access to trusted IP addresses only.

## Current Status

The project currently supports secure login, JWT authentication, protected customer dashboard, role-based UI and backend security, MongoDB customer profile integration, CSV order ingestion, JSON preference ingestion, and Admin-controlled dynamic CSV/JSON refresh.
