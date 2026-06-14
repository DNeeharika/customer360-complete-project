# Customer360 User Manual

## 1. Document Information

| Item             | Details                   |
| ---------------- | ------------------------- |
| Project Name     | Customer360               |
| Module           | Customer Data Integration |
| Document Type    | User Manual               |
| Version          | 1.0                       |
| Application Type | Web Application           |
| Backend          | Spring Boot               |
| Frontend         | React                     |
| Database         | MongoDB                   |
| AI Integration   | Ollama Local AI           |

---

## 2. Purpose of the User Manual

This user manual explains how to use the Customer360 application.

Customer360 is used to view consolidated customer information from multiple data sources:

* MongoDB customer profile data
* CSV customer order data
* JSON customer preference data
* AI-powered customer summary using Ollama local AI

The application allows users to search, filter, sort, view customer details, generate customer summaries, and export customer data.

---

## 3. Application Access

### 3.1 Frontend URL

Open the Customer360 application in the browser:

```text
http://localhost:5173
```

### 3.2 Backend URL

Backend API runs on:

```text
http://localhost:8080
```

### 3.3 Swagger URL

API documentation is available at:

```text
http://localhost:8080/swagger-ui.html
```

---

## 4. Application Home Screen

When the user opens Customer360, the home screen displays:

* Application title
* Search and filter section
* Export buttons
* Customer list table
* Customer detail section after selecting a customer

Main screen sections:

| Section         | Purpose                                   |
| --------------- | ----------------------------------------- |
| Header          | Shows application name and export actions |
| Filters         | Search, filter, and sort customer records |
| Customer Table  | Displays consolidated customer list       |
| Detail Panel    | Displays selected customer details        |
| Summary Section | Displays AI-generated or fallback summary |

---

## 5. Customer List

The customer list displays consolidated customer information.

### 5.1 Customer Table Columns

| Column            | Description                     |
| ----------------- | ------------------------------- |
| Customer ID       | Unique customer identifier      |
| Name              | Customer name                   |
| City              | Customer city                   |
| Membership        | Customer membership level       |
| Preferred Channel | Preferred communication channel |
| Orders            | Total number of orders          |
| Total Amount      | Total order amount              |
| Action            | View customer details           |

---

## 6. Search Customers

The search option allows users to search customer records.

### 6.1 How to Search

1. Go to the search input field.
2. Enter search text.
3. Click **Apply**.
4. Matching customers will be displayed.

### 6.2 Search Supports

Users can search by:

* Customer ID
* Customer name
* City
* Membership
* Preferred communication channel

### 6.3 Example Searches

| Search Text | Expected Result                                    |
| ----------- | -------------------------------------------------- |
| `Rahul`     | Displays Rahul Sharma                              |
| `Hyderabad` | Displays Hyderabad customers                       |
| `Gold`      | Displays Gold membership customers                 |
| `Email`     | Displays customers with Email as preferred channel |

---

## 7. Filter Customers

The filter section allows users to filter customer records.

### 7.1 Available Filters

| Filter            | Description                                |
| ----------------- | ------------------------------------------ |
| City              | Filters customers by city                  |
| Membership        | Filters customers by membership            |
| Preferred Channel | Filters customers by communication channel |

### 7.2 How to Apply Filters

1. Enter or select filter values.
2. Click **Apply**.
3. Filtered customers will be displayed in the table.

### 7.3 Example Filter

| Filter     | Value     |
| ---------- | --------- |
| City       | Hyderabad |
| Membership | Gold      |

Expected result:

```text
Only matching customers from Hyderabad with Gold membership should be displayed.
```

---

## 8. Sort Customers

The application supports sorting customer records.

### 8.1 Available Sort Fields

| Sort Field        | Description                |
| ----------------- | -------------------------- |
| Customer ID       | Sort by customer ID        |
| Name              | Sort by customer name      |
| City              | Sort by city               |
| Membership        | Sort by membership         |
| Preferred Channel | Sort by preferred channel  |
| Total Orders      | Sort by order count        |
| Total Amount      | Sort by total order amount |

### 8.2 Sort Direction

| Direction  | Description                     |
| ---------- | ------------------------------- |
| Ascending  | Sort from low to high or A to Z |
| Descending | Sort from high to low or Z to A |

### 8.3 How to Sort

1. Select the sort field.
2. Select sort direction.
3. Click **Apply**.
4. Sorted customer list will be displayed.

---

## 9. Reset Filters

The **Reset** button clears all search, filter, and sort selections.

### 9.1 How to Reset

1. Click **Reset**.
2. The application clears all filter values.
3. Full customer list is loaded again.
4. Selected customer details and summary are cleared.

---

## 10. View Customer Details

The user can view detailed information for any customer.

### 10.1 How to View Details

1. Go to the customer table.
2. Click **View** for the required customer.
3. Customer details will appear in the detail section.

### 10.2 Customer Detail Information

The detail section displays:

* Customer ID
* Name
* Email
* Mobile
* City
* Membership
* Preferred communication channel
* Total order amount
* Orders
* Data quality warnings
* AI customer summary

---

## 11. Customer Orders

When a customer is selected, the application displays order details.

### 11.1 Order Fields

| Field      | Description             |
| ---------- | ----------------------- |
| Order ID   | Unique order identifier |
| Order Date | Date of order           |
| Amount     | Order amount            |

### 11.2 Total Order Amount

The total order amount is calculated by adding all valid order amounts for the customer.

Example:

```text
Order 1: ₹2500
Order 2: ₹1200
Total: ₹3700
```

---

## 12. Data Quality Warnings

The application shows warnings when some data is missing or unavailable.

### 12.1 Warning Scenarios

| Scenario                 | Warning                                    |
| ------------------------ | ------------------------------------------ |
| Customer profile missing | Customer profile not found in MongoDB      |
| Orders missing           | No orders found in CSV file                |
| Preference missing       | Customer preference not found in JSON file |
| Invalid CSV data         | Invalid row is skipped and logged          |

### 12.2 Where Warnings Are Displayed

Warnings are shown in the selected customer detail section.

If no warnings exist, the customer record is considered complete for the current data sources.

---

## 13. Generate AI Customer Summary

The application supports AI-powered customer summary generation.

### 13.1 How to Generate AI Summary

1. Select a customer by clicking **View**.
2. Click **Generate AI Summary**.
3. The summary will appear in the customer detail section.

### 13.2 Summary Content

The summary may include:

* Customer name
* City
* Membership
* Order activity
* Total order amount
* Preferred communication channel
* Data quality warning, if any

### 13.3 AI Fallback Behavior

The backend uses Ollama local AI for summary generation.

If Ollama is unavailable, the system automatically returns a rule-based summary.

The user does not need to take any additional action.

---

## 14. Export Full Customer Data

The application supports full customer list export.

### 14.1 Available Full Export Options

| Button       | Description                           |
| ------------ | ------------------------------------- |
| Export CSV   | Downloads full customer list as CSV   |
| Export Excel | Downloads full customer list as Excel |
| Export PDF   | Downloads full customer list as PDF   |

### 14.2 How to Export Full Customer Data

1. Open the Customer360 application.
2. Click the required export button:

    * **Export CSV**
    * **Export Excel**
    * **Export PDF**
3. The file will be downloaded by the browser.

### 14.3 Full Export Includes

* Customer ID
* Name
* Email
* Mobile
* City
* Membership
* Preferred channel
* Total orders
* Total amount
* Warnings

---

## 15. Export Selected Customer Details

The application supports exporting details for the selected customer.

### 15.1 Available Selected Customer Export Options

| Button            | Description                                |
| ----------------- | ------------------------------------------ |
| Export Detail CSV | Downloads selected customer details as CSV |
| Export Detail PDF | Opens browser print dialog to save as PDF  |

### 15.2 How to Export Selected Customer CSV

1. Click **View** for a customer.
2. Click **Export Detail CSV**.
3. A CSV file will be downloaded.

Example file name:

```text
C1001_customer_details.csv
```

### 15.3 How to Export Selected Customer PDF

1. Click **View** for a customer.
2. Click **Export Detail PDF**.
3. A print-friendly page will open.
4. Select **Save as PDF** from the browser print dialog.
5. Save the PDF file.

### 15.4 Selected Customer Export Includes

* Customer profile
* Orders
* Warnings
* Customer summary

---

## 16. Swagger API Usage

Swagger can be used to test backend APIs directly.

### 16.1 Open Swagger

Open:

```text
http://localhost:8080/swagger-ui.html
```

### 16.2 Available API Groups

Swagger shows:

* Customer APIs
* Customer Export APIs

### 16.3 Common Swagger Tests

| API                                       | Purpose              |
| ----------------------------------------- | -------------------- |
| `GET /api/customers`                      | View all customers   |
| `GET /api/customers/{customerId}`         | View customer detail |
| `GET /api/customers/{customerId}/summary` | Generate summary     |
| `GET /api/customers/export/csv`           | Export CSV           |
| `GET /api/customers/export/excel`         | Export Excel         |
| `GET /api/customers/export/pdf`           | Export PDF           |

---

## 17. Common User Actions

### 17.1 View All Customers

1. Open frontend URL.
2. Customer list loads automatically.

### 17.2 Search Customer

1. Enter search keyword.
2. Click **Apply**.
3. Review matching results.

### 17.3 Filter Customer

1. Enter city, membership, or preferred channel.
2. Click **Apply**.
3. Review filtered list.

### 17.4 Sort Customer

1. Select sort field.
2. Select sort direction.
3. Click **Apply**.

### 17.5 View Details

1. Click **View**.
2. Review selected customer profile and order details.

### 17.6 Generate Summary

1. Select customer.
2. Click **Generate AI Summary**.
3. Review generated summary.

### 17.7 Export Data

1. For full list export, use top export buttons.
2. For selected customer export, use detail export buttons.

---

## 18. User Validation Checklist

| Action                    | Expected Result                 |
| ------------------------- | ------------------------------- |
| Open frontend             | Customer360 screen loads        |
| View customer list        | Customer table displays         |
| Search Rahul              | Rahul Sharma displays           |
| Filter Hyderabad          | Hyderabad customers display     |
| Sort by amount descending | Highest amount appears first    |
| Click View                | Customer details display        |
| Generate AI Summary       | Summary appears                 |
| Export CSV                | CSV file downloads              |
| Export Excel              | Excel file downloads            |
| Export PDF                | PDF file downloads              |
| Export Detail CSV         | Selected customer CSV downloads |
| Export Detail PDF         | Browser print dialog opens      |
| Reset filters             | Full customer list reloads      |

---

## 19. Troubleshooting for Users

### 19.1 Customer List Not Loading

Check:

* Backend is running
* Frontend is running
* Backend API works in browser:

```text
http://localhost:8080/api/customers
```

---

### 19.2 AI Summary Not Working

Possible reasons:

* Ollama is not running
* Model is not available
* Backend cannot connect to Ollama

Expected behavior:

```text
The application should still return a fallback summary.
```

---

### 19.3 Export Not Downloading

Check:

* Browser download is not blocked
* Backend is running
* Export API works directly in browser

Test:

```text
http://localhost:8080/api/customers/export/csv
```

---

### 19.4 Frontend Page Not Opening

Check frontend command:

```powershell
npm run dev
```

Open:

```text
http://localhost:5173
```

---

### 19.5 Backend Page Shows Error at Root URL

Opening this URL may not show the application:

```text
http://localhost:8080
```

Use these URLs instead:

```text
http://localhost:8080/api/customers
http://localhost:8080/swagger-ui.html
```

---

## 20. Best Practices for Users

* Start backend before frontend.
* Ensure MongoDB Atlas is accessible.
* Keep Ollama running for AI-generated summaries.
* Use **Reset** before applying a new search if results look filtered.
* Use selected customer export only after clicking **View**.
* Use browser **Save as PDF** for selected detail PDF export.

---

## 21. Application Limitations

The current version does not include:

* User login
* Role-based access control
* Pagination
* Production deployment
* Audit history
* Advanced analytics
* Multi-user access control
* Cloud AI integration

---

## 22. Current User Manual Status

| Area                           | Status    |
| ------------------------------ | --------- |
| Application access steps       | Completed |
| Customer list usage            | Completed |
| Search/filter/sort usage       | Completed |
| Customer detail usage          | Completed |
| AI summary usage               | Completed |
| Full export usage              | Completed |
| Selected customer export usage | Completed |
| Swagger usage                  | Completed |
| Troubleshooting                | Completed |

---

## 23. Conclusion

Customer360 provides a simple and effective interface to view consolidated customer data from MongoDB, CSV, and JSON sources.

Users can search, filter, sort, view details, generate AI summaries, and export both full customer data and selected customer details.

This user manual can be used for project demonstration, review, and handover.
