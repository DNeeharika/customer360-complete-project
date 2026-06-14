# Customer360 Testing Document

## 1. Document Information

| Item           | Details                   |
| -------------- | ------------------------- |
| Project Name   | Customer360               |
| Module         | Customer Data Integration |
| Document Type  | Testing Document          |
| Version        | 1.0                       |
| Backend        | Spring Boot               |
| Frontend       | React                     |
| Database       | MongoDB                   |
| AI Integration | Ollama Local AI           |

---

## 2. Testing Objective

The objective of this testing document is to define and validate the testing approach for the Customer360 application.

The testing confirms that the application correctly:

* Reads customer profile data from MongoDB
* Reads customer order data from CSV
* Reads customer preference data from JSON
* Matches data using `customerId`
* Displays consolidated customer details
* Calculates total order amount
* Supports search, filter, and sort
* Exports customer data
* Generates AI-powered customer summary using Ollama
* Falls back to rule-based summary when Ollama is unavailable
* Handles missing and invalid data
* Provides clean error responses
* Works correctly from the React frontend

---

## 3. Testing Scope

The testing scope includes:

| Area                         | Included |
| ---------------------------- | -------- |
| Backend API Testing          | Yes      |
| Frontend UI Testing          | Yes      |
| MongoDB Data Validation      | Yes      |
| CSV Data Loading Validation  | Yes      |
| JSON Data Loading Validation | Yes      |
| Data Consolidation Testing   | Yes      |
| Search/Filter/Sort Testing   | Yes      |
| Export Testing               | Yes      |
| AI Summary Testing           | Yes      |
| Rule-Based Fallback Testing  | Yes      |
| Data Masking Testing         | Yes      |
| Error Handling Testing       | Yes      |
| Unit Testing                 | Yes      |
| Swagger Testing              | Yes      |

---

## 4. Out of Scope

The following are not covered in the current version:

* Performance/load testing
* Security penetration testing
* Authentication testing
* Role-based access control testing
* Browser compatibility testing across all browsers
* Production deployment testing
* Automated frontend unit testing

---

## 5. Test Environment

| Component          | Details                                 |
| ------------------ | --------------------------------------- |
| Operating System   | Windows                                 |
| Backend Runtime    | Java 25                                 |
| Backend Framework  | Spring Boot 4.1.0                       |
| Frontend Framework | React + Vite                            |
| Database           | MongoDB Atlas                           |
| AI Runtime         | Ollama local AI                         |
| AI Model           | llama3.2                                |
| Backend URL        | `http://localhost:8080`                 |
| Frontend URL       | `http://localhost:5173`                 |
| Swagger URL        | `http://localhost:8080/swagger-ui.html` |

---

## 6. Test Data

### 6.1 MongoDB Customer Profile Data

| customerId | Name         | City      |
| ---------- | ------------ | --------- |
| C1001      | Rahul Sharma | Hyderabad |
| C1002      | Priya Mehta  | Mumbai    |
| C1003      | Amit Verma   | Bengaluru |
| C1004      | Sneha Reddy  | Hyderabad |
| C1005      | Arjun Nair   | Chennai   |

---

### 6.2 CSV Order Data

| customerId | orderId | orderDate  | amount         |
| ---------- | ------- | ---------- | -------------- |
| C1001      | O1001   | 2026-01-10 | 2500           |
| C1001      | O1002   | 2026-02-15 | 1200           |
| C1002      | O1003   | 2026-01-20 | 5600           |
| C1003      | O1004   | 2026-03-05 | 800            |
| C1003      | O1005   | 2026-03-22 | 1500           |
| C1006      | O1006   | 2026-04-01 | 3000           |
| C1004      | O1007   | 2026-04-10 | 0              |
| C1005      | O1008   | 2026-04-12 | 4500           |
| C1005      | O1009   | 2026-04-20 | invalid_amount |

---

### 6.3 JSON Preference Data

| customerId | Membership | Preferred Channel |
| ---------- | ---------- | ----------------- |
| C1001      | Gold       | Email             |
| C1002      | Silver     | SMS               |
| C1003      | Platinum   | WhatsApp          |
| C1006      | Gold       | Email             |
| C1007      | Bronze     | Phone             |

---

## 7. Test Scenarios Summary

| Test Area       | Scenario                                 | Status |
| --------------- | ---------------------------------------- | ------ |
| Backend Startup | Application starts successfully          | Passed |
| MongoDB         | Customer profiles are loaded             | Passed |
| CSV             | Orders are loaded                        | Passed |
| CSV Validation  | Invalid amount row is skipped            | Passed |
| JSON            | Preferences are loaded                   | Passed |
| Consolidation   | Data is matched by customerId            | Passed |
| Missing Data    | Warnings are generated                   | Passed |
| Search          | Search returns matching results          | Passed |
| Filter          | Filter returns correct customers         | Passed |
| Sort            | Sort returns correct order               | Passed |
| Export          | CSV/Excel/PDF export works               | Passed |
| AI Summary      | Ollama summary generated                 | Passed |
| AI Fallback     | Rule-based summary works if Ollama fails | Passed |
| Masking         | Email/mobile are masked                  | Passed |
| Error Handling  | Invalid sort returns clean error         | Passed |
| Frontend        | Customer list displays                   | Passed |
| Frontend        | Customer detail displays                 | Passed |
| Frontend        | Selected detail export works             | Passed |

---

## 8. Backend API Test Cases

### TC-BE-01: Get All Customers

| Item            | Details                                                                                  |
| --------------- | ---------------------------------------------------------------------------------------- |
| Test Case ID    | TC-BE-01                                                                                 |
| API             | `GET /api/customers`                                                                     |
| Objective       | Verify all consolidated customer records are returned                                    |
| Input           | None                                                                                     |
| Expected Result | Customer list should be returned with profile, orders, preferences, totals, and warnings |
| Status          | Passed                                                                                   |

Expected URL:

```text
http://localhost:8080/api/customers
```

---

### TC-BE-02: Get Customer Details

| Item            | Details                                                                              |
| --------------- | ------------------------------------------------------------------------------------ |
| Test Case ID    | TC-BE-02                                                                             |
| API             | `GET /api/customers/C1001`                                                           |
| Objective       | Verify customer detail response for a valid customer                                 |
| Input           | `C1001`                                                                              |
| Expected Result | Rahul Sharma details, two orders, total amount ₹3700, Gold membership, Email channel |
| Status          | Passed                                                                               |

Expected URL:

```text
http://localhost:8080/api/customers/C1001
```

---

### TC-BE-03: Customer With Missing Preference

| Item            | Details                                                                             |
| --------------- | ----------------------------------------------------------------------------------- |
| Test Case ID    | TC-BE-03                                                                            |
| API             | `GET /api/customers/C1004`                                                          |
| Objective       | Verify warning when JSON preference is missing                                      |
| Input           | `C1004`                                                                             |
| Expected Result | Membership and preferred channel should be `Not Available`; warning should be shown |
| Status          | Passed                                                                              |

Expected warning:

```text
Customer preference not found in JSON file.
```

---

### TC-BE-04: Customer With Missing MongoDB Profile

| Item            | Details                                                                             |
| --------------- | ----------------------------------------------------------------------------------- |
| Test Case ID    | TC-BE-04                                                                            |
| API             | `GET /api/customers/C1006`                                                          |
| Objective       | Verify warning when MongoDB profile is missing                                      |
| Input           | `C1006`                                                                             |
| Expected Result | Profile fields should be `Not Available`; order and preference should still display |
| Status          | Passed                                                                              |

Expected warning:

```text
Customer profile not found in MongoDB.
```

---

### TC-BE-05: Customer With Missing Orders

| Item            | Details                                                                     |
| --------------- | --------------------------------------------------------------------------- |
| Test Case ID    | TC-BE-05                                                                    |
| API             | `GET /api/customers/C1007`                                                  |
| Objective       | Verify warning when CSV orders are missing                                  |
| Input           | `C1007`                                                                     |
| Expected Result | Total orders should be 0; total amount should be 0; warning should be shown |
| Status          | Passed                                                                      |

Expected warning:

```text
No orders found in CSV file.
```

---

## 9. Search Test Cases

### TC-SR-01: Search by Customer Name

| Item            | Details                              |
| --------------- | ------------------------------------ |
| Test Case ID    | TC-SR-01                             |
| API             | `GET /api/customers?search=rahul`    |
| Objective       | Verify search by name                |
| Expected Result | Only Rahul Sharma should be returned |
| Status          | Passed                               |

---

### TC-SR-02: Search by City

| Item            | Details                                |
| --------------- | -------------------------------------- |
| Test Case ID    | TC-SR-02                               |
| API             | `GET /api/customers?search=Hyderabad`  |
| Objective       | Verify search by city                  |
| Expected Result | Hyderabad customers should be returned |
| Status          | Passed                                 |

---

### TC-SR-03: Search by Membership

| Item            | Details                           |
| --------------- | --------------------------------- |
| Test Case ID    | TC-SR-03                          |
| API             | `GET /api/customers?search=Gold`  |
| Objective       | Verify search by membership       |
| Expected Result | Gold customers should be returned |
| Status          | Passed                            |

---

## 10. Filter Test Cases

### TC-FL-01: Filter by City

| Item            | Details                                     |
| --------------- | ------------------------------------------- |
| Test Case ID    | TC-FL-01                                    |
| API             | `GET /api/customers?city=Hyderabad`         |
| Objective       | Verify city filter                          |
| Expected Result | Only Hyderabad customers should be returned |
| Status          | Passed                                      |

---

### TC-FL-02: Filter by Membership

| Item            | Details                              |
| --------------- | ------------------------------------ |
| Test Case ID    | TC-FL-02                             |
| API             | `GET /api/customers?membership=Gold` |
| Objective       | Verify membership filter             |
| Expected Result | Gold customers should be returned    |
| Status          | Passed                               |

---

### TC-FL-03: Filter by City and Membership

| Item            | Details                                             |
| --------------- | --------------------------------------------------- |
| Test Case ID    | TC-FL-03                                            |
| API             | `GET /api/customers?city=Hyderabad&membership=Gold` |
| Objective       | Verify multiple filters                             |
| Expected Result | Rahul Sharma should be returned                     |
| Status          | Passed                                              |

---

## 11. Sort Test Cases

### TC-ST-01: Sort by Total Amount Descending

| Item            | Details                                                   |
| --------------- | --------------------------------------------------------- |
| Test Case ID    | TC-ST-01                                                  |
| API             | `GET /api/customers?sortBy=totalOrderAmount&sortDir=desc` |
| Objective       | Verify sorting by total amount descending                 |
| Expected Result | Highest total amount customer should appear first         |
| Status          | Passed                                                    |

Expected order:

```text
Priya Mehta - 5600
Arjun Nair - 4500
Rahul Sharma - 3700
Amit Verma - 2300
Sneha Reddy - 0
```

---

### TC-ST-02: Invalid Sort Field

| Item            | Details                                            |
| --------------- | -------------------------------------------------- |
| Test Case ID    | TC-ST-02                                           |
| API             | `GET /api/customers?sortBy=wrongField`             |
| Objective       | Verify clean error response for invalid sort field |
| Expected Result | HTTP 400 Bad Request                               |
| Status          | Passed                                             |

Expected response:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid sortBy value: wrongField",
  "path": "/api/customers"
}
```

---

## 12. Export Test Cases

### TC-EX-01: Export Full Customer List as CSV

| Item            | Details                         |
| --------------- | ------------------------------- |
| Test Case ID    | TC-EX-01                        |
| API             | `GET /api/customers/export/csv` |
| Objective       | Verify full customer CSV export |
| Expected Result | `customers.csv` should download |
| Status          | Passed                          |

---

### TC-EX-02: Export Full Customer List as Excel

| Item            | Details                           |
| --------------- | --------------------------------- |
| Test Case ID    | TC-EX-02                          |
| API             | `GET /api/customers/export/excel` |
| Objective       | Verify full customer Excel export |
| Expected Result | `customers.xlsx` should download  |
| Status          | Passed                            |

---

### TC-EX-03: Export Full Customer List as PDF

| Item            | Details                         |
| --------------- | ------------------------------- |
| Test Case ID    | TC-EX-03                        |
| API             | `GET /api/customers/export/pdf` |
| Objective       | Verify full customer PDF export |
| Expected Result | `customers.pdf` should download |
| Status          | Passed                          |

---

### TC-EX-04: Export Selected Customer Detail as CSV

| Item            | Details                                    |
| --------------- | ------------------------------------------ |
| Test Case ID    | TC-EX-04                                   |
| UI Action       | Click `Export Detail CSV`                  |
| Objective       | Verify selected customer detail CSV export |
| Expected Result | Selected customer CSV file should download |
| Status          | Passed                                     |

Example file name:

```text
C1001_customer_details.csv
```

---

### TC-EX-05: Export Selected Customer Detail as PDF

| Item            | Details                                                |
| --------------- | ------------------------------------------------------ |
| Test Case ID    | TC-EX-05                                               |
| UI Action       | Click `Export Detail PDF`                              |
| Objective       | Verify selected customer detail PDF export             |
| Expected Result | Browser print dialog should open and allow Save as PDF |
| Status          | Passed                                                 |

---

## 13. AI Summary Test Cases

### TC-AI-01: Generate AI Summary With Ollama Running

| Item            | Details                                               |
| --------------- | ----------------------------------------------------- |
| Test Case ID    | TC-AI-01                                              |
| API             | `GET /api/customers/C1001/summary`                    |
| Objective       | Verify AI summary is generated using Ollama           |
| Precondition    | Ollama running locally and `llama3.2` model available |
| Expected Result | Professional AI-generated summary should be returned  |
| Status          | Passed                                                |

---

### TC-AI-02: Rule-Based Fallback When Ollama Is Unavailable

| Item            | Details                                                      |
| --------------- | ------------------------------------------------------------ |
| Test Case ID    | TC-AI-02                                                     |
| API             | `GET /api/customers/C1001/summary`                           |
| Objective       | Verify fallback summary when Ollama fails                    |
| Precondition    | Ollama stopped or unavailable                                |
| Expected Result | Rule-based summary should be returned instead of API failure |
| Status          | Passed                                                       |

---

### TC-AI-03: AI Summary from Frontend

| Item            | Details                                           |
| --------------- | ------------------------------------------------- |
| Test Case ID    | TC-AI-03                                          |
| UI Action       | Click `Generate AI Summary`                       |
| Objective       | Verify frontend displays AI/fallback summary      |
| Expected Result | Summary should appear in customer details section |
| Status          | Passed                                            |

---

## 14. Data Masking Test Cases

### TC-MK-01: Email Masking

| Item            | Details                  |
| --------------- | ------------------------ |
| Test Case ID    | TC-MK-01                 |
| Input           | `rahul.sharma@gmail.com` |
| Expected Result | `r***********@gmail.com` |
| Status          | Passed                   |

---

### TC-MK-02: Mobile Masking

| Item            | Details      |
| --------------- | ------------ |
| Test Case ID    | TC-MK-02     |
| Input           | `9876543210` |
| Expected Result | `9876******` |
| Status          | Passed       |

---

### TC-MK-03: Null or Invalid Email

| Item            | Details                    |
| --------------- | -------------------------- |
| Test Case ID    | TC-MK-03                   |
| Input           | Null, blank, invalid email |
| Expected Result | `Not Available`            |
| Status          | Passed                     |

---

## 15. Frontend UI Test Cases

### TC-UI-01: Customer List Page Load

| Item            | Details                             |
| --------------- | ----------------------------------- |
| Test Case ID    | TC-UI-01                            |
| URL             | `http://localhost:5173`             |
| Objective       | Verify frontend loads customer list |
| Expected Result | Customer table should display       |
| Status          | Passed                              |

---

### TC-UI-02: View Customer Details

| Item            | Details                                                       |
| --------------- | ------------------------------------------------------------- |
| Test Case ID    | TC-UI-02                                                      |
| Action          | Click `View`                                                  |
| Objective       | Verify selected customer detail displays                      |
| Expected Result | Profile, orders, warnings, and summary section should display |
| Status          | Passed                                                        |

---

### TC-UI-03: Apply Search and Filters

| Item            | Details                                         |
| --------------- | ----------------------------------------------- |
| Test Case ID    | TC-UI-03                                        |
| Action          | Enter search/filter and click Apply             |
| Objective       | Verify frontend sends filter request to backend |
| Expected Result | Filtered customer result should display         |
| Status          | Passed                                          |

---

### TC-UI-04: Reset Filters

| Item            | Details                                             |
| --------------- | --------------------------------------------------- |
| Test Case ID    | TC-UI-04                                            |
| Action          | Click Reset                                         |
| Objective       | Verify filters clear and full customer list reloads |
| Expected Result | Full customer list should display                   |
| Status          | Passed                                              |

---

## 16. Swagger Test Cases

### TC-SW-01: Swagger UI Opens

| Item            | Details                                              |
| --------------- | ---------------------------------------------------- |
| Test Case ID    | TC-SW-01                                             |
| URL             | `http://localhost:8080/swagger-ui.html`              |
| Objective       | Verify Swagger UI is available                       |
| Expected Result | Customer360 Backend API documentation should display |
| Status          | Passed                                               |

---

### TC-SW-02: OpenAPI JSON Opens

| Item            | Details                             |
| --------------- | ----------------------------------- |
| Test Case ID    | TC-SW-02                            |
| URL             | `http://localhost:8080/v3/api-docs` |
| Objective       | Verify OpenAPI JSON is available    |
| Expected Result | OpenAPI JSON should display         |
| Status          | Passed                              |

---

## 17. Unit Test Summary

Backend unit tests were executed using Maven.

Command:

```powershell
.\mvnw.cmd test
```

Expected result:

```text
BUILD SUCCESS
```

### 17.1 Unit Test Classes

| Test Class                           | Purpose                                                  | Status |
| ------------------------------------ | -------------------------------------------------------- | ------ |
| `MaskingServiceTest`                 | Tests masking logic                                      | Passed |
| `CustomerSummaryServiceTest`         | Tests AI summary and fallback logic                      | Passed |
| `CustomerConsolidationServiceTest`   | Tests data consolidation, warnings, search, filter, sort | Passed |
| `Customer360BackendApplicationTests` | Basic application class test                             | Passed |

---

## 18. Defect Summary

| Defect                                                       | Resolution                                                       |
| ------------------------------------------------------------ | ---------------------------------------------------------------- |
| Swagger initially showed test/debug APIs                     | Removed development test controllers                             |
| Maven test failed due to application context loading MongoDB | Replaced default context test with simple application class test |
| Frontend could not call backend due to CORS                  | Added backend CORS configuration                                 |
| Frontend table was difficult to read                         | Updated compact responsive CSS layout                            |
| AI summary used dollar symbol                                | Updated prompt to use Indian Rupees                              |
| AI summary added extra introduction text                     | Added stricter prompt and response cleanup                       |
| Selected customer detail had no export                       | Added Export Detail CSV and Export Detail PDF                    |

---

## 19. Test Completion Status

| Testing Area             | Completion |
| ------------------------ | ---------- |
| Backend API Testing      | Completed  |
| Frontend UI Testing      | Completed  |
| Data Integration Testing | Completed  |
| Export Testing           | Completed  |
| AI Summary Testing       | Completed  |
| Fallback Testing         | Completed  |
| Error Handling Testing   | Completed  |
| Unit Testing             | Completed  |
| Swagger Testing          | Completed  |

---

## 20. Final Test Result

The Customer360 application passed the required functional and unit testing.

Final status:

```text
Testing Completed Successfully
```

The application is ready for demonstration and review.
