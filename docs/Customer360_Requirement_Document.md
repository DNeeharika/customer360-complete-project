# Customer360 Requirement Document

## 1. Document Information

| Item             | Details                   |
| ---------------- | ------------------------- |
| Project Name     | Customer360               |
| Module           | Customer Data Integration |
| Application Type | Web Application           |
| Backend          | Spring Boot               |
| Frontend         | React                     |
| Database         | MongoDB                   |
| AI Integration   | Ollama Local AI           |
| Document Type    | Requirement Document      |
| Version          | 1.0                       |

---

## 2. Project Objective

The objective of the Customer360 application is to consolidate customer information from multiple data sources and provide a unified customer profile through a web-based application.

The system should read customer profile data from MongoDB, customer order data from a CSV file, and customer preference data from a JSON file. All data should be matched using a common field called `customerId`.

The final output should allow users to view consolidated customer details, order history, total order amount, membership information, preferred communication channel, and AI-powered customer summary.

---

## 3. Scope of the Application

The scope of this project includes:

* Reading customer profile data from MongoDB
* Reading customer order data from CSV file
* Reading customer preference data from JSON file
* Matching all data using `customerId`
* Displaying consolidated customer profile
* Showing customer orders
* Calculating total order amount
* Showing membership information
* Showing preferred communication channel
* Providing search, filtering, and sorting
* Exporting customer data
* Generating AI-powered customer summary
* Displaying data quality warnings
* Providing frontend user interface
* Providing backend REST APIs
* Providing Swagger/OpenAPI documentation
* Providing basic unit tests

---

## 4. Data Sources

| Source          | Data Format       | Purpose                            |
| --------------- | ----------------- | ---------------------------------- |
| MongoDB         | Document database | Stores customer profile data       |
| CSV File        | Flat file         | Stores customer order details      |
| JSON File       | Structured file   | Stores customer preference details |
| Ollama Local AI | Local AI model    | Generates customer summary         |

---

## 5. Functional Requirements

### FR-01: Read Customer Profile Data from MongoDB

The system shall read customer profile information from MongoDB.

Customer profile data shall include:

| Field      | Description                |
| ---------- | -------------------------- |
| customerId | Unique customer identifier |
| name       | Customer name              |
| email      | Customer email             |
| mobile     | Customer mobile number     |
| city       | Customer city              |

---

### FR-02: Read Customer Order Data from CSV File

The system shall read customer order information from a CSV file.

Customer order data shall include:

| Field      | Description             |
| ---------- | ----------------------- |
| customerId | Customer identifier     |
| orderId    | Unique order identifier |
| orderDate  | Order date              |
| amount     | Order amount            |

The system shall skip invalid CSV rows and log warnings.

---

### FR-03: Read Customer Preference Data from JSON File

The system shall read customer preference information from a JSON file.

Customer preference data shall include:

| Field            | Description                     |
| ---------------- | ------------------------------- |
| customerId       | Customer identifier             |
| membership       | Customer membership level       |
| preferredChannel | Preferred communication channel |

---

### FR-04: Match Data Using Customer ID

The system shall match data from MongoDB, CSV, and JSON using the common field:

```text
customerId
```

The system shall generate a consolidated customer profile using this matching logic.

---

### FR-05: Display Consolidated Customer Profile

The system shall display consolidated customer information including:

* Customer ID
* Name
* Email
* Mobile
* City
* Membership
* Preferred channel
* Orders
* Total order amount
* Data quality warnings

---

### FR-06: Show Customer Orders

The system shall display all orders related to a selected customer.

Order details shall include:

* Order ID
* Order date
* Order amount

---

### FR-07: Calculate Total Order Amount

The system shall calculate the total order amount for each customer by summing all valid order amounts from the CSV file.

---

### FR-08: Show Membership Information

The system shall display membership information from the JSON preference file.

Example membership values:

* Gold
* Silver
* Platinum
* Bronze

---

### FR-09: Show Preferred Communication Channel

The system shall display the preferred communication channel from the JSON preference file.

Example preferred channels:

* Email
* SMS
* WhatsApp
* Phone

---

### FR-10: Search Customers

The system shall allow users to search customers using:

* Customer ID
* Customer name
* City
* Membership
* Preferred communication channel

---

### FR-11: Filter Customers

The system shall allow filtering by:

* City
* Membership
* Preferred communication channel

---

### FR-12: Sort Customers

The system shall allow sorting by:

* Customer ID
* Name
* City
* Membership
* Preferred channel
* Total orders
* Total order amount

Sorting direction shall support:

* Ascending
* Descending

---

### FR-13: Export Full Customer Data

The system shall support exporting full customer data in the following formats:

| Export Type  | Format  |
| ------------ | ------- |
| CSV Export   | `.csv`  |
| Excel Export | `.xlsx` |
| PDF Export   | `.pdf`  |

---

### FR-14: Export Selected Customer Details

The system shall allow exporting selected customer details from the frontend.

Selected customer export shall include:

* Customer profile
* Orders
* Warnings
* Customer summary

Supported selected customer export formats:

| Export Type       | Format                      |
| ----------------- | --------------------------- |
| Export Detail CSV | `.csv`                      |
| Export Detail PDF | Browser print / Save as PDF |

---

### FR-15: Generate AI-Powered Customer Summary

The system shall generate a customer summary using Ollama local AI.

The AI-generated summary shall include:

* Customer profile context
* Order activity
* Membership
* Preferred communication channel
* Data quality warnings if available

The system shall use Indian Rupees symbol `₹` for monetary values.

---

### FR-16: Rule-Based Summary Fallback

If Ollama is unavailable or AI generation fails, the system shall return a rule-based customer summary.

The summary API shall not fail only because AI is unavailable.

---

### FR-17: Data Quality Warning Handling

The system shall show warnings for incomplete or missing data.

Examples:

| Scenario                            | Warning                                    |
| ----------------------------------- | ------------------------------------------ |
| Customer profile missing in MongoDB | Customer profile not found in MongoDB      |
| Orders missing in CSV               | No orders found in CSV file                |
| Preference missing in JSON          | Customer preference not found in JSON file |
| Invalid CSV amount                  | Invalid row skipped and logged             |

---

### FR-18: Mask Sensitive Data

The system shall mask sensitive customer information before sending it in API response.

| Field  | Masking Rule                                       |
| ------ | -------------------------------------------------- |
| Email  | Show first character and mask remaining local part |
| Mobile | Show first 4 digits and mask remaining digits      |

Example:

```text
rahul.sharma@gmail.com → r***********@gmail.com
9876543210 → 9876******
```

---

### FR-19: Swagger API Documentation

The system shall provide Swagger/OpenAPI documentation.

Swagger URL:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON URL:

```text
http://localhost:8080/v3/api-docs
```

---

### FR-20: Frontend User Interface

The system shall provide a React-based frontend with:

* Customer list
* Search bar
* Filters
* Sorting options
* Customer detail section
* Orders section
* Warnings section
* AI summary section
* Export buttons

---

## 6. Non-Functional Requirements

### NFR-01: Performance

The system shall load and display customer data efficiently for the sample dataset.

### NFR-02: Maintainability

The code shall follow a clean package structure with separation of responsibilities.

Main layers:

* Controller
* Service
* Repository
* DTO
* Model
* Loader
* Export
* Exception handling
* Configuration

### NFR-03: Security

The system shall not store sensitive credentials in source code.

MongoDB URI shall be read from environment variable:

```text
MONGODB_URI
```

Sensitive customer email and mobile number shall be masked in API responses.

### NFR-04: Reliability

The application shall continue to work even if Ollama local AI is unavailable by using rule-based summary fallback.

### NFR-05: Usability

The frontend shall provide a clean and readable interface with compact enterprise-style layout.

### NFR-06: Logging

The backend shall use structured logging instead of `System.out.println`.

### NFR-07: Error Handling

The backend shall return clean JSON error responses using global exception handling.

### NFR-08: Testability

The backend shall include unit tests for core business logic.

---

## 7. API Requirements

### Customer APIs

| Method | Endpoint                              | Description                              |
| ------ | ------------------------------------- | ---------------------------------------- |
| GET    | `/api/customers`                      | Get all consolidated customers           |
| GET    | `/api/customers/{customerId}`         | Get customer details                     |
| GET    | `/api/customers/{customerId}/summary` | Generate AI or fallback customer summary |

### Export APIs

| Method | Endpoint                      | Description                        |
| ------ | ----------------------------- | ---------------------------------- |
| GET    | `/api/customers/export/csv`   | Export full customer data as CSV   |
| GET    | `/api/customers/export/excel` | Export full customer data as Excel |
| GET    | `/api/customers/export/pdf`   | Export full customer data as PDF   |

---

## 8. User Interface Requirements

The frontend shall contain the following sections:

### 8.1 Header Section

The header shall display:

* Application name: Customer360
* Short description
* Full export buttons:

    * Export CSV
    * Export Excel
    * Export PDF

### 8.2 Filter Section

The filter section shall include:

* Search input
* City filter
* Membership filter
* Preferred channel filter
* Sort field dropdown
* Sort direction dropdown
* Apply button
* Reset button

### 8.3 Customer Table

The customer table shall display:

* Customer ID
* Name
* City
* Membership
* Channel
* Orders
* Total amount
* View action

### 8.4 Customer Detail Section

The customer detail section shall display:

* Customer profile
* Orders
* Warnings
* AI summary
* Export Detail CSV
* Export Detail PDF

---

## 9. AI Summary Requirement

The application shall integrate with Ollama local AI.

Ollama model:

```text
llama3.2
```

Ollama API URL:

```text
http://localhost:11434/api/generate
```

The prompt shall instruct the AI to:

* Generate a concise professional customer summary
* Use only provided customer data
* Use Indian Rupees symbol `₹`
* Avoid exposing sensitive data
* Avoid unsupported claims
* Mention data quality warnings if available

---

## 10. Assumptions

* MongoDB Atlas is available and accessible.
* CSV and JSON files are available in the backend resources folder.
* `customerId` is the common field across all data sources.
* Ollama is installed locally for AI summary generation.
* If Ollama is unavailable, rule-based fallback summary is acceptable.
* React frontend runs on `http://localhost:5173`.
* Backend runs on `http://localhost:8080`.

---

## 11. Out of Scope

The following items are not included in the current version:

* User login
* Role-based access control
* Production deployment
* Docker deployment
* Pagination
* Audit trail
* Advanced analytics dashboards
* Cloud-hosted AI model
* Real-time data synchronization

---

## 12. Acceptance Criteria

| Requirement            | Acceptance Criteria                            |
| ---------------------- | ---------------------------------------------- |
| MongoDB data read      | Customer profile data is returned from MongoDB |
| CSV data read          | Customer orders are loaded successfully        |
| JSON data read         | Customer preferences are loaded successfully   |
| Data consolidation     | Customer data is matched by `customerId`       |
| Total amount           | Total order amount is calculated correctly     |
| Search                 | Search returns matching customers              |
| Filter                 | Filters return correct customer list           |
| Sort                   | Sorting works for supported fields             |
| Export                 | CSV, Excel, and PDF exports work               |
| Selected detail export | Selected customer CSV and PDF export work      |
| AI summary             | Ollama summary is generated when available     |
| AI fallback            | Rule-based summary returns when Ollama fails   |
| Masking                | Email and mobile are masked                    |
| Swagger                | API documentation is available                 |
| Unit tests             | All backend tests pass                         |
| Frontend               | User can view customers and details from UI    |

---

## 13. Test Summary

Backend unit tests cover:

* Masking service
* Customer summary service
* AI fallback behavior
* Customer consolidation service
* Search/filter/sort logic
* Data warnings
* Application class test

Expected Maven test result:

```text
BUILD SUCCESS
```

---

## 14. Current Completion Status

| Area                            | Status    |
| ------------------------------- | --------- |
| Backend APIs                    | Completed |
| Frontend UI                     | Completed |
| MongoDB integration             | Completed |
| CSV loading                     | Completed |
| JSON loading                    | Completed |
| Data consolidation              | Completed |
| Search/filter/sort              | Completed |
| Full customer export            | Completed |
| Selected customer detail export | Completed |
| Ollama AI integration           | Completed |
| Rule-based fallback             | Completed |
| Swagger documentation           | Completed |
| Unit tests                      | Completed |
| README files                    | Completed |

---

## 15. Conclusion

The Customer360 application successfully consolidates customer data from MongoDB, CSV, and JSON sources using `customerId`. It provides a clean frontend interface, backend REST APIs, Swagger documentation, export features, AI-powered customer summary using Ollama, and fallback summary logic for reliability.

This version is suitable for project demonstration, review, and further enhancement.
