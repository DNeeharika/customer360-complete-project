## Current Features

| Feature                              | Status    |
| ------------------------------------ | --------- |
| Customer profile data from MongoDB   | Completed |
| Customer order data from CSV         | Completed |
| Customer preference data from JSON   | Completed |
| Consolidated customer profile        | Completed |
| Search, filter, sort, pagination     | Completed |
| Export CSV, Excel, PDF, JSON         | Completed |
| Customer profile export              | Completed |
| AI-powered customer summary          | Completed |
| Login page                           | Completed |
| JWT authentication                   | Completed |
| Protected dashboard route            | Completed |
| Logout / switch user                 | Completed |
| Role-Based Access Control            | Completed |
| Admin CSV/JSON upload API            | Completed |
| Admin data upload UI                 | Completed |
| Dynamic CSV/JSON refresh             | Completed |
| Persistent uploaded CSV/JSON storage | Completed |
| Reset to default CSV/JSON data       | Completed |

## Dynamic Data Upload

The Admin user can upload new CSV and JSON files from the Admin Data Upload page.

| File Type | API                                  | Purpose                          |
| --------- | ------------------------------------ | -------------------------------- |
| CSV       | `/api/admin/data/upload/orders`      | Refresh customer order data      |
| JSON      | `/api/admin/data/upload/preferences` | Refresh customer preference data |

Uploaded files are saved persistently under:

```text
customer360-backend/uploads
```

Saved file names:

```text
customer_orders.csv
customer_preferences.json
```

On backend startup, the application follows this loading order:

```text
1. Check customer360-backend/uploads
2. If uploaded CSV/JSON files are available, load them
3. If uploaded files are not available, load default files from src/main/resources/data
```

## Reset to Default Data

The Admin user can reset uploaded CSV/JSON data back to the original default data.

Reset API:

```http
POST /api/admin/data/reset-defaults
```

Reset behavior:

```text
1. Deletes uploaded customer_orders.csv from customer360-backend/uploads
2. Deletes uploaded customer_preferences.json from customer360-backend/uploads
3. Reloads default CSV and JSON files from src/main/resources/data
4. Refreshes the dashboard with default data
```

Expected default data count after reset:

| Source                          | Expected Count |
| ------------------------------- | -------------: |
| Orders CSV customer count       |              6 |
| Preferences JSON customer count |              5 |

## Upload Folder Git Behavior

Runtime uploaded CSV/JSON files are not committed to Git.

The project `.gitignore` excludes:

```gitignore
customer360-backend/uploads/*.csv
customer360-backend/uploads/*.json
```

This prevents uploaded customer data from being pushed to GitHub.
