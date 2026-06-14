# Customer360 Workflow Document

## 1. Document Information

| Item           | Details                   |
| -------------- | ------------------------- |
| Project Name   | Customer360               |
| Module         | Customer Data Integration |
| Document Type  | Workflow Document         |
| Version        | 1.0                       |
| Backend        | Spring Boot               |
| Frontend       | React                     |
| Database       | MongoDB                   |
| AI Integration | Ollama Local AI           |

---

## 2. Workflow Objective

The objective of this document is to explain the end-to-end workflow of the Customer360 application.

This document covers:

* Application startup workflow
* Data loading workflow
* Customer consolidation workflow
* Search/filter/sort workflow
* Customer detail workflow
* AI summary workflow
* Export workflow
* Error handling workflow
* Frontend user workflow

---

## 3. High-Level Application Workflow

```text
User opens Customer360 Frontend
        |
React frontend loads customer list
        |
Frontend calls Spring Boot backend API
        |
Backend reads MongoDB profile data
        |
Backend combines profile data with CSV orders and JSON preferences
        |
Backend returns consolidated customer data
        |
Frontend displays customer list
        |
User can search, filter, sort, view details, generate AI summary, and export data
```

---

## 4. Application Startup Workflow

When the backend application starts, the following steps happen:

```text
Start Spring Boot Application
        |
Load application.properties
        |
Read MONGODB_URI from environment variable
        |
Create MongoDB connection
        |
Load CSV order data
        |
Load JSON preference data
        |
Store CSV and JSON data in memory using DataCache
        |
Start Tomcat server on port 8080
        |
Backend APIs become available
```

### 4.1 Startup Responsibilities

| Step                | Component              | Description                                 |
| ------------------- | ---------------------- | ------------------------------------------- |
| Load MongoDB config | `MongoConfig`          | Reads MongoDB URI from environment variable |
| Load CSV file       | `OrderCsvLoader`       | Reads customer orders from CSV              |
| Load JSON file      | `PreferenceJsonLoader` | Reads customer preferences from JSON        |
| Store data          | `DataCache`            | Stores CSV and JSON data in memory          |
| Start APIs          | Spring Boot            | Exposes REST APIs                           |

---

## 5. CSV Order Loading Workflow

```text
Application starts
        |
OrderCsvLoader reads customer_orders.csv
        |
Header row is skipped
        |
Each row is validated
        |
Valid rows are converted into CustomerOrder objects
        |
Invalid rows are skipped and logged
        |
Orders are stored in DataCache by customerId
```

### 5.1 CSV Validation Rules

| Condition       | Action                   |
| --------------- | ------------------------ |
| Valid row       | Load into memory         |
| Invalid amount  | Skip row and log warning |
| Negative amount | Skip row and log warning |
| Missing columns | Skip row and log warning |

---

## 6. JSON Preference Loading Workflow

```text
Application starts
        |
PreferenceJsonLoader reads customer_preferences.json
        |
JSON is parsed into CustomerPreference objects
        |
Each preference is validated
        |
Valid preferences are stored in DataCache by customerId
        |
Invalid preferences are skipped and logged
```

### 6.1 JSON Validation Rules

| Condition            | Action                   |
| -------------------- | ------------------------ |
| Valid customerId     | Load into memory         |
| Missing customerId   | Skip and log warning     |
| Duplicate customerId | Latest value is retained |

---

## 7. Customer List Workflow

When the user opens the frontend, the customer list is loaded automatically.

```text
User opens http://localhost:5173
        |
React app loads CustomerListPage
        |
Frontend calls GET /api/customers
        |
Backend retrieves all customer profiles from MongoDB
        |
Backend matches each profile with orders from DataCache
        |
Backend matches each profile with preferences from DataCache
        |
Backend calculates total orders and total amount
        |
Backend masks email and mobile
        |
Backend returns consolidated customer list
        |
Frontend displays customers in table
```

### 7.1 Customer List API

```text
GET /api/customers
```

### 7.2 Customer List Output

The frontend table displays:

```text
Customer ID
Name
City
Membership
Preferred Channel
Total Orders
Total Amount
View Action
```

---

## 8. Customer Consolidation Workflow

The customer consolidation workflow combines data from MongoDB, CSV, and JSON.

```text
CustomerProfile from MongoDB
        |
Find orders by customerId from DataCache
        |
Find preference by customerId from DataCache
        |
Calculate total order amount
        |
Calculate total order count
        |
Mask email and mobile
        |
Add warnings if data is missing
        |
Return CustomerDetailResponse
```

### 8.1 Data Matching Key

All data sources are matched using:

```text
customerId
```

### 8.2 Consolidation Output

| Field             | Source                          |
| ----------------- | ------------------------------- |
| Customer ID       | MongoDB / input                 |
| Name              | MongoDB                         |
| Email             | MongoDB, masked before response |
| Mobile            | MongoDB, masked before response |
| City              | MongoDB                         |
| Orders            | CSV                             |
| Total orders      | Calculated from CSV             |
| Total amount      | Calculated from CSV             |
| Membership        | JSON                            |
| Preferred channel | JSON                            |
| Warnings          | Generated by backend            |

---

## 9. Missing Data Workflow

The system handles missing data without failing the API.

### 9.1 Missing MongoDB Profile

```text
Customer ID requested
        |
Profile not found in MongoDB
        |
Set profile fields as "Not Available"
        |
Add warning: Customer profile not found in MongoDB
        |
Continue response generation
```

### 9.2 Missing Orders

```text
Customer ID found
        |
No orders found in CSV data
        |
Set total orders as 0
        |
Set total amount as 0
        |
Add warning: No orders found in CSV file
```

### 9.3 Missing Preferences

```text
Customer ID found
        |
No preference found in JSON data
        |
Set membership as "Not Available"
        |
Set preferred channel as "Not Available"
        |
Add warning: Customer preference not found in JSON file
```

---

## 10. Search Workflow

```text
User enters search text
        |
User clicks Apply
        |
Frontend calls GET /api/customers?search=value
        |
Backend loads consolidated customer list
        |
Backend searches across supported fields
        |
Matching customers are returned
        |
Frontend displays filtered result
```

### 10.1 Search Fields

The system supports search by:

```text
Customer ID
Name
City
Membership
Preferred Channel
```

### 10.2 Example

```text
GET /api/customers?search=Rahul
```

---

## 11. Filter Workflow

```text
User enters filter values
        |
User clicks Apply
        |
Frontend calls backend with filter parameters
        |
Backend consolidates customer list
        |
Backend applies filters
        |
Filtered customers are returned
        |
Frontend displays result
```

### 11.1 Supported Filters

| Filter            | Parameter          |
| ----------------- | ------------------ |
| City              | `city`             |
| Membership        | `membership`       |
| Preferred Channel | `preferredChannel` |

### 11.2 Example

```text
GET /api/customers?city=Hyderabad&membership=Gold
```

---

## 12. Sort Workflow

```text
User selects sort field and direction
        |
User clicks Apply
        |
Frontend calls backend with sortBy and sortDir
        |
Backend validates sort field
        |
Backend sorts consolidated customer list
        |
Sorted result is returned
        |
Frontend displays sorted list
```

### 12.1 Supported Sort Fields

```text
customerId
name
city
membership
preferredChannel
totalOrders
totalOrderAmount
```

### 12.2 Supported Sort Directions

```text
asc
desc
```

### 12.3 Invalid Sort Workflow

```text
User sends invalid sortBy value
        |
Backend validates sort field
        |
Invalid field detected
        |
IllegalArgumentException is thrown
        |
GlobalExceptionHandler returns 400 Bad Request
```

Example:

```text
GET /api/customers?sortBy=wrongField
```

Response:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid sortBy value: wrongField",
  "path": "/api/customers"
}
```

---

## 13. Customer Detail Workflow

```text
User clicks View button
        |
Frontend calls GET /api/customers/{customerId}
        |
Backend consolidates selected customer data
        |
Backend returns CustomerDetailResponse
        |
Frontend displays detail panel
```

### 13.1 Customer Detail Section Displays

```text
Customer ID
Name
Email
Mobile
City
Membership
Preferred Channel
Total Amount
Orders
Warnings
AI Customer Summary
```

---

## 14. AI Summary Workflow

Customer360 uses Ollama local AI to generate a professional customer summary.

```text
User selects customer
        |
User clicks Generate AI Summary
        |
Frontend calls GET /api/customers/{customerId}/summary
        |
Backend gets consolidated customer details
        |
Backend builds AI prompt
        |
Backend sends prompt to Ollama local AI
        |
Ollama returns generated summary
        |
Backend cleans AI response
        |
Frontend displays AI summary
```

### 14.1 AI Summary API

```text
GET /api/customers/{customerId}/summary
```

### 14.2 Ollama Configuration

```properties
ai.summary.enabled=true
ai.ollama.url=http://localhost:11434/api/generate
ai.ollama.model=llama3.2
```

---

## 15. AI Fallback Workflow

If Ollama is unavailable, the application still works.

```text
User clicks Generate AI Summary
        |
Backend calls Ollama
        |
Ollama is unavailable or returns error
        |
Backend catches exception
        |
Backend generates rule-based summary
        |
API returns fallback summary
        |
Frontend displays summary
```

### 15.1 Reason for Fallback

Fallback ensures:

```text
Application does not fail during demo
Summary API remains reliable
AI is treated as enhancement, not hard dependency
```

---

## 16. Full Customer Export Workflow

The frontend supports exporting the full customer list.

```text
User clicks Export CSV / Excel / PDF
        |
Frontend calls backend export API
        |
Backend consolidates all customer data
        |
Backend generates selected file format
        |
Browser downloads file
```

### 16.1 Full Export APIs

| Export | Endpoint                      |
| ------ | ----------------------------- |
| CSV    | `/api/customers/export/csv`   |
| Excel  | `/api/customers/export/excel` |
| PDF    | `/api/customers/export/pdf`   |

### 16.2 Export Output Includes

```text
Customer ID
Name
Email
Mobile
City
Membership
Preferred Channel
Total Orders
Total Amount
Warnings
```

---

## 17. Selected Customer Detail Export Workflow

Selected customer detail export is handled in the frontend.

```text
User clicks View
        |
Customer detail is displayed
        |
User clicks Export Detail CSV or Export Detail PDF
        |
Frontend generates export using selected customer data
        |
CSV is downloaded or browser print window opens
```

### 17.1 Export Detail CSV

```text
User clicks Export Detail CSV
        |
Frontend creates CSV content
        |
Browser Blob is generated
        |
CSV file is downloaded
```

Example file name:

```text
C1001_customer_details.csv
```

### 17.2 Export Detail PDF

```text
User clicks Export Detail PDF
        |
Frontend opens print-friendly view
        |
Browser print dialog opens
        |
User selects Save as PDF
```

### 17.3 Selected Detail Export Includes

```text
Customer profile
Orders
Warnings
Customer summary
```

---

## 18. Frontend Reset Workflow

```text
User clicks Reset
        |
Search field is cleared
        |
Filter fields are cleared
        |
Sort is reset to default
        |
Selected customer is cleared
        |
Summary is cleared
        |
Frontend reloads full customer list
```

---

## 19. Error Handling Workflow

The backend uses a global exception handler.

```text
Exception occurs in controller/service
        |
GlobalExceptionHandler catches exception
        |
ApiErrorResponse is generated
        |
Clean JSON error response is returned
        |
Frontend displays error message
```

### 19.1 Error Response Format

```json
{
  "timestamp": "2026-06-14T01:33:34.2680746",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid sortBy value: wrongField",
  "path": "/api/customers"
}
```

---

## 20. Logging Workflow

The backend logs important events.

### 20.1 Startup Logs

```text
Customer orders CSV loaded successfully
Customer preferences JSON loaded successfully
```

### 20.2 Warning Logs

```text
Invalid CSV data skipped
AI summary generation failed, falling back to rule-based summary
Invalid sortBy value
```

### 20.3 Success Logs

```text
AI summary generated successfully for customerId
```

---

## 21. Security Workflow

### 21.1 MongoDB Credential Workflow

```text
Application starts
        |
MongoConfig reads MONGODB_URI from environment variable
        |
MongoDB connection is created
        |
No password is stored in source code
```

### 21.2 Data Masking Workflow

```text
Customer data retrieved from MongoDB
        |
Email is masked
        |
Mobile is masked
        |
Masked data is returned to frontend
```

Example:

```text
rahul.sharma@gmail.com → r***********@gmail.com
9876543210 → 9876******
```

---

## 22. CORS Workflow

The frontend and backend run on different ports.

```text
Frontend: http://localhost:5173
Backend: http://localhost:8080
```

Workflow:

```text
Frontend sends API request
        |
Browser checks CORS policy
        |
Backend CorsConfig allows frontend origin
        |
Request is accepted
        |
API response is returned
```

---

## 23. Swagger Workflow

```text
Backend starts
        |
Springdoc scans controllers
        |
OpenAPI specification is generated
        |
Swagger UI is available
```

Swagger URL:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON URL:

```text
http://localhost:8080/v3/api-docs
```

---

## 24. Testing Workflow

```text
Developer runs Maven test
        |
Unit tests execute
        |
MaskingServiceTest validates masking
        |
CustomerSummaryServiceTest validates AI and fallback summary
        |
CustomerConsolidationServiceTest validates data merge/search/filter/sort
        |
Build succeeds if all tests pass
```

Command:

```powershell
.\mvnw.cmd test
```

Expected result:

```text
BUILD SUCCESS
```

---

## 25. End-to-End User Workflow

```text
1. User starts backend
2. User starts frontend
3. User opens Customer360 UI
4. Customer list loads
5. User searches or filters customer list
6. User sorts customer list
7. User clicks View
8. Customer detail is displayed
9. User generates AI summary
10. User exports full customer data if needed
11. User exports selected customer detail if needed
```

---

## 26. Workflow Summary

| Workflow                        | Status      |
| ------------------------------- | ----------- |
| Backend startup workflow        | Implemented |
| CSV loading workflow            | Implemented |
| JSON loading workflow           | Implemented |
| MongoDB profile workflow        | Implemented |
| Customer consolidation workflow | Implemented |
| Search workflow                 | Implemented |
| Filter workflow                 | Implemented |
| Sort workflow                   | Implemented |
| Customer detail workflow        | Implemented |
| AI summary workflow             | Implemented |
| AI fallback workflow            | Implemented |
| Full export workflow            | Implemented |
| Selected detail export workflow | Implemented |
| Error handling workflow         | Implemented |
| Logging workflow                | Implemented |
| Swagger workflow                | Implemented |
| Testing workflow                | Implemented |

---

## 27. Conclusion

The Customer360 workflow provides a complete end-to-end process for reading, consolidating, displaying, summarizing, and exporting customer data.

The system supports reliable customer data integration using MongoDB, CSV, and JSON sources. It also includes AI-powered summary generation using Ollama local AI with rule-based fallback to ensure application stability.

This workflow is suitable for demonstration, review, and future enhancement.
