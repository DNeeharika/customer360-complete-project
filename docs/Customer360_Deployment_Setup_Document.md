# Customer360 Deployment Setup Document

## 1. Document Information

| Item           | Details                   |
| -------------- | ------------------------- |
| Project Name   | Customer360               |
| Module         | Customer Data Integration |
| Document Type  | Deployment Setup Document |
| Version        | 1.0                       |
| Backend        | Spring Boot               |
| Frontend       | React                     |
| Database       | MongoDB Atlas             |
| AI Integration | Ollama Local AI           |

---

## 2. Deployment Objective

The objective of this document is to explain how to set up, configure, run, test, and validate the Customer360 application in a local development environment.

This document covers:

* Required software installation
* Backend setup
* Frontend setup
* MongoDB setup
* Ollama AI setup
* Environment variable setup
* Application run commands
* Testing commands
* Swagger validation
* GitHub commit and push process
* Troubleshooting steps

---

## 3. Application Components

Customer360 contains two main projects:

| Component | Folder                 | Purpose                                                      |
| --------- | ---------------------- | ------------------------------------------------------------ |
| Backend   | `customer360-backend`  | Spring Boot REST API, data consolidation, export, AI summary |
| Frontend  | `customer360-frontend` | React UI for customer list, details, summary, and export     |

Main project folder:

```text
C:\Users\neeha\Downloads\customer360
```

Expected folder structure:

```text
customer360
  customer360-backend
  customer360-frontend
  docs
```

---

## 4. Prerequisites

The following software should be installed before running the application.

| Software                | Purpose                       |
| ----------------------- | ----------------------------- |
| Java JDK 25             | Run Spring Boot backend       |
| IntelliJ IDEA Community | Backend development           |
| Node.js                 | Run React frontend            |
| npm                     | Install frontend dependencies |
| MongoDB Atlas Account   | Cloud database                |
| Ollama                  | Local AI summary generation   |
| Git                     | Version control               |
| Browser                 | Access frontend and Swagger   |

---

## 5. Backend Setup

### 5.1 Backend Project Path

```text
C:\Users\neeha\Downloads\customer360\customer360-backend
```

### 5.2 Open Backend in IntelliJ

Open IntelliJ IDEA and select:

```text
C:\Users\neeha\Downloads\customer360\customer360-backend
```

Wait for Maven dependencies to load.

---

## 6. MongoDB Atlas Setup

### 6.1 Database Details

| Item       | Value              |
| ---------- | ------------------ |
| Database   | `customer360_db`   |
| Collection | `customer_profile` |

### 6.2 Required Collection

MongoDB collection name:

```text
customer_profile
```

### 6.3 Sample MongoDB Document

```json
{
  "customerId": "C1001",
  "name": "Rahul Sharma",
  "email": "rahul.sharma@gmail.com",
  "mobile": "9876543210",
  "city": "Hyderabad"
}
```

### 6.4 MongoDB Connection String

The MongoDB URI should not be stored directly in source code.

It should be configured as an environment variable:

```text
MONGODB_URI
```

Example format:

```text
mongodb+srv://<username>:<password>@<cluster-url>/customer360_db
```

---

## 7. Environment Variable Setup

### 7.1 Set MongoDB URI in Windows PowerShell

Run the following command in PowerShell:

```powershell
setx MONGODB_URI "mongodb+srv://<username>:<password>@<cluster-url>/customer360_db"
```

After setting the environment variable:

1. Close IntelliJ
2. Close PowerShell
3. Reopen IntelliJ or PowerShell
4. Run the backend again

### 7.2 Verify Environment Variable

Run:

```powershell
echo $env:MONGODB_URI
```

Expected result:

```text
MongoDB connection string should be displayed
```

---

## 8. Backend Application Properties

File path:

```text
customer360-backend\src\main\resources\application.properties
```

Important properties:

```properties
spring.application.name=customer360-backend
server.port=8080

springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha

ai.summary.enabled=true
ai.ollama.url=http://localhost:11434/api/generate
ai.ollama.model=llama3.2
```

---

## 9. Backend Run Steps

### 9.1 Run Backend Using PowerShell

Open PowerShell:

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-backend
```

Run:

```powershell
.\mvnw.cmd spring-boot:run
```

### 9.2 Run Backend Using IntelliJ

Open:

```text
Customer360BackendApplication.java
```

Click:

```text
Run
```

### 9.3 Backend URL

Backend runs on:

```text
http://localhost:8080
```

### 9.4 Validate Backend API

Open browser:

```text
http://localhost:8080/api/customers
```

Expected result:

```text
Customer list JSON should be displayed
```

---

## 10. Swagger Setup and Validation

Swagger UI URL:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON URL:

```text
http://localhost:8080/v3/api-docs
```

Expected Swagger sections:

```text
Customer APIs
Customer Export APIs
Schemas
```

---

## 11. Ollama Local AI Setup

Customer360 uses Ollama local AI for generating AI-powered customer summaries.

### 11.1 Check Ollama Version

```powershell
ollama --version
```

### 11.2 Pull Required Model

```powershell
ollama pull llama3.2
```

### 11.3 Check Installed Models

```powershell
ollama list
```

Expected model:

```text
llama3.2
```

### 11.4 Ollama Runtime URL

Ollama normally runs on:

```text
http://localhost:11434
```

### 11.5 Important Note

If this command gives a port already in use message:

```powershell
ollama serve
```

It means Ollama is already running.

No further action is needed.

---

## 12. AI Summary Validation

### 12.1 Test AI Summary API

Open browser:

```text
http://localhost:8080/api/customers/C1001/summary
```

Expected response:

```json
{
  "customerId": "C1001",
  "summary": "Rahul Sharma is a Gold member from Hyderabad. The customer has placed 2 orders with a total order value of ₹3700. The preferred communication channel is Email, and there are no data quality warnings for this customer."
}
```

### 12.2 AI Fallback Validation

To test fallback:

1. Stop Ollama if possible
2. Call summary API again

```text
http://localhost:8080/api/customers/C1001/summary
```

Expected result:

```text
Rule-based summary should be returned instead of API failure
```

---

## 13. Frontend Setup

### 13.1 Frontend Project Path

```text
C:\Users\neeha\Downloads\customer360\customer360-frontend
```

### 13.2 Open Frontend Folder in PowerShell

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-frontend
```

### 13.3 Install Dependencies

```powershell
npm install
```

### 13.4 Run Frontend

```powershell
npm run dev
```

### 13.5 Frontend URL

Open browser:

```text
http://localhost:5173
```

Expected result:

```text
Customer360 UI should load
```

---

## 14. Backend and Frontend Communication

| Application | URL                     |
| ----------- | ----------------------- |
| Backend     | `http://localhost:8080` |
| Frontend    | `http://localhost:5173` |

Frontend API base URL:

```javascript
const API_BASE_URL = "http://localhost:8080/api/customers";
```

Backend CORS allows frontend requests from:

```text
http://localhost:5173
```

---

## 15. Data Files Setup

### 15.1 CSV File Path

```text
customer360-backend\src\main\resources\data\customer_orders.csv
```

### 15.2 JSON File Path

```text
customer360-backend\src\main\resources\data\customer_preferences.json
```

### 15.3 Important Note

If CSV or JSON data is changed, restart the backend application because data is loaded during backend startup.

---

## 16. Backend Build Command

From backend project folder:

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-backend
```

Run:

```powershell
.\mvnw.cmd clean package
```

Expected result:

```text
BUILD SUCCESS
```

---

## 17. Backend Test Command

Run all backend unit tests:

```powershell
.\mvnw.cmd test
```

Expected result:

```text
BUILD SUCCESS
```

Test classes:

| Test Class                           | Purpose                             |
| ------------------------------------ | ----------------------------------- |
| `MaskingServiceTest`                 | Tests email and mobile masking      |
| `CustomerSummaryServiceTest`         | Tests AI summary and fallback logic |
| `CustomerConsolidationServiceTest`   | Tests customer data consolidation   |
| `Customer360BackendApplicationTests` | Basic application class test        |

---

## 18. Run Backend JAR

After building the backend, run:

```powershell
java -jar target/customer360-backend-0.0.1-SNAPSHOT.jar
```

Before running the JAR, ensure:

```text
MONGODB_URI is configured
```

---

## 19. Frontend Build Command

From frontend project folder:

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-frontend
```

Run:

```powershell
npm run build
```

Expected output folder:

```text
dist
```

---

## 20. Frontend Preview Command

After building frontend:

```powershell
npm run preview
```

Expected result:

```text
Frontend preview server should start
```

---

## 21. Full Application Startup Order

Use the following order to run the full application:

```text
1. Ensure MongoDB Atlas is accessible
2. Ensure MONGODB_URI environment variable is configured
3. Ensure Ollama is running locally
4. Start Spring Boot backend
5. Start React frontend
6. Open frontend URL in browser
```

---

## 22. Validation Checklist

| Step                      | Validation                                     |
| ------------------------- | ---------------------------------------------- |
| MongoDB URI configured    | `echo $env:MONGODB_URI` returns value          |
| Backend starts            | No startup error                               |
| Customer API works        | `/api/customers` returns data                  |
| Swagger opens             | `/swagger-ui.html` opens                       |
| Ollama model exists       | `ollama list` shows `llama3.2`                 |
| AI summary works          | `/api/customers/C1001/summary` returns summary |
| Frontend starts           | `http://localhost:5173` opens                  |
| Customer list displays    | UI table shows customers                       |
| View button works         | Detail panel opens                             |
| Generate AI Summary works | Summary appears                                |
| Export CSV works          | File downloads                                 |
| Export Excel works        | File downloads                                 |
| Export PDF works          | File downloads                                 |
| Export Detail CSV works   | Selected customer CSV downloads                |
| Export Detail PDF works   | Browser print dialog opens                     |

---

## 23. GitHub Commit and Push Process

### 23.1 Backend Commit

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-backend
git status
git add .
git commit -m "Update backend changes"
git push
```

### 23.2 Frontend Commit

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-frontend
git status
git add .
git commit -m "Update frontend changes"
git push
```

### 23.3 Documentation Commit

If documentation is maintained in the main project folder, commit from the relevant Git repository if configured.

If `docs` is outside backend and frontend Git repositories, either:

* Create a separate documentation repository, or
* Copy docs into one of the existing repositories, or
* Commit docs from the parent repository if it is initialized as a Git repository

---

## 24. Troubleshooting

### 24.1 Backend Does Not Start

Possible causes:

| Issue                      | Solution                  |
| -------------------------- | ------------------------- |
| `MONGODB_URI` missing      | Set environment variable  |
| MongoDB password incorrect | Verify Atlas URI          |
| Network issue              | Check internet connection |
| Java version issue         | Verify JDK 25             |

---

### 24.2 MongoDB Connection Error

Check:

```powershell
echo $env:MONGODB_URI
```

Also verify:

* MongoDB username
* MongoDB password
* Atlas network access
* Database name
* Cluster availability

---

### 24.3 Frontend Cannot Call Backend

Possible cause:

```text
CORS issue
```

Solution:

* Confirm backend is running on `http://localhost:8080`
* Confirm frontend is running on `http://localhost:5173`
* Confirm backend CORS allows frontend origin

---

### 24.4 Ollama Port Already in Use

If this message appears:

```text
listen tcp 127.0.0.1:11434: bind: Only one usage of each socket address is normally permitted
```

It means Ollama is already running.

No action required.

---

### 24.5 AI Summary Not Generated

Check:

```powershell
ollama list
```

Make sure the model exists:

```text
llama3.2
```

Check backend properties:

```properties
ai.summary.enabled=true
ai.ollama.url=http://localhost:11434/api/generate
ai.ollama.model=llama3.2
```

If Ollama still fails, backend should return fallback summary.

---

### 24.6 Frontend Page Blank

Run:

```powershell
npm install
npm run dev
```

Check browser developer console for errors.

Also confirm backend API is working:

```text
http://localhost:8080/api/customers
```

---

### 24.7 Export Not Downloading

Check:

* Backend is running
* Browser pop-up/download is not blocked
* Export API opens directly in browser
* Network tab does not show 500 error

Test export directly:

```text
http://localhost:8080/api/customers/export/csv
```

---

## 25. Local Deployment Summary

| Component         | Command                      |
| ----------------- | ---------------------------- |
| Backend Run       | `.\mvnw.cmd spring-boot:run` |
| Backend Test      | `.\mvnw.cmd test`            |
| Backend Build     | `.\mvnw.cmd clean package`   |
| Frontend Install  | `npm install`                |
| Frontend Run      | `npm run dev`                |
| Frontend Build    | `npm run build`              |
| Ollama Model Pull | `ollama pull llama3.2`       |
| Ollama Model List | `ollama list`                |

---

## 26. Current Deployment Status

| Area                       | Status    |
| -------------------------- | --------- |
| Backend local setup        | Completed |
| Frontend local setup       | Completed |
| MongoDB Atlas setup        | Completed |
| Ollama local AI setup      | Completed |
| Environment variable setup | Completed |
| Swagger validation         | Completed |
| API validation             | Completed |
| Frontend validation        | Completed |
| Export validation          | Completed |
| Unit test execution        | Completed |

---

## 27. Conclusion

The Customer360 application can be deployed and run locally using Spring Boot backend, React frontend, MongoDB Atlas, and Ollama local AI.

The local setup supports complete customer data consolidation, customer list view, detail view, search, filter, sort, export, AI-powered summary generation, and fallback summary behavior.

This setup is suitable for development, testing, demonstration, and project review.
