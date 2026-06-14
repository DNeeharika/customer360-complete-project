# Customer360 Architecture Document

## 1. Document Information

| Item           | Details                   |
| -------------- | ------------------------- |
| Project Name   | Customer360               |
| Module         | Customer Data Integration |
| Document Type  | Architecture Document     |
| Version        | 1.0                       |
| Backend        | Spring Boot               |
| Frontend       | React                     |
| Database       | MongoDB                   |
| AI Integration | Ollama Local AI           |

---

## 2. Architecture Objective

The objective of this architecture is to define the technical design of the Customer360 application.

Customer360 consolidates customer data from multiple sources and provides a unified view through a React frontend and Spring Boot backend.

The architecture supports:

* MongoDB profile data integration
* CSV order data integration
* JSON preference data integration
* Customer data consolidation using `customerId`
* Search, filter, and sort
* Full customer data export
* Selected customer detail export
* AI-powered customer summary using Ollama local AI
* Rule-based summary fallback
* Swagger/OpenAPI documentation
* Structured logging
* Global exception handling
* Unit testing

---

## 3. High-Level Architecture

```text
+---------------------------+
|       React Frontend      |
|  Customer360 UI           |
|  Search / Filter / Sort   |
|  Detail View / Export     |
+-------------+-------------+
              |
              | HTTP / REST API
              |
+-------------v-------------+
|     Spring Boot Backend   |
|  Controllers              |
|  Services                 |
|  Loaders                  |
|  Export Services          |
|  AI Summary Service       |
+------+------+-------------+
       |      |
       |      |
       |      +-----------------------------+
       |                                    |
+------v------+     +----------------+     +----------------+
|  MongoDB    |     | CSV File       |     | JSON File      |
|  Profiles   |     | Orders         |     | Preferences    |
+-------------+     +----------------+     +----------------+
       |
       |
+------v-------------------+
| Ollama Local AI          |
| llama3.2 Model           |
| AI Summary Generation    |
+--------------------------+
```

---

## 4. Application Components

The system is divided into two main applications:

| Component            | Technology   | Purpose                                                               |
| -------------------- | ------------ | --------------------------------------------------------------------- |
| Customer360 Backend  | Spring Boot  | Provides REST APIs, data consolidation, export, AI summary            |
| Customer360 Frontend | React + Vite | Provides user interface for customer search, details, summary, export |

---

## 5. Backend Architecture

The backend follows a layered architecture.

```text
Controller Layer
      |
Service Layer
      |
Repository / Loader / Export / AI Layer
      |
MongoDB / CSV / JSON / Ollama
```

### 5.1 Backend Package Structure

```text
com.customer360.backend
  config
  controller
  dto
  exception
  export
  loader
  model
  repository
  service
```

### 5.2 Package Responsibilities

| Package      | Responsibility                                     |
| ------------ | -------------------------------------------------- |
| `config`     | MongoDB, CORS, Swagger/OpenAPI configuration       |
| `controller` | REST API endpoints                                 |
| `dto`        | API response objects                               |
| `exception`  | Global exception handling and error response       |
| `export`     | CSV, Excel, PDF export logic                       |
| `loader`     | CSV and JSON file loading                          |
| `model`      | Domain/data models                                 |
| `repository` | MongoDB repository interface                       |
| `service`    | Business logic, consolidation, masking, AI summary |

---

## 6. Backend Main Classes

### 6.1 Configuration Classes

| Class           | Purpose                                                                    |
| --------------- | -------------------------------------------------------------------------- |
| `MongoConfig`   | Reads MongoDB URI from environment variable and creates MongoDB connection |
| `CorsConfig`    | Allows frontend running on `http://localhost:5173` to access backend APIs  |
| `OpenApiConfig` | Configures Swagger title, description, version, and contact                |

---

### 6.2 Controller Classes

| Class                      | Purpose                                                      |
| -------------------------- | ------------------------------------------------------------ |
| `CustomerController`       | Provides customer list, customer detail, and AI summary APIs |
| `CustomerExportController` | Provides full customer data export APIs                      |

---

### 6.3 Service Classes

| Class                          | Purpose                                                             |
| ------------------------------ | ------------------------------------------------------------------- |
| `CustomerConsolidationService` | Consolidates profile, order, and preference data using `customerId` |
| `CustomerSummaryService`       | Generates customer summary using AI or fallback logic               |
| `AiSummaryService`             | Calls Ollama local AI API                                           |
| `MaskingService`               | Masks email and mobile number                                       |

---

### 6.4 Loader Classes

| Class                  | Purpose                                                              |
| ---------------------- | -------------------------------------------------------------------- |
| `OrderCsvLoader`       | Loads order data from CSV file at application startup                |
| `PreferenceJsonLoader` | Loads customer preference data from JSON file at application startup |
| `DataCache`            | Stores CSV and JSON data in memory for fast access                   |

---

### 6.5 Export Classes

| Class                   | Purpose                                                           |
| ----------------------- | ----------------------------------------------------------------- |
| `CustomerExportService` | Generates CSV, Excel, and PDF export files for full customer list |

---

## 7. Frontend Architecture

The frontend is built using React and Vite.

```text
React UI
  |
CustomerListPage.jsx
  |
customerApi.js
  |
Backend REST APIs
```

### 7.1 Frontend Structure

```text
customer360-frontend
  src
    api
      customerApi.js
    pages
      CustomerListPage.jsx
    App.jsx
    App.css
    index.css
    main.jsx
```

### 7.2 Frontend Responsibilities

| File                   | Responsibility                                             |
| ---------------------- | ---------------------------------------------------------- |
| `customerApi.js`       | Centralized API calls using Axios                          |
| `CustomerListPage.jsx` | Main customer list, filter, detail, summary, and export UI |
| `App.jsx`              | Loads main page                                            |
| `App.css`              | Application styling                                        |
| `index.css`            | Global page styling                                        |

---

## 8. Data Architecture

Customer360 uses three main input sources.

### 8.1 MongoDB Customer Profile Data

MongoDB stores customer profile details.

| Field        | Description                |
| ------------ | -------------------------- |
| `customerId` | Unique customer identifier |
| `name`       | Customer name              |
| `email`      | Customer email             |
| `mobile`     | Customer mobile            |
| `city`       | Customer city              |

---

### 8.2 CSV Customer Order Data

CSV stores customer order data.

| Field        | Description         |
| ------------ | ------------------- |
| `customerId` | Customer identifier |
| `orderId`    | Order identifier    |
| `orderDate`  | Order date          |
| `amount`     | Order amount        |

CSV file path:

```text
src/main/resources/data/customer_orders.csv
```

---

### 8.3 JSON Customer Preference Data

JSON stores customer preference data.

| Field              | Description                     |
| ------------------ | ------------------------------- |
| `customerId`       | Customer identifier             |
| `membership`       | Membership level                |
| `preferredChannel` | Preferred communication channel |

JSON file path:

```text
src/main/resources/data/customer_preferences.json
```

---

## 9. Data Consolidation Logic

The system uses `customerId` as the common key.

```text
MongoDB customer_profile.customerId
          +
CSV customer_orders.customerId
          +
JSON customer_preferences.customerId
          =
Consolidated Customer Detail
```

### 9.1 Consolidated Response

The consolidated customer response includes:

* Customer ID
* Name
* Masked email
* Masked mobile
* City
* Membership
* Preferred channel
* Orders
* Total orders
* Total order amount
* Warnings

---

## 10. Data Loading Flow

### 10.1 Application Startup Flow

```text
Application Starts
      |
OrderCsvLoader loads CSV orders
      |
PreferenceJsonLoader loads JSON preferences
      |
DataCache stores orders and preferences in memory
      |
MongoDB profile data is read through repository when APIs are called
```

### 10.2 Reason for In-Memory Cache

CSV and JSON data are loaded into memory because:

* Data files are small for this project
* Lookup by `customerId` becomes simple and fast
* It avoids repeatedly reading files for every API request

---

## 11. API Architecture

### 11.1 Customer APIs

| Method | Endpoint                              | Purpose                                  |
| ------ | ------------------------------------- | ---------------------------------------- |
| GET    | `/api/customers`                      | Get all consolidated customers           |
| GET    | `/api/customers/{customerId}`         | Get selected customer detail             |
| GET    | `/api/customers/{customerId}/summary` | Generate AI or fallback customer summary |

---

### 11.2 Export APIs

| Method | Endpoint                      | Purpose                            |
| ------ | ----------------------------- | ---------------------------------- |
| GET    | `/api/customers/export/csv`   | Export full customer list as CSV   |
| GET    | `/api/customers/export/excel` | Export full customer list as Excel |
| GET    | `/api/customers/export/pdf`   | Export full customer list as PDF   |

---

### 11.3 Search, Filter, and Sort

The `/api/customers` endpoint supports query parameters.

| Parameter          | Purpose                                                             |
| ------------------ | ------------------------------------------------------------------- |
| `search`           | Search by customer ID, name, city, membership, or preferred channel |
| `city`             | Filter by city                                                      |
| `membership`       | Filter by membership                                                |
| `preferredChannel` | Filter by preferred channel                                         |
| `sortBy`           | Sort field                                                          |
| `sortDir`          | Sort direction                                                      |

Example:

```text
GET /api/customers?city=Hyderabad&membership=Gold&sortBy=totalOrderAmount&sortDir=desc
```

---

## 12. AI Summary Architecture

Customer360 integrates with Ollama local AI for generating customer summaries.

### 12.1 AI Summary Flow

```text
Frontend clicks Generate AI Summary
      |
GET /api/customers/{customerId}/summary
      |
CustomerSummaryService
      |
CustomerConsolidationService gets customer details
      |
AiSummaryService sends prompt to Ollama
      |
Ollama returns generated summary
      |
Backend returns summary to frontend
```

### 12.2 Ollama Configuration

```properties
ai.summary.enabled=true
ai.ollama.url=http://localhost:11434/api/generate
ai.ollama.model=llama3.2
```

### 12.3 AI Fallback Flow

```text
Ollama unavailable / error / blank response
      |
AiSummaryService throws exception
      |
CustomerSummaryService catches exception
      |
Rule-based summary is generated
      |
API still returns successful response
```

### 12.4 Reason for Fallback

Fallback is important because:

* Demo should not fail if Ollama is stopped
* API should remain reliable
* AI is an enhancement, not a hard dependency

---

## 13. Export Architecture

### 13.1 Full Customer Export

Full customer export is handled by backend.

```text
Frontend Export Button
      |
GET /api/customers/export/{type}
      |
CustomerExportController
      |
CustomerExportService
      |
CSV / Excel / PDF generated
      |
File downloaded
```

Supported backend exports:

| Format | Library / Method  |
| ------ | ----------------- |
| CSV    | String generation |
| Excel  | Apache POI        |
| PDF    | OpenPDF           |

---

### 13.2 Selected Customer Detail Export

Selected customer detail export is handled in frontend.

```text
User selects customer
      |
Customer detail loaded
      |
User clicks Export Detail CSV / PDF
      |
Frontend generates selected customer export
```

Supported selected detail exports:

| Format | Method                      |
| ------ | --------------------------- |
| CSV    | Browser Blob download       |
| PDF    | Browser print / Save as PDF |

Selected customer export includes:

* Customer profile
* Orders
* Warnings
* Customer summary

---

## 14. Security Architecture

### 14.1 Credential Security

MongoDB connection string is not stored in source code.

It is read from environment variable:

```text
MONGODB_URI
```

### 14.2 Data Masking

Sensitive data is masked before returning API response.

| Field  | Masking Rule                                         |
| ------ | ---------------------------------------------------- |
| Email  | First character visible, remaining local part masked |
| Mobile | First 4 digits visible, remaining digits masked      |

Example:

```text
rahul.sharma@gmail.com → r***********@gmail.com
9876543210 → 9876******
```

### 14.3 AI Data Safety

The AI prompt is designed to avoid exposing sensitive data.

The prompt instructs AI not to mention:

* Masked email
* Masked mobile
* Unsupported assumptions
* Sensitive data

---

## 15. CORS Architecture

The frontend and backend run on different ports.

| Application | URL                     |
| ----------- | ----------------------- |
| Frontend    | `http://localhost:5173` |
| Backend     | `http://localhost:8080` |

CORS is configured in backend to allow frontend access:

```text
Allowed Origin: http://localhost:5173
Allowed Path: /api/**
```

---

## 16. Exception Handling Architecture

The backend uses global exception handling.

| Class                    | Purpose                        |
| ------------------------ | ------------------------------ |
| `GlobalExceptionHandler` | Handles exceptions globally    |
| `ApiErrorResponse`       | Standard error response format |

### 16.1 Error Response Format

```json
{
  "timestamp": "2026-06-14T01:33:34.2680746",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid sortBy value: wrongField",
  "path": "/api/customers"
}
```

### 16.2 Benefits

* Clean API error response
* No raw stack trace exposed to user
* Easier frontend error handling
* More professional API behavior

---

## 17. Logging Architecture

The backend uses SLF4J structured logging.

Logs are used for:

* CSV load success
* JSON load success
* Invalid CSV row warnings
* AI summary success
* AI summary fallback
* API errors

Example logs:

```text
Customer orders CSV loaded successfully. Loaded rows: 8, Skipped rows: 1
Customer preferences JSON loaded successfully. Loaded rows: 5, Skipped rows: 0
AI summary generated successfully for customerId: C1001
Bad request at path /api/customers: Invalid sortBy value: wrongField
```

---

## 18. Swagger/OpenAPI Architecture

Swagger is configured using Springdoc OpenAPI.

Swagger UI URL:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON URL:

```text
http://localhost:8080/v3/api-docs
```

Swagger provides:

* API title
* API version
* API description
* Controller tags
* Endpoint summaries
* Parameter descriptions
* Response schema details

---

## 19. Testing Architecture

The backend includes unit tests for core business logic.

| Test Class                           | Purpose                                                  |
| ------------------------------------ | -------------------------------------------------------- |
| `MaskingServiceTest`                 | Tests email and mobile masking                           |
| `CustomerSummaryServiceTest`         | Tests AI summary fallback and AI response behavior       |
| `CustomerConsolidationServiceTest`   | Tests data consolidation, warnings, search, filter, sort |
| `Customer360BackendApplicationTests` | Basic application class test                             |

Run all tests:

```powershell
.\mvnw.cmd test
```

Expected result:

```text
BUILD SUCCESS
```

---

## 20. Deployment View

### 20.1 Local Development Deployment

```text
Developer Laptop
  |
  +-- MongoDB Atlas
  |
  +-- Ollama Local AI running on localhost:11434
  |
  +-- Spring Boot Backend running on localhost:8080
  |
  +-- React Frontend running on localhost:5173
```

### 20.2 Runtime URLs

| Component | URL                                     |
| --------- | --------------------------------------- |
| Frontend  | `http://localhost:5173`                 |
| Backend   | `http://localhost:8080`                 |
| Swagger   | `http://localhost:8080/swagger-ui.html` |
| Ollama    | `http://localhost:11434`                |
| MongoDB   | MongoDB Atlas Cloud                     |

---

## 21. Design Decisions

| Decision                                         | Reason                                         |
| ------------------------------------------------ | ---------------------------------------------- |
| React frontend and Spring Boot backend separated | Clear separation of UI and API                 |
| MongoDB URI from environment variable            | Avoid secrets in source code                   |
| CSV and JSON loaded at startup                   | Simple and efficient for project scope         |
| Data matched by `customerId`                     | Common key across all data sources             |
| Rule-based AI fallback                           | Prevent API failure when Ollama is unavailable |
| Swagger added                                    | Easy API testing and documentation             |
| Selected detail export handled in frontend       | Avoid extra backend complexity                 |
| Sensitive data masking                           | Improve privacy and security                   |
| Global exception handling                        | Standardized API error response                |
| Unit tests added                                 | Validate core business logic                   |

---

## 22. Risks and Mitigations

| Risk                                  | Impact                       | Mitigation                                                |
| ------------------------------------- | ---------------------------- | --------------------------------------------------------- |
| Ollama not running                    | AI summary may fail          | Rule-based fallback summary                               |
| MongoDB URI missing                   | Backend startup failure      | Environment variable setup documented                     |
| Invalid CSV data                      | Incorrect totals             | Invalid rows skipped and logged                           |
| Missing profile/order/preference data | Incomplete customer view     | Data quality warnings shown                               |
| Browser CORS issue                    | Frontend cannot call backend | Backend CORS configuration added                          |
| Large dataset                         | Slow response                | Future enhancement: pagination and database-backed orders |

---

## 23. Future Architecture Enhancements

Future enhancements may include:

* Authentication and role-based access control
* Docker deployment
* Cloud deployment
* Backend-generated selected customer PDF export
* Pagination for large datasets
* Database-backed orders instead of CSV
* Database-backed preferences instead of JSON
* Audit logging
* Frontend unit tests
* CI/CD pipeline
* Environment-specific configurations
* Production AI model configuration

---

## 24. Conclusion

The Customer360 architecture provides a clear separation between frontend, backend, data sources, and AI integration.

The backend consolidates data from MongoDB, CSV, and JSON sources, provides REST APIs, generates exports, and integrates with Ollama local AI for customer summaries. The frontend provides a compact and user-friendly interface for viewing, searching, filtering, sorting, exporting, and summarizing customer data.

The architecture is suitable for project demonstration, review, and future enhancement.
