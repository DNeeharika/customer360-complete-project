# Customer360 Backend

Customer360 Backend is a Spring Boot application that consolidates customer information from multiple data sources and exposes REST APIs for search, filtering, sorting, export, and AI-powered customer summary generation.

## Project Objective

The objective of this project is to build a backend application that reads customer data from different sources, matches the data using a common `customerId`, and provides a consolidated customer profile.

The system reads data from:

| Source          | Data Type            | Purpose                                     |
| --------------- | -------------------- | ------------------------------------------- |
| MongoDB         | Customer profile     | Name, email, mobile, city                   |
| CSV File        | Customer orders      | Order ID, order date, amount                |
| JSON File       | Customer preferences | Membership, preferred communication channel |
| Ollama Local AI | AI summary           | Professional customer summary generation    |

## Features Implemented

* Read customer profile data from MongoDB
* Read customer order data from CSV file
* Read customer preference data from JSON file
* Match data using `customerId`
* Display consolidated customer profile
* Show customer orders
* Calculate total order amount
* Show membership information
* Show preferred communication channel
* Search customers
* Filter customers by city, membership, and preferred channel
* Sort customers by customer ID, name, city, membership, preferred channel, total orders, and total order amount
* Export customer data to CSV
* Export customer data to Excel
* Export customer data to PDF
* Generate AI-powered customer summary using Ollama local AI
* Rule-based customer summary fallback when Ollama is unavailable
* Mask sensitive customer email and mobile number
* Handle missing profile, order, and preference data with warnings
* Handle invalid CSV data
* Global exception handling
* Structured logging
* Swagger/OpenAPI documentation
* Unit tests for core business logic

## Technology Stack

| Layer             | Technology                 |
| ----------------- | -------------------------- |
| Backend           | Java 25, Spring Boot 4.1.0 |
| Database          | MongoDB Atlas              |
| Data Files        | CSV, JSON                  |
| AI Integration    | Ollama Local AI            |
| API Documentation | Swagger/OpenAPI            |
| Build Tool        | Maven                      |
| Testing           | JUnit 5, Mockito           |
| Export            | Apache POI, OpenPDF        |
| Version Control   | Git, GitHub                |

## Project Structure

```text
customer360-backend
  src/main/java/com/customer360/backend
    config
      CorsConfig.java
      MongoConfig.java
      OpenApiConfig.java
    controller
      CustomerController.java
      CustomerExportController.java
    dto
      CustomerDetailResponse.java
      CustomerSummaryResponse.java
      OrderResponse.java
    exception
      ApiErrorResponse.java
      GlobalExceptionHandler.java
    export
      CustomerExportService.java
    loader
      DataCache.java
      OrderCsvLoader.java
      PreferenceJsonLoader.java
    model
      CustomerOrder.java
      CustomerPreference.java
      CustomerProfile.java
    repository
      CustomerProfileRepository.java
    service
      AiSummaryService.java
      CustomerConsolidationService.java
      CustomerSummaryService.java
      MaskingService.java
    Customer360BackendApplication.java

  src/main/resources
    application.properties
    data
      customer_orders.csv
      customer_preferences.json

  src/test/java/com/customer360/backend
    Customer360BackendApplicationTests.java
    service
      CustomerConsolidationServiceTest.java
      CustomerSummaryServiceTest.java
      MaskingServiceTest.java
```

## Data Sources

### MongoDB Collection

Database:

```text
customer360_db
```

Collection:

```text
customer_profile
```

Sample document:

```json
{
  "customerId": "C1001",
  "name": "Rahul Sharma",
  "email": "rahul.sharma@gmail.com",
  "mobile": "9876543210",
  "city": "Hyderabad"
}
```

### CSV File

File path:

```text
src/main/resources/data/customer_orders.csv
```

Sample data:

```csv
customerId,orderId,orderDate,amount
C1001,O1001,2026-01-10,2500
C1001,O1002,2026-02-15,1200
C1002,O1003,2026-01-20,5600
```

### JSON File

File path:

```text
src/main/resources/data/customer_preferences.json
```

Sample data:

```json
[
  {
    "customerId": "C1001",
    "membership": "Gold",
    "preferredChannel": "Email"
  }
]
```

## Environment Variable Setup

The MongoDB connection string is not stored in the source code.

Set the following environment variable before running the application:

```text
MONGODB_URI=<your MongoDB Atlas connection string>
```

Example:

```text
MONGODB_URI=mongodb+srv://<username>:<password>@<cluster-url>/customer360_db
```

Do not commit the actual MongoDB password or connection string to GitHub.

## Application Properties

The following important properties are configured in:

```text
src/main/resources/application.properties
```

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

## How to Run Locally

From the project root folder, run:

```powershell
.\mvnw.cmd spring-boot:run
```

Or run the main class from IntelliJ:

```text
Customer360BackendApplication.java
```

The application starts on:

```text
http://localhost:8080
```

## API Endpoints

### Customer APIs

| Method | Endpoint                              | Description                       |
| ------ | ------------------------------------- | --------------------------------- |
| GET    | `/api/customers`                      | Get all consolidated customers    |
| GET    | `/api/customers/{customerId}`         | Get consolidated customer details |
| GET    | `/api/customers/{customerId}/summary` | Get AI-powered customer summary   |

### Search, Filter, and Sort

Example search:

```text
GET /api/customers?search=rahul
```

Example filter:

```text
GET /api/customers?city=Hyderabad
```

Example multiple filters:

```text
GET /api/customers?city=Hyderabad&membership=Gold
```

Example sorting:

```text
GET /api/customers?sortBy=totalOrderAmount&sortDir=desc
```

Supported `sortBy` values:

```text
customerId
name
city
membership
preferredChannel
totalOrders
totalOrderAmount
```

Supported `sortDir` values:

```text
asc
desc
```

### Export APIs

| Method | Endpoint                      | Description               |
| ------ | ----------------------------- | ------------------------- |
| GET    | `/api/customers/export/csv`   | Export customers as CSV   |
| GET    | `/api/customers/export/excel` | Export customers as Excel |
| GET    | `/api/customers/export/pdf`   | Export customers as PDF   |

## Swagger/OpenAPI

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON is available at:

```text
http://localhost:8080/v3/api-docs
```

## Ollama Local AI Integration

The backend supports AI-powered customer summary generation using Ollama running locally.

The summary API first attempts to generate a summary using Ollama. If Ollama is unavailable, stopped, or returns an invalid response, the application automatically falls back to the rule-based summary logic.

### Ollama Setup

Check Ollama version:

```powershell
ollama --version
```

Pull the required model:

```powershell
ollama pull llama3.2
```

Check available models:

```powershell
ollama list
```

Ollama normally runs on:

```text
http://localhost:11434
```

If Ollama is already running, there is no need to run `ollama serve` again.

### AI Summary API

Endpoint:

```text
GET /api/customers/{customerId}/summary
```

Example:

```text
GET /api/customers/C1001/summary
```

Sample response:

```json
{
  "customerId": "C1001",
  "summary": "Rahul Sharma is a Gold member from Hyderabad. The customer has placed 2 orders with a total order value of ₹3700. The preferred communication channel is Email, and there are no data quality warnings for this customer."
}
```

### Fallback Behavior

If Ollama is not running or AI generation fails, the backend returns a rule-based summary instead of failing the API.

## Sample Customer Detail Response

```json
{
  "customerId": "C1001",
  "name": "Rahul Sharma",
  "email": "r***********@gmail.com",
  "mobile": "9876******",
  "city": "Hyderabad",
  "membership": "Gold",
  "preferredChannel": "Email",
  "orders": [
    {
      "orderId": "O1001",
      "orderDate": "2026-01-10",
      "amount": 2500
    },
    {
      "orderId": "O1002",
      "orderDate": "2026-02-15",
      "amount": 1200
    }
  ],
  "totalOrders": 2,
  "totalOrderAmount": 3700,
  "warnings": []
}
```

## Error Handling

The application uses global exception handling to return clean error responses.

Example invalid sort request:

```text
GET /api/customers?sortBy=wrongField
```

Sample response:

```json
{
  "timestamp": "2026-06-14T01:33:34.2680746",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid sortBy value: wrongField",
  "path": "/api/customers"
}
```

## Logging

The application uses structured logging with SLF4J.

Examples:

```text
Customer orders CSV loaded successfully. Loaded rows: 8, Skipped rows: 1
Customer preferences JSON loaded successfully. Loaded rows: 5, Skipped rows: 0
Invalid CSV data skipped: C1005,O1009,2026-04-20,invalid_amount
AI summary generated successfully for customerId: C1001
Bad request at path /api/customers: Invalid sortBy value: wrongField
```

## Data Quality Handling

| Scenario                     | Handling                                 |
| ---------------------------- | ---------------------------------------- |
| Missing MongoDB profile      | Returns `Not Available` and adds warning |
| Missing orders               | Returns zero orders and adds warning     |
| Missing preference           | Returns `Not Available` and adds warning |
| Invalid CSV amount           | Skips row and logs warning               |
| Negative order amount        | Skips row and logs warning               |
| Empty preference customer ID | Skips row and logs warning               |

## Security Measures

* MongoDB URI is read from environment variable
* Sensitive data is not stored in source code
* Email address is masked in API response
* Mobile number is masked in API response
* `.idea`, `target`, `.env`, and local property files are ignored using `.gitignore`
* Clean error responses are returned instead of exposing stack traces
* AI prompt is designed not to expose sensitive masked email or mobile information

## CORS Configuration

The backend allows the React frontend to access API endpoints from:

```text
http://localhost:5173
```

CORS is configured for:

```text
/api/**
```

## Unit Tests

Unit tests are implemented using JUnit 5 and Mockito.

| Test Class                           | Purpose                                                   | Status |
| ------------------------------------ | --------------------------------------------------------- | ------ |
| `MaskingServiceTest`                 | Tests email and mobile masking                            | Passed |
| `CustomerSummaryServiceTest`         | Tests AI summary fallback and AI response behavior        | Passed |
| `CustomerConsolidationServiceTest`   | Tests data merging, warnings, search, filter, and sorting | Passed |
| `Customer360BackendApplicationTests` | Basic application class test                              | Passed |

Run all tests:

```powershell
.\mvnw.cmd test
```

Expected result:

```text
BUILD SUCCESS
```

## Build Command

```powershell
.\mvnw.cmd clean package
```

## Run JAR

After building, run:

```powershell
java -jar target/customer360-backend-0.0.1-SNAPSHOT.jar
```

Ensure `MONGODB_URI` is configured before running the JAR.

## Current Status

| Area                        | Status    |
| --------------------------- | --------- |
| Backend APIs                | Completed |
| MongoDB Integration         | Completed |
| CSV Loader                  | Completed |
| JSON Loader                 | Completed |
| Data Consolidation          | Completed |
| Search, Filter, Sort        | Completed |
| Export CSV/Excel/PDF        | Completed |
| Ollama AI Summary           | Completed |
| Rule-Based Summary Fallback | Completed |
| Swagger/OpenAPI             | Completed |
| Global Exception Handling   | Completed |
| Structured Logging          | Completed |
| Unit Testing                | Completed |
| GitHub Push                 | Completed |

## Future Enhancements

* Add authentication and role-based access control
* Add Docker support
* Add deployment pipeline
* Add integration tests
* Add pagination for large customer datasets
* Add audit logging
* Add production profile configuration
* Add environment-specific AI configuration

## Author

Neeharika D
