# Customer360 Frontend

Customer360 Frontend is a React-based user interface for viewing consolidated customer profile, order, preference, export, and AI-powered summary data from the Customer360 Backend API.

## Project Objective

The objective of this frontend application is to provide a clean and user-friendly interface to:

* View consolidated customer data
* Search customers
* Filter customers
* Sort customer data
* View customer details
* View customer orders
* Generate AI-powered customer summary
* Export full customer data as CSV, Excel, and PDF
* Export selected customer details as CSV and PDF

## Technology Stack

| Area               | Technology                      |
| ------------------ | ------------------------------- |
| Frontend Framework | React                           |
| Build Tool         | Vite                            |
| Language           | JavaScript                      |
| API Client         | Axios                           |
| Styling            | CSS                             |
| Backend API        | Spring Boot Customer360 Backend |
| AI Summary         | Ollama local AI through backend |

## Project Structure

```text
customer360-frontend
  src
    api
      customerApi.js
    assets
    pages
      CustomerListPage.jsx
    App.jsx
    App.css
    index.css
    main.jsx
  package.json
  vite.config.js
  README.md
```

## Backend Dependency

This frontend depends on the Customer360 Backend API.

Backend should be running at:

```text
http://localhost:8080
```

Frontend runs at:

```text
http://localhost:5173
```

The backend must allow CORS for the frontend URL:

```text
http://localhost:5173
```

## API Base URL

The frontend connects to the backend using:

```javascript
const API_BASE_URL = "http://localhost:8080/api/customers";
```

This is configured in:

```text
src/api/customerApi.js
```

## Features Implemented

* Customer list display
* Customer search
* Filter by city
* Filter by membership
* Filter by preferred communication channel
* Sort by:

    * Customer ID
    * Name
    * City
    * Membership
    * Preferred channel
    * Total orders
    * Total amount
* Customer detail view
* Customer order list
* Data quality warnings display
* Generate AI-powered customer summary using backend Ollama integration
* Rule-based fallback summary from backend when Ollama is unavailable
* Export full customer list as CSV
* Export full customer list as Excel
* Export full customer list as PDF
* Export selected customer details as CSV
* Export selected customer details as PDF using browser print/save option
* Responsive and compact enterprise-style layout

## Screens

### Customer List Screen

Displays consolidated customer data in a table.

Columns:

```text
Customer ID
Name
City
Membership
Preferred Channel
Orders
Total Amount
Action
```

### Customer Detail Section

Displays selected customer details:

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

### Export Actions

Full customer list export options:

```text
Export CSV
Export Excel
Export PDF
```

Selected customer detail export options:

```text
Export Detail CSV
Export Detail PDF
```

## AI Customer Summary

The frontend provides a **Generate AI Summary** action in the customer detail section.

When a user selects a customer and clicks **Generate AI Summary**, the frontend calls the backend summary API:

```text
GET /api/customers/{customerId}/summary
```

The backend generates the summary using Ollama local AI when available. If Ollama is unavailable, the backend automatically returns a rule-based fallback summary.

The generated summary is displayed in the customer detail section under:

```text
AI Customer Summary
```

## Selected Customer Detail Export

In addition to exporting the full customer list, the frontend supports exporting selected customer details.

Available selected-customer export actions:

```text
Export Detail CSV
Export Detail PDF
```

### Export Detail CSV

Exports the selected customer's details, orders, warnings, and generated summary into a CSV file.

Example file name:

```text
C1001_customer_details.csv
```

CSV export includes:

```text
Customer profile
Orders
Warnings
Customer summary
```

### Export Detail PDF

Opens a print-friendly customer detail view in the browser. The user can select **Save as PDF** from the browser print dialog.

The PDF export includes:

```text
Customer profile
Orders
Warnings
Customer summary
```

## How to Install

Open PowerShell in the frontend project folder:

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-frontend
```

Install dependencies:

```powershell
npm install
```

## How to Run

Start the frontend application:

```powershell
npm run dev
```

Open the application in browser:

```text
http://localhost:5173/
```

## Required Backend Setup

Before running the frontend, start the backend application.

Backend URL:

```text
http://localhost:8080
```

Test backend API:

```text
http://localhost:8080/api/customers
```

Swagger URL:

```text
http://localhost:8080/swagger-ui.html
```

## Required Ollama Setup for AI Summary

For AI-generated summaries, Ollama should be running locally.

Check Ollama:

```powershell
ollama --version
```

Pull model:

```powershell
ollama pull llama3.2
```

Check available models:

```powershell
ollama list
```

Ollama runs on:

```text
http://localhost:11434
```

If Ollama is not running, the backend returns a fallback rule-based summary.

## Frontend API Methods

The API calls are defined in:

```text
src/api/customerApi.js
```

Available methods:

| Method                           | Purpose                                                   |
| -------------------------------- | --------------------------------------------------------- |
| `getCustomers(params)`           | Get customer list with search, filter, and sort           |
| `getCustomerDetails(customerId)` | Get customer detail by customer ID                        |
| `getCustomerSummary(customerId)` | Get AI-generated or fallback customer summary             |
| `downloadExport(type)`           | Download CSV, Excel, or PDF export for full customer list |

Selected customer detail export is handled in the frontend using browser APIs.

## Test Checklist

Use the following checklist to validate the frontend:

| Feature             | Test Data                 | Expected Result                              |
| ------------------- | ------------------------- | -------------------------------------------- |
| Customer list       | Page load                 | All customers should display                 |
| Search              | `Rahul`                   | Rahul Sharma should display                  |
| Search              | `Hyderabad`               | Hyderabad customers should display           |
| Filter city         | `Hyderabad`               | Only Hyderabad customers should display      |
| Filter membership   | `Gold`                    | Gold customers should display                |
| Sort amount         | Total Amount + Descending | Highest amount should appear first           |
| View details        | Click View                | Customer detail should display               |
| Generate AI Summary | Click Generate AI Summary | AI or fallback summary should display        |
| Export CSV          | Click Export CSV          | Full customer CSV file should download       |
| Export Excel        | Click Export Excel        | Full customer Excel file should download     |
| Export PDF          | Click Export PDF          | Full customer PDF file should download       |
| Export Detail CSV   | Click Export Detail CSV   | Selected customer detail CSV should download |
| Export Detail PDF   | Click Export Detail PDF   | Print dialog should open for Save as PDF     |
| Reset               | Click Reset               | Filters should clear                         |

## Current Status

| Area                             | Status    |
| -------------------------------- | --------- |
| React project setup              | Completed |
| Axios integration                | Completed |
| Customer list UI                 | Completed |
| Search/filter/sort UI            | Completed |
| Customer detail UI               | Completed |
| AI summary integration           | Completed |
| Full customer export integration | Completed |
| Selected customer CSV export     | Completed |
| Selected customer PDF export     | Completed |
| Backend CORS integration         | Completed |
| Responsive layout                | Completed |
| GitHub Push                      | Completed |

## Future Enhancements

* Add pagination
* Add loading skeletons
* Add advanced dropdown filters
* Add date range filter for orders
* Add charts for order value and customer distribution
* Add authentication
* Add role-based access control
* Add environment-based API URL configuration
* Add frontend unit tests
* Add deployment configuration
* Add direct backend-generated selected customer PDF export

## Author

Neeharika D
