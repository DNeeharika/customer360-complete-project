# Customer360 Frontend

This is the React frontend for the Customer360 project.

The frontend provides:

```text
Login
Protected routing
Role-Based UI
Customer dashboard
Customer profile view
Search/filter/sort/pagination
Exports
AI summary
Admin data upload
Reset to default data
Admin customer profile management
```

## Frontend Technology Stack

| Technology   | Purpose                                    |
| ------------ | ------------------------------------------ |
| React        | UI development                             |
| Vite         | Frontend development server and build tool |
| React Router | Page routing and protected routes          |
| Axios        | Backend API calls                          |
| CSS          | Styling and responsive layout              |
| localStorage | Stores JWT token and logged-in user data   |

## Frontend URL

```text
http://localhost:5174
```

The frontend may also run on:

```text
http://localhost:5173
```

depending on the available Vite port.

## Backend API URL

```text
http://localhost:8080
```

## Frontend Folder Structure

```text
customer360-frontend
├── src
│   ├── api
│   │   ├── authApi.js
│   │   ├── customerApi.js
│   │   ├── adminDataApi.js
│   │   └── adminCustomerApi.js
│   │
│   ├── context
│   │   └── AuthContext.jsx
│   │
│   ├── pages
│   │   ├── LoginPage.jsx
│   │   ├── CustomerListPage.jsx
│   │   ├── AdminDataUploadPage.jsx
│   │   └── AdminCustomerManagementPage.jsx
│   │
│   ├── routes
│   │   └── ProtectedRoute.jsx
│   │
│   ├── App.jsx
│   └── App.css
│
├── package.json
├── vite.config.js
└── README.md
```

## Main Routes

| Route                | Page                                   | Access                 |
| -------------------- | -------------------------------------- | ---------------------- |
| `/login`             | Login page                             | Public                 |
| `/customers`         | Customer dashboard                     | ADMIN, MANAGER, VIEWER |
| `/admin/data-upload` | Admin CSV/JSON upload page             | ADMIN only             |
| `/admin/customers`   | Admin customer profile management page | ADMIN only             |

## Login Users

| Username | Password    | Role    |
| -------- | ----------- | ------- |
| admin    | Admin@123   | ADMIN   |
| manager  | Manager@123 | MANAGER |
| viewer   | Viewer@123  | VIEWER  |

## Login Flow

```text
User opens frontend
    ↓
User enters username/password
    ↓
Frontend calls POST /api/auth/login
    ↓
Backend returns JWT token and user role
    ↓
Frontend stores token in localStorage
    ↓
User is redirected to /customers
```

JWT token is stored in:

```text
localStorage.customer360_token
```

Logged-in user details are stored in:

```text
localStorage.customer360_user
```

## Logout Flow

When the user clicks Logout:

```text
1. customer360_token is removed from localStorage
2. customer360_user is removed from localStorage
3. User is redirected to /login
```

## Role-Based UI

| Feature                      | ADMIN | MANAGER | VIEWER |
| ---------------------------- | ----: | ------: | -----: |
| Customer dashboard           |   Yes |     Yes |    Yes |
| View customer profile        |   Yes |     Yes |    Yes |
| AI summary                   |   Yes |     Yes |     No |
| Export CSV                   |   Yes |     Yes |     No |
| Export JSON                  |   Yes |     Yes |     No |
| Export Excel                 |   Yes |     Yes |     No |
| Export PDF                   |   Yes |     Yes |     No |
| Export customer profile CSV  |   Yes |     Yes |     No |
| Print customer profile / PDF |   Yes |     Yes |     No |
| Upload CSV/JSON              |   Yes |      No |     No |
| Reset to default data        |   Yes |      No |     No |
| Add customer profile         |   Yes |      No |     No |
| Edit customer profile        |   Yes |      No |     No |
| Delete customer profile      |   Yes |      No |     No |

## Important Frontend Files

| File                                        | Purpose                                                 |
| ------------------------------------------- | ------------------------------------------------------- |
| `src/api/authApi.js`                        | Login and current-user API calls                        |
| `src/api/customerApi.js`                    | Customer dashboard, details, summary, export API calls  |
| `src/api/adminDataApi.js`                   | Admin CSV/JSON upload, status, reset API calls          |
| `src/api/adminCustomerApi.js`               | Admin customer profile create/update/delete API calls   |
| `src/context/AuthContext.jsx`               | Stores token, user, role, login/logout, and role helper |
| `src/routes/ProtectedRoute.jsx`             | Protects authenticated pages                            |
| `src/pages/LoginPage.jsx`                   | Login screen                                            |
| `src/pages/CustomerListPage.jsx`            | Main dashboard                                          |
| `src/pages/AdminDataUploadPage.jsx`         | CSV/JSON upload and reset page                          |
| `src/pages/AdminCustomerManagementPage.jsx` | MongoDB customer profile management page                |
| `src/App.jsx`                               | Frontend routes                                         |
| `src/App.css`                               | Application styling                                     |

## Customer Dashboard

Route:

```text
/customers
```

The dashboard supports:

```text
Customer list
Backend pagination
Search
Filters
Sorting
Customer profile view
Customer order summary
Customer preferences
Data quality warnings
AI summary
CSV export
JSON export
Excel export
PDF export
Customer profile CSV export
Print/PDF profile
Logout
```

## Dashboard Header Actions

For ADMIN:

```text
Upload Data
Manage Customers
Logout
```

For MANAGER:

```text
Logout
```

For VIEWER:

```text
Logout
```

Admin-specific pages are hidden for MANAGER and VIEWER.

## Admin Data Upload Page

Route:

```text
/admin/data-upload
```

Only ADMIN can access this page.

The page supports:

| Feature                 | Purpose                                          |
| ----------------------- | ------------------------------------------------ |
| Upload Orders CSV       | Upload and persist customer order data           |
| Upload Preferences JSON | Upload and persist customer preference data      |
| View Data Status        | Shows current CSV/JSON customer counts           |
| Reset to Default Data   | Deletes uploaded files and reloads default files |
| Back to Dashboard       | Returns to customer dashboard                    |
| Logout                  | Ends user session                                |

## Upload Orders CSV

Required columns:

```text
customerId,orderId,orderDate,amount,productCategory,orderStatus,paymentMode,discountAmount
```

Example:

```csv
customerId,orderId,orderDate,amount,productCategory,orderStatus,paymentMode,discountAmount
C1001,O9001,2026-06-20,9999,Electronics,Completed,UPI,500
C1002,O9002,2026-06-20,2500,Fashion,Completed,Credit Card,100
```

After upload:

```text
1. Frontend sends file to backend
2. Backend saves file as customer360-backend/uploads/customer_orders.csv
3. Backend refreshes order cache
4. Dashboard immediately reflects updated order data
5. Uploaded file is loaded again after backend restart
```

## Upload Preferences JSON

Expected format:

```json
[
  {
    "customerId": "C1001",
    "membership": "Gold",
    "preferredChannel": "Email",
    "preferredLanguage": "English",
    "notificationOptIn": true,
    "marketingConsent": true,
    "preferredContactTime": "Evening"
  }
]
```

After upload:

```text
1. Frontend sends file to backend
2. Backend saves file as customer360-backend/uploads/customer_preferences.json
3. Backend refreshes preference cache
4. Dashboard immediately reflects updated preference data
5. Uploaded file is loaded again after backend restart
```

## Reset to Default Data

The Admin Data Upload page has a `Reset to Default Data` button.

When clicked:

```text
1. Confirmation popup is shown
2. Uploaded CSV/JSON files are deleted from backend uploads folder
3. Default CSV/JSON files are reloaded from backend resources
4. Status cards are refreshed
5. Dashboard returns to default source data
```

Expected status after reset:

```text
Orders Customer Count: 6
Preferences Customer Count: 5
```

## Admin Customer Management Page

Route:

```text
/admin/customers
```

Only ADMIN can access this page.

The page supports:

| Feature           | Purpose                                   |
| ----------------- | ----------------------------------------- |
| Add Customer      | Creates a new customer profile in MongoDB |
| Edit Customer     | Updates an existing customer profile      |
| Delete Customer   | Deletes a customer profile from MongoDB   |
| Refresh           | Reloads customer profile table            |
| Back to Dashboard | Returns to customer dashboard             |
| Upload Data       | Opens Admin Data Upload page              |
| Logout            | Ends current session                      |

## Add Customer Form Fields

| Field         | Required | Description                  |
| ------------- | -------: | ---------------------------- |
| Customer ID   |      Yes | Unique customer identifier   |
| Name          |      Yes | Customer name                |
| Email         |      Yes | Customer email               |
| Mobile        |      Yes | Customer mobile              |
| City          |      Yes | Customer city                |
| Gender        |       No | Customer gender              |
| Age           |       No | Customer age                 |
| Date of Birth |       No | Customer DOB                 |
| Customer Type |       No | Retail / Premium / Corporate |
| Address       |       No | Customer address             |

## Important Note for Customer Management

The Admin Customer Management page updates only MongoDB profile data.

```text
MongoDB → Customer Profile
CSV     → Orders
JSON    → Preferences
```

If a new customer is added in MongoDB but does not exist in CSV or JSON:

```text
Dashboard will show the customer profile
Orders may show missing or zero
Preferences may show missing
Data quality status may show warning
```

This is expected and proves that the system is integrating separate data sources.

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

## Recommended Frontend Testing

### 1. Login as Admin

```text
admin / Admin@123
```

Test:

```text
Dashboard loads
Upload Data button is visible
Manage Customers button is visible
Exports are visible
AI Summary is visible
```

### 2. Test Admin Data Upload

Open:

```text
/admin/data-upload
```

Test:

```text
Upload Orders CSV
Upload Preferences JSON
Check status cards
Reset to Default Data
```

### 3. Test Admin Customer Management

Open:

```text
/admin/customers
```

Test:

```text
Add customer C3001
Edit customer C3001
Delete customer C3001
Refresh customer table
Check dashboard after each action
```

### 4. Login as Manager

```text
manager / Manager@123
```

Expected:

```text
Dashboard visible
AI summary visible
Exports visible
Upload Data hidden
Manage Customers hidden
/admin/data-upload blocked
/admin/customers blocked
```

### 5. Login as Viewer

```text
viewer / Viewer@123
```

Expected:

```text
Dashboard visible
Customer profile visible
AI summary hidden
Exports hidden
Upload Data hidden
Manage Customers hidden
/admin/data-upload blocked
/admin/customers blocked
```

## CORS Note

If the frontend runs on a different Vite port, update backend CORS in:

```text
customer360-backend/src/main/java/com/customer360/backend/security/SecurityConfig.java
```

Currently allowed origins:

```text
http://localhost:5173
http://localhost:5174
```

## Current Frontend Status

The frontend currently supports:

```text
Login
JWT token storage
Protected routes
Logout
Role-based UI
Customer dashboard
Customer profile view
Search/filter/sort/pagination
Export actions
AI summary
Admin CSV/JSON upload
Persistent uploaded data support
Reset to default data
Admin customer profile CRUD UI
Responsive styling
```
