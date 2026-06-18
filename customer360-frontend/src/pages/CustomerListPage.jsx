import { useEffect, useState } from "react";
import {
  downloadExport,
  getCustomerDetails,
  getCustomers,
  getCustomerSummary,
} from "../api/customerApi";

function CustomerListPage() {
  const defaultFilters = {
    search: "",
    city: "",
    membership: "",
    preferredChannel: "",
    sortBy: "customerId",
    sortDir: "asc",
  };

  const [customers, setCustomers] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [summary, setSummary] = useState("");
  const [filters, setFilters] = useState(defaultFilters);
  const [loading, setLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [summaryLoading, setSummaryLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    loadCustomers(defaultFilters);
  }, []);

  const loadCustomers = async (filterValues = filters) => {
    try {
      setLoading(true);
      setError("");

      const params = {};

      Object.entries(filterValues).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== "") {
          params[key] = value;
        }
      });

      const data = await getCustomers(params);
      setCustomers(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load customers. Please check whether backend is running.");
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (event) => {
    const { name, value } = event.target;

    setFilters((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleApply = () => {
    setSelectedCustomer(null);
    setSummary("");
    loadCustomers(filters);
  };

  const handleReset = () => {
    setFilters(defaultFilters);
    setSelectedCustomer(null);
    setSummary("");
    loadCustomers(defaultFilters);
  };

  const handleViewDetails = async (customerId) => {
    try {
      setDetailLoading(true);
      setError("");
      setSummary("");

      const data = await getCustomerDetails(customerId);
      setSelectedCustomer(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load customer details.");
    } finally {
      setDetailLoading(false);
    }
  };

  const handleGenerateSummary = async () => {
    if (!selectedCustomer) {
      return;
    }

    try {
      setSummaryLoading(true);
      setError("");

      const data = await getCustomerSummary(selectedCustomer.customerId);
      setSummary(data.summary);
    } catch (err) {
      console.error(err);
      setError("Unable to generate customer summary.");
    } finally {
      setSummaryLoading(false);
    }
  };

  const formatCurrency = (value) => {
    if (value === null || value === undefined || value === "") {
      return "₹0";
    }

    return `₹${value}`;
  };

  const formatBoolean = (value) => {
    return value ? "Yes" : "No";
  };

  const safeValue = (value) => {
    if (value === null || value === undefined || value === "") {
      return "Not Available";
    }

    return value;
  };

  const escapeCsv = (value) => {
    if (value === null || value === undefined) {
      return "";
    }

    const text = String(value).replaceAll('"', '""');
    return `"${text}"`;
  };

  const exportSelectedCustomerCsv = () => {
    if (!selectedCustomer) {
      return;
    }

    const rows = [];

    rows.push(["Section", "Field", "Value"]);
    rows.push(["Profile", "Customer ID", selectedCustomer.customerId]);
    rows.push(["Profile", "Name", selectedCustomer.name]);
    rows.push(["Profile", "Email", selectedCustomer.email]);
    rows.push(["Profile", "Mobile", selectedCustomer.mobile]);
    rows.push(["Profile", "City", selectedCustomer.city]);
    rows.push(["Profile", "Gender", selectedCustomer.gender]);
    rows.push(["Profile", "Age", selectedCustomer.age]);
    rows.push(["Profile", "Date of Birth", selectedCustomer.dateOfBirth]);
    rows.push(["Profile", "Address", selectedCustomer.address]);
    rows.push(["Profile", "Customer Type", selectedCustomer.customerType]);

    rows.push(["Preference", "Membership", selectedCustomer.membership]);
    rows.push(["Preference", "Preferred Channel", selectedCustomer.preferredChannel]);
    rows.push(["Preference", "Preferred Language", selectedCustomer.preferredLanguage]);
    rows.push(["Preference", "Notification Opt-In", formatBoolean(selectedCustomer.notificationOptIn)]);
    rows.push(["Preference", "Marketing Consent", formatBoolean(selectedCustomer.marketingConsent)]);
    rows.push(["Preference", "Preferred Contact Time", selectedCustomer.preferredContactTime]);

    rows.push(["Business Metrics", "Total Orders", selectedCustomer.totalOrders]);
    rows.push(["Business Metrics", "Total Order Amount", selectedCustomer.totalOrderAmount]);
    rows.push(["Business Metrics", "Total Discount Amount", selectedCustomer.totalDiscountAmount]);
    rows.push(["Business Metrics", "Net Order Amount", selectedCustomer.netOrderAmount]);
    rows.push(["Business Metrics", "Reward Points", selectedCustomer.rewardPoints]);
    rows.push(["Business Metrics", "Customer Segment", selectedCustomer.customerSegment]);
    rows.push(["Business Metrics", "Data Quality Score", selectedCustomer.dataQualityScore]);
    rows.push(["Business Metrics", "Data Quality Status", selectedCustomer.dataQualityStatus]);

    rows.push([]);
    rows.push([
      "Orders",
      "Order ID",
      "Order Date",
      "Amount",
      "Discount",
      "Net Amount",
      "Product Category",
      "Order Status",
      "Payment Mode",
    ]);

    selectedCustomer.orders?.forEach((order) => {
      rows.push([
        "Orders",
        order.orderId,
        order.orderDate,
        order.amount,
        order.discountAmount,
        order.netAmount,
        order.productCategory,
        order.orderStatus,
        order.paymentMode,
      ]);
    });

    rows.push([]);
    rows.push(["Warnings", "Message", ""]);
    if (selectedCustomer.warnings?.length > 0) {
      selectedCustomer.warnings.forEach((warning) => {
        rows.push(["Warnings", warning, ""]);
      });
    } else {
      rows.push(["Warnings", "No warnings", ""]);
    }

    rows.push([]);
    rows.push(["Summary", "Customer Summary", summary || "Summary not generated"]);

    const csvContent = rows
      .map((row) => row.map(escapeCsv).join(","))
      .join("\n");

    const blob = new Blob([csvContent], {
      type: "text/csv;charset=utf-8;",
    });

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

    const orderRows = selectedCustomer.orders?.length
      ? selectedCustomer.orders
          .map(
            (order) => `
              <tr>
                <td>${safeValue(order.orderId)}</td>
                <td>${safeValue(order.orderDate)}</td>
                <td>₹${safeValue(order.amount)}</td>
                <td>₹${safeValue(order.discountAmount)}</td>
                <td>₹${safeValue(order.netAmount)}</td>
                <td>${safeValue(order.productCategory)}</td>
                <td>${safeValue(order.orderStatus)}</td>
                <td>${safeValue(order.paymentMode)}</td>
              </tr>
            `
          )
          .join("")
      : `<tr><td colspan="8">No orders found</td></tr>`;

    const warningRows = selectedCustomer.warnings?.length
      ? selectedCustomer.warnings.map((warning) => `<li>${warning}</li>`).join("")
      : "<li>No warnings</li>";

    const printWindow = window.open("", "_blank");

    printWindow.document.write(`
      <html>
        <head>
          <title>${selectedCustomer.customerId} Customer Details</title>
          <style>
            body {
              font-family: Arial, sans-serif;
              padding: 24px;
              color: #111827;
            }
            h1, h2 {
              color: #1e3a8a;
            }
            table {
              width: 100%;
              border-collapse: collapse;
              margin-bottom: 24px;
            }
            th, td {
              border: 1px solid #d1d5db;
              padding: 8px;
              text-align: left;
              font-size: 13px;
            }
            th {
              background: #eef2ff;
            }
            .grid {
              display: grid;
              grid-template-columns: repeat(2, 1fr);
              gap: 8px 24px;
              margin-bottom: 24px;
            }
            .label {
              font-weight: bold;
              color: #374151;
            }
            .summary {
              border: 1px solid #c7d2fe;
              background: #eef2ff;
              padding: 16px;
              border-radius: 8px;
            }
          </style>
        </head>
        <body>
          <h1>Customer360 - Customer Detail Report</h1>

          <h2>Customer Profile</h2>
          <div class="grid">
            <div><span class="label">Customer ID:</span> ${safeValue(selectedCustomer.customerId)}</div>
            <div><span class="label">Name:</span> ${safeValue(selectedCustomer.name)}</div>
            <div><span class="label">Email:</span> ${safeValue(selectedCustomer.email)}</div>
            <div><span class="label">Mobile:</span> ${safeValue(selectedCustomer.mobile)}</div>
            <div><span class="label">City:</span> ${safeValue(selectedCustomer.city)}</div>
            <div><span class="label">Gender:</span> ${safeValue(selectedCustomer.gender)}</div>
            <div><span class="label">Age:</span> ${safeValue(selectedCustomer.age)}</div>
            <div><span class="label">Date of Birth:</span> ${safeValue(selectedCustomer.dateOfBirth)}</div>
            <div><span class="label">Address:</span> ${safeValue(selectedCustomer.address)}</div>
            <div><span class="label">Customer Type:</span> ${safeValue(selectedCustomer.customerType)}</div>
          </div>

          <h2>Preferences</h2>
          <div class="grid">
            <div><span class="label">Membership:</span> ${safeValue(selectedCustomer.membership)}</div>
            <div><span class="label">Preferred Channel:</span> ${safeValue(selectedCustomer.preferredChannel)}</div>
            <div><span class="label">Preferred Language:</span> ${safeValue(selectedCustomer.preferredLanguage)}</div>
            <div><span class="label">Notification Opt-In:</span> ${formatBoolean(selectedCustomer.notificationOptIn)}</div>
            <div><span class="label">Marketing Consent:</span> ${formatBoolean(selectedCustomer.marketingConsent)}</div>
            <div><span class="label">Preferred Contact Time:</span> ${safeValue(selectedCustomer.preferredContactTime)}</div>
          </div>

          <h2>Business Metrics</h2>
          <div class="grid">
            <div><span class="label">Total Orders:</span> ${safeValue(selectedCustomer.totalOrders)}</div>
            <div><span class="label">Total Order Amount:</span> ₹${safeValue(selectedCustomer.totalOrderAmount)}</div>
            <div><span class="label">Total Discount:</span> ₹${safeValue(selectedCustomer.totalDiscountAmount)}</div>
            <div><span class="label">Net Amount:</span> ₹${safeValue(selectedCustomer.netOrderAmount)}</div>
            <div><span class="label">Reward Points:</span> ${safeValue(selectedCustomer.rewardPoints)}</div>
            <div><span class="label">Customer Segment:</span> ${safeValue(selectedCustomer.customerSegment)}</div>
            <div><span class="label">Data Quality Score:</span> ${safeValue(selectedCustomer.dataQualityScore)}</div>
            <div><span class="label">Data Quality Status:</span> ${safeValue(selectedCustomer.dataQualityStatus)}</div>
          </div>

          <h2>Orders</h2>
          <table>
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Date</th>
                <th>Amount</th>
                <th>Discount</th>
                <th>Net Amount</th>
                <th>Category</th>
                <th>Status</th>
                <th>Payment</th>
              </tr>
            </thead>
            <tbody>
              ${orderRows}
            </tbody>
          </table>

          <h2>Warnings</h2>
          <ul>${warningRows}</ul>

          <h2>Customer Summary</h2>
          <div class="summary">${summary || "Summary not generated"}</div>
        </body>
      </html>
    `);

    printWindow.document.close();
    printWindow.print();
  };

  return (
    <div className="page-shell">
      <header className="hero-card">
        <div>
          <p className="eyebrow">Customer Data Integration</p>
          <h1>Customer360</h1>
          <p className="subtitle">
            Unified customer profile from MongoDB, CSV, JSON, and AI-powered summary.
          </p>
        </div>

        <div className="export-actions">
          <button onClick={() => downloadExport("csv")}>Export CSV</button>
          <button onClick={() => downloadExport("excel")}>Export Excel</button>
          <button onClick={() => downloadExport("pdf")}>Export PDF</button>
        </div>
      </header>

      <section className="filter-card">
        <div className="filter-grid">
          <input
            name="search"
            value={filters.search}
            onChange={handleInputChange}
            placeholder="Search customer, city, segment..."
          />

          <input
            name="city"
            value={filters.city}
            onChange={handleInputChange}
            placeholder="City"
          />

          <input
            name="membership"
            value={filters.membership}
            onChange={handleInputChange}
            placeholder="Membership"
          />

          <input
            name="preferredChannel"
            value={filters.preferredChannel}
            onChange={handleInputChange}
            placeholder="Preferred Channel"
          />

          <select name="sortBy" value={filters.sortBy} onChange={handleInputChange}>
            <option value="customerId">Customer ID</option>
            <option value="name">Name</option>
            <option value="city">City</option>
            <option value="gender">Gender</option>
            <option value="customerType">Customer Type</option>
            <option value="membership">Membership</option>
            <option value="preferredChannel">Preferred Channel</option>
            <option value="totalOrders">Total Orders</option>
            <option value="totalOrderAmount">Total Amount</option>
            <option value="netOrderAmount">Net Amount</option>
            <option value="rewardPoints">Reward Points</option>
            <option value="dataQualityScore">Data Quality Score</option>
          </select>

          <select name="sortDir" value={filters.sortDir} onChange={handleInputChange}>
            <option value="asc">Ascending</option>
            <option value="desc">Descending</option>
          </select>

          <button onClick={handleApply}>Apply</button>
          <button className="secondary-button" onClick={handleReset}>
            Reset
          </button>
        </div>
      </section>

      {error && <div className="error-box">{error}</div>}

      <main className="main-grid">
        <section className="table-card">
          <div className="section-title-row">
            <h2>Customers</h2>
            {loading && <span>Loading...</span>}
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>City</th>
                  <th>Type</th>
                  <th>Membership</th>
                  <th>Segment</th>
                  <th>Reward</th>
                  <th>Net Amount</th>
                  <th>Quality</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {customers.length === 0 && !loading && (
                  <tr>
                    <td colSpan="10" className="empty-cell">
                      No customers found
                    </td>
                  </tr>
                )}

                {customers.map((customer) => (
                  <tr key={customer.customerId}>
                    <td>{customer.customerId}</td>
                    <td>{customer.name}</td>
                    <td>{customer.city}</td>
                    <td>{customer.customerType}</td>
                    <td>{customer.membership}</td>
                    <td>
                      <span className="pill">{customer.customerSegment}</span>
                    </td>
                    <td>{customer.rewardPoints}</td>
                    <td>{formatCurrency(customer.netOrderAmount)}</td>
                    <td>
                      <span className="quality-pill">
                        {customer.dataQualityScore} - {customer.dataQualityStatus}
                      </span>
                    </td>
                    <td>
                      <button
                        className="small-button"
                        onClick={() => handleViewDetails(customer.customerId)}
                      >
                        View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="detail-card">
          <h2>Customer Details</h2>

          {detailLoading && <p>Loading customer details...</p>}

          {!selectedCustomer && !detailLoading && (
            <p className="muted-text">Select a customer to view full details.</p>
          )}

          {selectedCustomer && (
            <>
              <div className="detail-actions">
                <button onClick={exportSelectedCustomerCsv}>Export Detail CSV</button>
                <button onClick={exportSelectedCustomerPdf}>Export Detail PDF</button>
              </div>

              <h3>Profile</h3>
              <div className="detail-grid">
                <DetailItem label="ID" value={selectedCustomer.customerId} />
                <DetailItem label="Name" value={selectedCustomer.name} />
                <DetailItem label="Email" value={selectedCustomer.email} />
                <DetailItem label="Mobile" value={selectedCustomer.mobile} />
                <DetailItem label="City" value={selectedCustomer.city} />
                <DetailItem label="Gender" value={selectedCustomer.gender} />
                <DetailItem label="Age" value={selectedCustomer.age} />
                <DetailItem label="Date of Birth" value={selectedCustomer.dateOfBirth} />
                <DetailItem label="Address" value={selectedCustomer.address} />
                <DetailItem label="Customer Type" value={selectedCustomer.customerType} />
              </div>

              <h3>Preferences</h3>
              <div className="detail-grid">
                <DetailItem label="Membership" value={selectedCustomer.membership} />
                <DetailItem label="Channel" value={selectedCustomer.preferredChannel} />
                <DetailItem label="Language" value={selectedCustomer.preferredLanguage} />
                <DetailItem
                  label="Notification Opt-In"
                  value={formatBoolean(selectedCustomer.notificationOptIn)}
                />
                <DetailItem
                  label="Marketing Consent"
                  value={formatBoolean(selectedCustomer.marketingConsent)}
                />
                <DetailItem
                  label="Contact Time"
                  value={selectedCustomer.preferredContactTime}
                />
              </div>

              <h3>Business Metrics</h3>
              <div className="metric-grid">
                <MetricCard label="Total Orders" value={selectedCustomer.totalOrders} />
                <MetricCard
                  label="Total Amount"
                  value={formatCurrency(selectedCustomer.totalOrderAmount)}
                />
                <MetricCard
                  label="Total Discount"
                  value={formatCurrency(selectedCustomer.totalDiscountAmount)}
                />
                <MetricCard
                  label="Net Amount"
                  value={formatCurrency(selectedCustomer.netOrderAmount)}
                />
                <MetricCard label="Reward Points" value={selectedCustomer.rewardPoints} />
                <MetricCard label="Segment" value={selectedCustomer.customerSegment} />
                <MetricCard
                  label="Quality Score"
                  value={`${selectedCustomer.dataQualityScore}`}
                />
                <MetricCard
                  label="Quality Status"
                  value={selectedCustomer.dataQualityStatus}
                />
              </div>

              <h3>Orders</h3>
              <div className="table-wrap small-table">
                <table>
                  <thead>
                    <tr>
                      <th>Order ID</th>
                      <th>Date</th>
                      <th>Amount</th>
                      <th>Discount</th>
                      <th>Net</th>
                      <th>Category</th>
                      <th>Status</th>
                      <th>Payment</th>
                    </tr>
                  </thead>

                  <tbody>
                    {selectedCustomer.orders?.map((order) => (
                      <tr key={order.orderId}>
                        <td>{order.orderId}</td>
                        <td>{order.orderDate}</td>
                        <td>{formatCurrency(order.amount)}</td>
                        <td>{formatCurrency(order.discountAmount)}</td>
                        <td>{formatCurrency(order.netAmount)}</td>
                        <td>{order.productCategory}</td>
                        <td>{order.orderStatus}</td>
                        <td>{order.paymentMode}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <h3>Warnings</h3>
              {selectedCustomer.warnings?.length > 0 ? (
                <ul className="warning-list">
                  {selectedCustomer.warnings.map((warning) => (
                    <li key={warning}>{warning}</li>
                  ))}
                </ul>
              ) : (
                <p className="success-text">No data quality warnings.</p>
              )}

              <button
                className="summary-button"
                onClick={handleGenerateSummary}
                disabled={summaryLoading}
              >
                {summaryLoading ? "Generating..." : "Generate AI Summary"}
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

function DetailItem({ label, value }) {
  return (
    <div className="detail-item">
      <span>{label}</span>
      <strong>{value ?? "Not Available"}</strong>
    </div>
  );
}

function MetricCard({ label, value }) {
  return (
    <div className="metric-card">
      <span>{label}</span>
      <strong>{value ?? "Not Available"}</strong>
    </div>
  );
}

export default CustomerListPage;
