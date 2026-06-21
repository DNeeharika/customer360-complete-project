## Admin Data APIs

The backend provides Admin-only APIs for dynamic CSV/JSON upload, persistent file storage, status check, and reset to default data.

```http
GET  /api/admin/data/status
POST /api/admin/data/upload/orders
POST /api/admin/data/upload/preferences
POST /api/admin/data/reset-defaults
```

All Admin data APIs require:

```text
Authorization: Bearer <admin-token>
```

Only users with the `ADMIN` role can access these APIs.

## Dynamic CSV and JSON Upload

Admin can upload new CSV and JSON files using the Admin APIs or the React Admin Data Upload page.

Orders CSV upload:

```http
POST /api/admin/data/upload/orders
```

Preferences JSON upload:

```http
POST /api/admin/data/upload/preferences
```

The uploaded files are saved persistently in:

```text
customer360-backend/uploads
```

The backend stores uploaded files using fixed names:

```text
customer_orders.csv
customer_preferences.json
```

After upload, the backend immediately refreshes the in-memory cache, so the dashboard shows the latest uploaded data without restarting the application.

## Persistent Upload Behavior

The backend loading logic works as follows:

```text
Application starts
    ↓
Check customer360-backend/uploads folder
    ↓
If uploaded customer_orders.csv exists, load it
Else load default data/customer_orders.csv from resources
    ↓
If uploaded customer_preferences.json exists, load it
Else load default data/customer_preferences.json from resources
```

This means uploaded CSV/JSON data remains available even after backend restart.

## Reset to Default Data

Admin can reset the application back to default CSV and JSON files.

Reset API:

```http
POST /api/admin/data/reset-defaults
```

Reset behavior:

```text
1. Deletes customer360-backend/uploads/customer_orders.csv
2. Deletes customer360-backend/uploads/customer_preferences.json
3. Reloads default CSV from src/main/resources/data/customer_orders.csv
4. Reloads default JSON from src/main/resources/data/customer_preferences.json
5. Updates backend cache immediately
```

Expected default status after reset:

```text
ordersCustomerCount = 6
preferencesCustomerCount = 5
```

## Orders CSV Format

The orders CSV should contain the following columns:

```text
customerId,orderId,orderDate,amount,productCategory,orderStatus,paymentMode,discountAmount
```

Example:

```csv
customerId,orderId,orderDate,amount,productCategory,orderStatus,paymentMode,discountAmount
C1001,O9001,2026-06-20,9999,Electronics,Completed,UPI,500
C1002,O9002,2026-06-20,2500,Fashion,Completed,Credit Card,100
```

## Preferences JSON Format

The preferences JSON should be an array of preference objects.

Example:

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

## Test Reset API Using PowerShell

```powershell
$body = @{
  username = "admin"
  password = "Admin@123"
} | ConvertTo-Json

$login = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

$headers = @{
  Authorization = "Bearer $($login.token)"
}

Invoke-RestMethod `
  -Uri "http://localhost:8080/api/admin/data/reset-defaults" `
  -Method POST `
  -Headers $headers
```

## Current Backend Status

The backend supports:

```text
Authentication
JWT token
RBAC
Customer data consolidation
MongoDB + CSV + JSON integration
Export APIs
AI summary API
Admin dynamic CSV/JSON upload
Persistent uploaded CSV/JSON storage
Reset to default data
Swagger API testing
```
