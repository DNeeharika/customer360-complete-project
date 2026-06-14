# Customer360 Complete Project

Customer360 is a full-stack customer data integration application that consolidates customer information from multiple data sources and provides a unified customer profile through a React frontend and Spring Boot backend.

The project integrates customer profile data from MongoDB, customer order data from a CSV file, customer preference data from a JSON file, and AI-powered customer summary generation using Ollama local AI.

---

## Project Objective

The objective of Customer360 is to build a web application that can:

* Read customer profile data from MongoDB
* Read customer order data from CSV
* Read customer preference data from JSON
* Match all data using `customerId`
* Display consolidated customer profile
* Show customer orders
* Calculate total order amount
* Show membership information
* Show preferred communication channel
* Provide search, filtering, sorting, and export features
* Generate AI-powered customer summary using Ollama local AI

---

## Repository Structure

```text
customer360-complete-project
  customer360-backend
  customer360-frontend
  docs
  .gitignore
  README.md
```

---

## Project Modules

| Module                 | Description                     |
| ---------------------- | ------------------------------- |
| `customer360-backend`  | Spring Boot backend application |
| `customer360-frontend` | React frontend application      |
| `docs`                 | Complete project documentation  |

---

## Technology Stack

| Layer             | Technology                   |
| ----------------- | ---------------------------- |
| Frontend          | React, Vite, JavaScript, CSS |
| Backend           | Java 25, Spring Boot 4.1.0   |
| Database          | MongoDB Atlas                |
| Data Files        | CSV, JSON                    |
| AI Integration    | Ollama Local AI              |
| API Documentation | Swagger/OpenAPI              |
| Testing           | JUnit 5, Mockito             |
| Build Tools       | Maven, npm                   |
| Version Control   | Git, GitHub                  |

---

## Data Sources

| Source          | Data Type            | Purpose                                               |
| --------------- | -------------------- | ----------------------------------------------------- |
| MongoDB         | Customer profile     | Stores name, email, mobile, city                      |
| CSV File        | Customer orders      | Stores order ID, order date, amount                   |
| JSON File       | Customer preferences | Stores membership and preferred communication channel |
| Ollama Local AI | AI summary           | Generates professional customer summary               |

---

## Features Implemented

### Backend Features

* MongoDB customer profile integration
* CSV customer order loading
* JSON customer preference loading
* Customer data consolidation using `customerId`
* Search, filter, and sort APIs
* Customer detail API
* AI-powered summary API using Ollama
* Rule-based fallback summary
* Full customer export as CSV, Excel, and PDF
* Sensitive data masking for email and mobile
* Data quality warning handling
* Global exception handling
* Structured logging
* Swagger/OpenAPI documentation
* Unit tests

### Frontend Features

* Customer list screen
* Search customers
* Filter by city, membership, and preferred channel
* Sort customers
* View customer details
* View customer orders
* Generate AI customer summary
* Export full customer list as CSV, Excel, and PDF
* Export selected customer details as CSV
* Export selected customer details as PDF using browser print/save
* Responsive enterprise-style UI

---

## Backend Application

Backend folder:

```text
customer360-backend
```

Backend runs on:

```text
http://localhost:8080
```

Swagger URL:

```text
http://localhost:8080/swagger-ui.html
```

Main backend APIs:

| Method | Endpoint                              | Description                              |
| ------ | ------------------------------------- | ---------------------------------------- |
| GET    | `/api/customers`                      | Get all consolidated customers           |
| GET    | `/api/customers/{customerId}`         | Get selected customer details            |
| GET    | `/api/customers/{customerId}/summary` | Generate AI or fallback customer summary |
| GET    | `/api/customers/export/csv`           | Export full customer data as CSV         |
| GET    | `/api/customers/export/excel`         | Export full customer data as Excel       |
| GET    | `/api/customers/export/pdf`           | Export full customer data as PDF         |

---

## Frontend Application

Frontend folder:

```text
customer360-frontend
```

Frontend runs on:

```text
http://localhost:5173
```

The frontend communicates with backend using:

```text
http://localhost:8080/api/customers
```

---

## AI Integration

Customer360 uses Ollama local AI for generating professional customer summaries.

Configured model:

```text
llama3.2
```

Ollama URL:

```text
http://localhost:11434/api/generate
```

If Ollama is unavailable, the backend automatically returns a rule-based fallback summary.

This ensures the summary API continues to work even when local AI is not running.

---

## How to Run the Project

### 1. Start Backend

Open PowerShell:

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-backend
.\mvnw.cmd spring-boot:run
```

Backend will start at:

```text
http://localhost:8080
```

---

### 2. Start Frontend

Open another PowerShell window:

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-frontend
npm install
npm run dev
```

Frontend will start at:

```text
http://localhost:5173
```

---

### 3. Start or Verify Ollama

Check Ollama:

```powershell
ollama --version
ollama list
```

Pull model if required:

```powershell
ollama pull llama3.2
```

Ollama normally runs at:

```text
http://localhost:11434
```

---

## Environment Variable

MongoDB connection string is configured through environment variable:

```text
MONGODB_URI
```

Example:

```text
mongodb+srv://<username>:<password>@<cluster-url>/customer360_db
```

The actual MongoDB URI and password should not be committed to GitHub.

---

## Documentation

All project documents are available in the `docs` folder.

| Document                     | File                                               |
| ---------------------------- | -------------------------------------------------- |
| Requirement Document         | `docs/Customer360_Requirement_Document.md`         |
| Architecture Document        | `docs/Customer360_Architecture_Document.md`        |
| Workflow Document            | `docs/Customer360_Workflow_Document.md`            |
| Testing Document             | `docs/Customer360_Testing_Document.md`             |
| Deployment Setup Document    | `docs/Customer360_Deployment_Setup_Document.md`    |
| User Manual                  | `docs/Customer360_User_Manual.md`                  |
| Project Submission Checklist | `docs/Customer360_Project_Submission_Checklist.md` |

---

## Testing

Run backend tests:

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-backend
.\mvnw.cmd test
```

Expected result:

```text
BUILD SUCCESS
```

Unit tests cover:

* Masking service
* Customer consolidation service
* Customer summary service
* AI fallback behavior
* Search/filter/sort logic
* Missing data warnings

---

## Demo Flow

Recommended project demo flow:

```text
1. Open GitHub repository
2. Show project structure
3. Open docs folder
4. Show Requirement Document
5. Show Architecture Document
6. Start backend
7. Open Swagger
8. Test /api/customers
9. Test /api/customers/C1001
10. Test /api/customers/C1001/summary
11. Start frontend
12. Show customer list
13. Search and filter customers
14. Sort by total amount
15. View customer details
16. Generate AI Summary
17. Export full customer data
18. Export selected customer detail
19. Show unit test BUILD SUCCESS
```

---

## Current Project Status

| Area                  | Status    |
| --------------------- | --------- |
| Backend development   | Completed |
| Frontend development  | Completed |
| MongoDB integration   | Completed |
| CSV integration       | Completed |
| JSON integration      | Completed |
| Ollama AI integration | Completed |
| Rule-based fallback   | Completed |
| Search/filter/sort    | Completed |
| Export features       | Completed |
| Swagger documentation | Completed |
| Unit testing          | Completed |
| Project documentation | Completed |
| GitHub complete repo  | Completed |

---

## Known Limitations

The current version does not include:

* User login
* Role-based access control
* Pagination
* Production deployment
* Docker setup
* Frontend automated tests
* Cloud AI integration

These can be considered as future enhancements.

---

## Author

Neeharika D
