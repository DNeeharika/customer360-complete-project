## Admin Data Upload Page

Only the `ADMIN` user can access:

```text
/admin/data-upload
```

The Admin Data Upload page supports:

| Feature                   | Purpose                                                 |
| ------------------------- | ------------------------------------------------------- |
| Upload Orders CSV         | Upload and persist customer order data                  |
| Upload Preferences JSON   | Upload and persist customer preference data             |
| View Dynamic Cache Status | Shows current CSV/JSON customer counts                  |
| Reset to Default Data     | Deletes uploaded files and reloads default source files |
| Back to Dashboard         | Returns to customer dashboard                           |
| Logout                    | Ends the current session                                |

## Upload Orders CSV

The Admin can upload a CSV file containing customer order data.

Required columns:

```text
customerId,orderId,orderDate,amount,productCategory,orderStatus,paymentMode,discountAmount
```

After upload:

```text
1. File is sent to backend
2. Backend saves it as customer360-backend/uploads/customer_orders.csv
3. Backend refreshes order cache
4. Customer dashboard shows updated order totals
5. Uploaded file remains available after backend restart
```

## Upload Preferences JSON

The Admin can upload a JSON file containing customer preference data.

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
1. File is sent to backend
2. Backend saves it as customer360-backend/uploads/customer_preferences.json
3. Backend refreshes preference cache
4. Customer dashboard shows updated preference details
5. Uploaded file remains available after backend restart
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

## Role-Based UI

| Feature                         | ADMIN | MANAGER | VIEWER |
| ------------------------------- | ----: | ------: | -----: |
| Customer dashboard              |   Yes |     Yes |    Yes |
| View customer profile           |   Yes |     Yes |    Yes |
| AI summary                      |   Yes |     Yes |     No |
| Export CSV / Excel / PDF / JSON |   Yes |     Yes |     No |
| Export customer profile         |   Yes |     Yes |     No |
| Admin data upload               |   Yes |      No |     No |
| Reset to default data           |   Yes |      No |     No |

## Current Frontend Status

The frontend supports:

```text
Login
Protected route
Logout
Role-based dashboard actions
Admin-only data upload page
Persistent CSV/JSON upload through UI
Reset to default source data
Customer dashboard
Dynamic CSV/JSON refresh through UI
```
