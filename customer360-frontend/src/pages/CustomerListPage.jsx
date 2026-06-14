import { useEffect, useState } from "react";
import {
  downloadExport,
  getCustomerDetails,
  getCustomers,
  getCustomerSummary,
} from "../api/customerApi";

function CustomerListPage() {
  const [customers, setCustomers] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [summary, setSummary] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [filters, setFilters] = useState({
    search: "",
    city: "",
    membership: "",
    preferredChannel: "",
    sortBy: "customerId",
    sortDir: "asc",
  });

  const loadCustomers = async (customFilters = filters) => {
    try {
      setLoading(true);
      setError("");

      const data = await getCustomers(customFilters);
      setCustomers(data);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load customers");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCustomers();
  }, []);

  const handleChange = (event) => {
    setFilters({
      ...filters,
      [event.target.name]: event.target.value,
    });
  };

  const handleSearch = () => {
    loadCustomers();
  };

  const handleReset = () => {
    const resetFilters = {
      search: "",
      city: "",
      membership: "",
      preferredChannel: "",
      sortBy: "customerId",
      sortDir: "asc",
    };

    setFilters(resetFilters);
    setSelectedCustomer(null);
    setSummary("");
    loadCustomers(resetFilters);
  };

  const handleViewDetails = async (customerId) => {
    try {
      setError("");
      const data = await getCustomerDetails(customerId);
      setSelectedCustomer(data);
      setSummary("");
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load customer details");
    }
  };

  const handleSummary = async (customerId) => {
    try {
      setError("");
      const data = await getCustomerSummary(customerId);
      setSummary(data.summary);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load summary");
    }
  };

  const exportSelectedCustomerCsv = () => {
    if (!selectedCustomer) {
      return;
    }

    const rows = [];

    rows.push(["Customer Details"]);
    rows.push(["Customer ID", selectedCustomer.customerId]);
    rows.push(["Name", selectedCustomer.name]);
    rows.push(["Email", selectedCustomer.email]);
    rows.push(["Mobile", selectedCustomer.mobile]);
    rows.push(["City", selectedCustomer.city]);
    rows.push(["Membership", selectedCustomer.membership]);
    rows.push(["Preferred Channel", selectedCustomer.preferredChannel]);
    rows.push(["Total Orders", selectedCustomer.totalOrders]);
    rows.push(["Total Amount", selectedCustomer.totalOrderAmount]);
    rows.push([]);

    rows.push(["Orders"]);
    rows.push(["Order ID", "Order Date", "Amount"]);

    selectedCustomer.orders.forEach((order) => {
      rows.push([order.orderId, order.orderDate, order.amount]);
    });

    rows.push([]);

    rows.push(["Warnings"]);
    if (selectedCustomer.warnings?.length > 0) {
      selectedCustomer.warnings.forEach((warning) => {
        rows.push([warning]);
      });
    } else {
      rows.push(["No warnings"]);
    }

    rows.push([]);

    rows.push(["Customer Summary"]);
    rows.push([summary || "Summary not generated"]);

    const csvContent = rows
      .map((row) => row.map((value) => `"${value}"`).join(","))
      .join("\n");

    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);

    const link = document.createElement("a");
    link.href = url;
    link.download = `${selectedCustomer.customerId}_customer_details.csv`;
    link.click();

    URL.revokeObjectURL(url);
  };

  const exportSelectedCustomerPdf = () => {
    if (!selectedCustomer) {
      return;
    }

    const printWindow = window.open("", "_blank");

    const ordersHtml = selectedCustomer.orders
      .map(
        (order) => `
          <tr>
            <td>${order.orderId}</td>
            <td>${order.orderDate}</td>
            <td>₹${order.amount}</td>
          </tr>
        `
      )
      .join("");

    const warningsHtml =
      selectedCustomer.warnings?.length > 0
        ? selectedCustomer.warnings
            .map((warning) => `<li>${warning}</li>`)
            .join("")
        : "<li>No warnings</li>";

    printWindow.document.write(`
      <html>
        <head>
          <title>${selectedCustomer.customerId} Customer Details</title>
          <style>
            body {
              font-family: Arial, Helvetica, sans-serif;
              padding: 30px;
              color: #111827;
            }

            h1 {
              margin-bottom: 20px;
            }

            h2 {
              margin-top: 28px;
              border-bottom: 1px solid #e5e7eb;
              padding-bottom: 8px;
            }

            .profile {
              display: grid;
              grid-template-columns: 1fr 1fr;
              gap: 12px;
              border: 1px solid #e5e7eb;
              border-radius: 12px;
              padding: 20px;
              background: #f8fafc;
            }

            .profile p {
              margin: 0;
              font-size: 14px;
            }

            .label {
              font-weight: bold;
              text-transform: uppercase;
              display: block;
              margin-bottom: 4px;
            }

            table {
              width: 100%;
              border-collapse: collapse;
              margin-top: 12px;
            }

            th,
            td {
              border-bottom: 1px solid #e5e7eb;
              padding: 10px;
              text-align: left;
            }

            th {
              background: #f8fafc;
            }

            .summary {
              border: 1px solid #c7d2fe;
              background: #eef2ff;
              padding: 16px;
              border-radius: 12px;
              line-height: 1.6;
            }

            .warnings {
              color: #9a3412;
            }
          </style>
        </head>

        <body>
          <h1>Customer Details</h1>

          <div class="profile">
            <p><span class="label">ID</span>${selectedCustomer.customerId}</p>
            <p><span class="label">Name</span>${selectedCustomer.name}</p>
            <p><span class="label">Email</span>${selectedCustomer.email}</p>
            <p><span class="label">Mobile</span>${selectedCustomer.mobile}</p>
            <p><span class="label">City</span>${selectedCustomer.city}</p>
            <p><span class="label">Membership</span>${selectedCustomer.membership}</p>
            <p><span class="label">Channel</span>${selectedCustomer.preferredChannel}</p>
            <p><span class="label">Total Amount</span>₹${selectedCustomer.totalOrderAmount}</p>
          </div>

          <h2>Orders</h2>
          <table>
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Date</th>
                <th>Amount</th>
              </tr>
            </thead>
            <tbody>
              ${ordersHtml}
            </tbody>
          </table>

          <h2>Warnings</h2>
          <ul class="warnings">
            ${warningsHtml}
          </ul>

          <h2>Customer Summary</h2>
          <div class="summary">
            ${summary || "Summary not generated"}
          </div>
        </body>
      </html>
    `);

    printWindow.document.close();
    printWindow.focus();
    printWindow.print();
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <div>
          <h1>Customer360</h1>
          <p>Consolidated customer profile, orders, preferences, and summary</p>
        </div>

        <div className="export-actions">
          <button onClick={() => downloadExport("csv")}>Export CSV</button>
          <button onClick={() => downloadExport("excel")}>Export Excel</button>
          <button onClick={() => downloadExport("pdf")}>Export PDF</button>
        </div>
      </header>

      <section className="filter-card">
        <input
          name="search"
          placeholder="Search customer, city, membership..."
          value={filters.search}
          onChange={handleChange}
        />

        <input
          name="city"
          placeholder="City"
          value={filters.city}
          onChange={handleChange}
        />

        <input
          name="membership"
          placeholder="Membership"
          value={filters.membership}
          onChange={handleChange}
        />

        <input
          name="preferredChannel"
          placeholder="Preferred Channel"
          value={filters.preferredChannel}
          onChange={handleChange}
        />

        <select name="sortBy" value={filters.sortBy} onChange={handleChange}>
          <option value="customerId">Customer ID</option>
          <option value="name">Name</option>
          <option value="city">City</option>
          <option value="membership">Membership</option>
          <option value="preferredChannel">Preferred Channel</option>
          <option value="totalOrders">Total Orders</option>
          <option value="totalOrderAmount">Total Amount</option>
        </select>

        <select name="sortDir" value={filters.sortDir} onChange={handleChange}>
          <option value="asc">Ascending</option>
          <option value="desc">Descending</option>
        </select>

        <button onClick={handleSearch}>Apply</button>
        <button className="secondary" onClick={handleReset}>
          Reset
        </button>
      </section>

      {error && <div className="error-box">{error}</div>}
      {loading && <div className="loading-box">Loading customers...</div>}

      <main className="main-grid">
        <section className="table-card">
          <h2>Customers</h2>

          <table>
            <thead>
              <tr>
                <th>Customer ID</th>
                <th>Name</th>
                <th>City</th>
                <th>Membership</th>
                <th>Channel</th>
                <th>Orders</th>
                <th>Total Amount</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {customers.map((customer) => (
                <tr key={customer.customerId}>
                  <td>{customer.customerId}</td>
                  <td>{customer.name}</td>
                  <td>{customer.city}</td>
                  <td>{customer.membership}</td>
                  <td>{customer.preferredChannel}</td>
                  <td>{customer.totalOrders}</td>
                  <td>₹{customer.totalOrderAmount}</td>
                  <td>
                    <button onClick={() => handleViewDetails(customer.customerId)}>
                      View
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        <section className="detail-card">
          <h2>Customer Details</h2>

          {!selectedCustomer && (
            <p className="empty-state">Select a customer to view details.</p>
          )}

          {selectedCustomer && (
            <>
              <div className="detail-actions">
                <button onClick={exportSelectedCustomerCsv}>
                  Export Detail CSV
                </button>
                <button onClick={exportSelectedCustomerPdf}>
                  Export Detail PDF
                </button>
              </div>

              <div className="profile-grid">
                <p>
                  <strong>ID:</strong> {selectedCustomer.customerId}
                </p>
                <p>
                  <strong>Name:</strong> {selectedCustomer.name}
                </p>
                <p>
                  <strong>Email:</strong> {selectedCustomer.email}
                </p>
                <p>
                  <strong>Mobile:</strong> {selectedCustomer.mobile}
                </p>
                <p>
                  <strong>City:</strong> {selectedCustomer.city}
                </p>
                <p>
                  <strong>Membership:</strong> {selectedCustomer.membership}
                </p>
                <p>
                  <strong>Channel:</strong> {selectedCustomer.preferredChannel}
                </p>
                <p>
                  <strong>Total Amount:</strong> ₹
                  {selectedCustomer.totalOrderAmount}
                </p>
              </div>

              {selectedCustomer.warnings?.length > 0 && (
                <div className="warning-box">
                  <strong>Warnings:</strong>
                  <ul>
                    {selectedCustomer.warnings.map((warning, index) => (
                      <li key={index}>{warning}</li>
                    ))}
                  </ul>
                </div>
              )}

              <h3>Orders</h3>
              <table>
                <thead>
                  <tr>
                    <th>Order ID</th>
                    <th>Date</th>
                    <th>Amount</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedCustomer.orders.map((order) => (
                    <tr key={order.orderId}>
                      <td>{order.orderId}</td>
                      <td>{order.orderDate}</td>
                      <td>₹{order.amount}</td>
                    </tr>
                  ))}
                </tbody>
              </table>

              <button
                className="summary-button"
                onClick={() => handleSummary(selectedCustomer.customerId)}
              >
                Generate AI Summary
              </button>

              {summary && (
                <div className="summary-box">
                  <h3>AI Customer Summary</h3>
                  <p>{summary}</p>
                </div>
              )}
            </>
          )}
        </section>
      </main>
    </div>
  );
}

export default CustomerListPage;
