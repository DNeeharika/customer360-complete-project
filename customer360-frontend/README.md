# Customer360 Frontend

This is the React frontend for the Customer360 project. It provides login, protected dashboard, role-based access, customer search/filter/sort/pagination, customer profile view, export actions, AI summary, and Admin data upload.

## Technology Stack

| Technology   | Usage                       |
| ------------ | --------------------------- |
| React        | UI development              |
| Vite         | Development server          |
| React Router | Routing and protected pages |
| Axios        | Backend API calls           |
| CSS          | Dashboard styling           |

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

## Main Pages

| Route                | Page               | Access                 |
| -------------------- | ------------------ | ---------------------- |
| `/login`             | Login page         | Public                 |
| `/customers`         | Customer dashboard | ADMIN, MANAGER, VIEWER |
| `/admin/data-upload` | Admin data upload  | ADMIN only             |

## Login Users

| Username | Password    | Role    |
| -------- | ----------- | ------- |
| admin    | Admin@123   | ADMIN   |
| manager  | Manager@123 | MANAGER |
| viewer   | Viewer@123  | VIEWER  |

## User Flow

```text
Open App
    ↓
Login Page
    ↓
JWT token stored in localStorage
    ↓
Protected Customer Dashboard
    ↓
Logout clears token and redirects to Login
```

## Role-Based UI

| Feature                         | ADMIN | MANAGER | VIEWER |
| ------------------------------- | ----: | ------: | -----: |
| Customer dashboard              |   Yes |     Yes |    Yes |
| View customer profile           |   Yes |     Yes |    Yes |
| AI summary                      |   Yes |     Yes |     No |
| Export CSV / Excel / PDF / JSON |   Yes |     Yes |     No |
| Export customer profile         |   Yes |     Yes |     No |
| Admin data upload               |   Yes |      No |     No |

## Frontend Folder Structure

```text
src
├── api
│   ├── authApi.js
│   ├── customerApi.js
│   └── adminDataApi.js
│
├── context
│   └── AuthContext.jsx
│
├── pages
│   ├── LoginPage.jsx
│   ├── CustomerListPage.jsx
│   └── AdminDataUploadPage.jsx
│
├── routes
│   └── ProtectedRoute.jsx
│
├── App.jsx
└── App.css
```

## Important Files

| File                                | Purpose                               |
| ----------------------------------- | ------------------------------------- |
| `src/api/authApi.js`                | Login and current user API calls      |
| `src/api/customerApi.js`            | Customer dashboard API calls          |
| `src/api/adminDataApi.js`           | Admin CSV/JSON upload API calls       |
| `src/context/AuthContext.jsx`       | Stores login state, token, user, role |
| `src/routes/ProtectedRoute.jsx`     | Protects dashboard and admin pages    |
| `src/pages/LoginPage.jsx`           | Login screen                          |
| `src/pages/CustomerListPage.jsx`    | Customer dashboard                    |
| `src/pages/AdminDataUploadPage.jsx` | Admin CSV/JSON upload screen          |
| `src/App.jsx`                       | Application routes                    |
| `src/App.css`                       | Styling                               |

## Run Frontend

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-frontend
npm run dev
```

Open:

```text
http://localhost:5174
```

## Build Frontend

```powershell
cd C:\Users\neeha\Downloads\customer360\customer360-frontend
npm run build
```

## Login Page

The login page allows users to log in using demo credentials.

After successful login:

```text
/login → /customers
```

JWT token is saved in:

```text
localStorage.customer360_token
```

Logged-in user details are saved in:

```text
localStorage.customer360_user
```

## Customer Dashboard

The dashboard supports:

```text
Search
Filter
Sort
Backend pagination
Customer profile view
AI summary
Export CSV
Export JSON
Export Excel
Export PDF
Profile CSV export
Print / PDF
Logout
```

## Admin Data Upload Page

Only ADMIN can access:

```text
/admin/data-upload
```

The page allows Admin to upload:

| Upload           | Purpose                 |
| ---------------- | ----------------------- |
| Orders CSV       | Refresh order data      |
| Preferences JSON | Refresh preference data |

After upload, the dashboard reflects the latest uploaded CSV/JSON data.

## Logout

Logout clears:

```text
customer360_token
customer360_user
```

and redirects the user back to:

```text
/login
```

## Development Notes

If the frontend runs on a new port, update backend CORS in:

```text
customer360-backend/src/main/java/com/customer360/backend/security/SecurityConfig.java
```

Current allowed frontend origins:

```text
http://localhost:5173
http://localhost:5174
```

## Current Frontend Status

The frontend supports:

```text
Login
Protected route
Logout
Role-based dashboard actions
Admin-only data upload page
Customer dashboard
Dynamic CSV/JSON refresh through UI
```
