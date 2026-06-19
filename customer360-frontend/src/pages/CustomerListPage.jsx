import { useEffect, useMemo, useState } from "react";
import {
  downloadExport,
  getCustomerDetails,
  getCustomersPage,
  getCustomerSummary,
} from "../api/customerApi";

function CustomerListPage() {
  const [customers, setCustomers] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [summary, setSummary] = useState("");
  const [loading, setLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [summaryLoading, setSummaryLoading] = useState(false);
  const [error, setError] = useState("");

  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 1,
    first: true,
    last: true,
    hasNext: false,
    hasPrevious: false,
  });

  const [filters, setFilters] = useState({
    search: "",
    city: "",
    membership: "",
    preferredChannel: "",
    sortBy: "customerId",
    sortDir: "asc",
    rowsPerPage: 10,
  });

  useEffect(() => {
    loadCustomers(0);
  }, [
    filters.search,
    filters.city,
    filters.membership,
    filters.preferredChannel,
    filters.sortBy,
    filters.sortDir,
    filters.rowsPerPage,
  ]);

  const loadCustomers = async (pageNumber = 0) => {
    try {
      setLoading(true);
      setError("");

      const data = await getCustomersPage({
        search: filters.search || undefined,
        city: filters.city || undefined,
        membership: filters.membership || undefined,
        preferredChannel: filters.preferredChannel || undefined,
        sortBy: filters.sortBy,
        sortDir: filters.sortDir,
        page: pageNumber,
        size: filters.rowsPerPage,
      });

      setCustomers(data.content || []);

      setPagination({
        page: data.page ?? 0,
        size: data.size ?? Number(filters.rowsPerPage),
        totalElements: data.totalElements ?? 0,
        totalPages: data.totalPages === 0 ? 1 : data.totalPages,
        first: data.first ?? true,
        last: data.last ?? true,
        hasNext: data.hasNext ?? false,
        hasPrevious: data.hasPrevious ?? false,
      });
    } catch (err) {
      console.error(err);
      setError("Unable to load customers. Please check whether backend is running.");
    } finally {
      setLoading(false);
    }
  };

  const dashboardStats = useMemo(() => {
    const totalCustomers = pagination.totalElements;

    const totalOrders = customers.reduce(
      (sum, customer) => sum + Number(customer.totalOrders || 0),
      0
    );

    const totalOrderAmount = customers.reduce(
      (sum, customer) => sum + Number(customer.totalOrderAmount || 0),
      0
    );

    const averageOrderAmount =
      totalOrders > 0 ? totalOrderAmount / totalOrders : 0;

    const activeMembershipCustomers = customers.filter(
      (customer) => formatMembership(customer.membership) !== "No Membership"
    ).length;

    const customersWithoutOrders = customers.filter(
      (customer) => Number(customer.totalOrders || 0) === 0
    ).length;

    return {
      totalCustomers,
      totalOrders,
      totalOrderAmount,
      averageOrderAmount,
      activeMembershipCustomers,
      customersWithoutOrders,
    };
  }, [customers, pagination.totalElements]);

  const handleFilterChange = (event) => {
    const { name, value } = event.target;

    setFilters((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const clearFilters = () => {
    setFilters({
      search: "",
      city: "",
      membership: "",
      preferredChannel: "",
      sortBy: "customerId",
      sortDir: "asc",
      rowsPerPage: 10,
    });

    setSummary("");
    setSelectedCustomer(null);
  };

  const handleViewCustomer = async (customerId) => {
    try {
      setDetailLoading(true);
      setError("");
      setSummary("");

      const data = await getCustomerDetails(customerId);
      setSelectedCustomer(data);

      setTimeout(() => {
        document.getElementById("customer-profile-section")?.scrollIntoView({
          behavior: "smooth",
          block: "start",
        });
      }, 100);
    } catch (err) {
      console.error(err);
      setError("Unable to load customer details.");
    } finally {
      setDetailLoading(false);
    }
  };

  const handleGenerateSummary = async (customerId) => {
    try {
      setSummaryLoading(true);
      setError("");

      let customerData = selectedCustomer;

      if (!customerData || customerData.customerId !== customerId) {
        customerData = await getCustomerDetails(customerId);
        setSelectedCustomer(customerData);
      }

      const data = await getCustomerSummary(customerId);
      setSummary(data.summary || "");

      setTimeout(() => {
        document.getElementById("ai-summary-card")?.scrollIntoView({
          behavior: "smooth",
          block: "center",
        });
      }, 100);
    } catch (err) {
      console.error(err);
      setError("Unable to generate customer summary.");
    } finally {
      setSummaryLoading(false);
    }
  };

  const exportCurrentPageJson = () => {
    const blob = new Blob([JSON.stringify(customers, null, 2)], {
      type: "application/json",
    });

    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");

    link.href = url;
    link.download = "customer360-current-page-customers.json";
    link.click();

    URL.revokeObjectURL(url);
  };

  const exportCustomerCsv = async (customerId) => {
    try {
      const customer = await getCustomerDetails(customerId);

      let summaryForExport = "";

      if (selectedCustomer?.customerId === customerId && summary) {
        summaryForExport = summary;
      } else {
        try {
          const summaryResponse = await getCustomerSummary(customerId);
          summaryForExport = summaryResponse?.summary || "AI summary not generated.";
        } catch (summaryError) {
          console.error(summaryError);
          summaryForExport = "AI summary not available.";
        }
      }

      const rows = [
        ["Section", "Field", "Value"],
        ["Profile", "Customer ID", customer.customerId],
        ["Profile", "Name", customer.name],
        ["Profile", "City", customer.city],
        ["Profile", "Gender", customer.gender],
        ["Profile", "Age", customer.age],
        ["Profile", "Date of Birth", customer.dateOfBirth],
        ["Profile", "Address", customer.address],
        ["Profile", "Customer Type", customer.customerType],

        ["Preference", "Membership", formatMembership(customer.membership)],
        ["Preference", "Preferred Channel", formatPreferredChannel(customer.preferredChannel)],
        ["Preference", "Preferred Language", customer.preferredLanguage],
        ["Preference", "Notification Opt-In", formatBoolean(customer.notificationOptIn)],
        ["Preference", "Marketing Consent", formatBoolean(customer.marketingConsent)],
        ["Preference", "Preferred Contact Time", customer.preferredContactTime],

        ["Business", "Total Orders", customer.totalOrders],
        ["Business", "Total Order Amount", customer.totalOrderAmount],
        ["Business", "Total Discount", customer.totalDiscountAmount],
        ["Business", "Net Order Amount", customer.netOrderAmount],
        ["Business", "Reward Points", customer.rewardPoints],
        ["Business", "Customer Segment", customer.customerSegment],
        ["Business", "Data Quality Score", customer.dataQualityScore],
        ["Business", "Data Quality Status", customer.dataQualityStatus],
      ];

      rows.push([]);
      rows.push(["AI Summary", "Summary", summaryForExport]);

      rows.push([]);
      rows.push([
        "Orders",
        "Order ID",
        "Order Date",
        "Amount",
        "Discount",
        "Net Amount",
        "Category",
        "Status",
        "Payment",
      ]);

      customer.orders?.forEach((order) => {
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

      if (customer.warnings?.length > 0) {
        customer.warnings.forEach((warning) => {
          rows.push(["Warnings", warning, ""]);
        });
      } else {
        rows.push(["Warnings", "No warnings", ""]);
      }

      const csvContent = rows
        .map((row) => row.map(escapeCsv).join(","))
        .join("\n");

      const blob = new Blob([csvContent], {
        type: "text/csv;charset=utf-8;",
      });

      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");

      link.href = url;
      link.download = `${customer.customerId}_customer_profile.csv`;
      link.click();

      URL.revokeObjectURL(url);
    } catch (err) {
      console.error(err);
      setError("Unable to export selected customer.");
    }
  };

  const exportSelectedCustomerPdf = () => {
    if (!selectedCustomer) {
      return;
    }

    const printWindow = window.open("", "_blank");

    const orderRows =
      selectedCustomer.orders?.length > 0
        ? selectedCustomer.orders
            .map(
              (order) => `
              <tr>
                <td>${safe(order.orderId)}</td>
                <td>${safe(order.orderDate)}</td>
                <td>${formatCurrency(order.amount)}</td>
                <td>${formatCurrency(order.discountAmount)}</td>
                <td>${formatCurrency(order.netAmount)}</td>
                <td>${safe(order.productCategory)}</td>
                <td>${safe(order.orderStatus)}</td>
                <td>${safe(order.paymentMode)}</td>
              </tr>
            `
            )
            .join("")
        : `<tr><td colspan="8">No orders found</td></tr>`;

    printWindow.document.write(`
      <html>
        <head>
          <title>${safe(selectedCustomer.customerId)} Profile</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 24px; color: #111827; }
            h1, h2 { color: #1e3a8a; }
            table { width: 100%; border-collapse: collapse; margin-top: 12px; }
            th, td { border: 1px solid #d1d5db; padding: 8px; font-size: 12px; text-align: left; }
            th { background: #eef2ff; }
            .grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px 20px; }
            .label { font-weight: bold; }
          </style>
        </head>
        <body>
          <h1>Customer Profile Report</h1>

          <h2>${safe(selectedCustomer.name)} - ${safe(selectedCustomer.customerId)}</h2>

          <div class="grid">
            <div><span class="label">City:</span> ${safe(selectedCustomer.city)}</div>
            <div><span class="label">Customer Type:</span> ${safe(selectedCustomer.customerType)}</div>
            <div><span class="label">Membership:</span> ${formatMembership(selectedCustomer.membership)}</div>
            <div><span class="label">Preferred Channel:</span> ${formatPreferredChannel(selectedCustomer.preferredChannel)}</div>
            <div><span class="label">Total Orders:</span> ${safe(selectedCustomer.totalOrders)}</div>
            <div><span class="label">Total Amount:</span> ${formatCurrency(selectedCustomer.totalOrderAmount)}</div>
            <div><span class="label">Net Amount:</span> ${formatCurrency(selectedCustomer.netOrderAmount)}</div>
            <div><span class="label">Reward Points:</span> ${safe(selectedCustomer.rewardPoints)}</div>
            <div><span class="label">Segment:</span> ${safe(selectedCustomer.customerSegment)}</div>
            <div><span class="label">Data Quality:</span> ${safe(selectedCustomer.dataQualityScore)} - ${safe(selectedCustomer.dataQualityStatus)}</div>
          </div>

          <h2>Recent Orders</h2>
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
            <tbody>${orderRows}</tbody>
          </table>

          <h2>AI Summary</h2>
          <p>${summary || "Summary not generated."}</p>
        </body>
      </html>
    `);

    printWindow.document.close();
    printWindow.print();
  };

  return (
    <div className="dashboard-page">
      <header className="dashboard-header">
        <div>
          <h1>CUSTOMER LIST REPORT</h1>
          <p>Search, filter, sort, paginate and export consolidated customer data.</p>
        </div>

        <div className="header-search">
          <input
            name="search"
            value={filters.search}
            onChange={handleFilterChange}
            placeholder="Search by Customer ID, Name, Email, Mobile, City..."
          />
          <span>☷</span>
        </div>

        <div className="admin-box">
          <span className="bell">🔔</span>
          <span className="avatar">AD</span>
          <span>Admin User</span>
        </div>
      </header>

      <section className="stat-grid">
        <StatCard
          icon="👥"
          label="Total Customers"
          value={dashboardStats.totalCustomers}
          hint="Backend filtered records"
        />
        <StatCard
          icon="🛒"
          label="Page Orders"
          value={dashboardStats.totalOrders}
          hint="Orders in current page"
        />
        <StatCard
          icon="₹"
          label="Page Order Amount"
          value={formatCurrency(dashboardStats.totalOrderAmount)}
          hint="Current page total"
        />
        <StatCard
          icon="↗"
          label="Avg. Order Amount"
          value={formatCurrency(dashboardStats.averageOrderAmount)}
          hint="Current page average"
        />
        <StatCard
          icon="♛"
          label="Membership Customers"
          value={dashboardStats.activeMembershipCustomers}
          hint="Current page count"
        />
        <StatCard
          icon="⚠"
          label="Customers Without Orders"
          value={dashboardStats.customersWithoutOrders}
          hint="Current page count"
        />
      </section>

      <section className="dashboard-card filter-panel">
        <div className="panel-title-row">
          <h2>☷ Filters</h2>
          <span>{countAppliedFilters(filters)} Applied</span>
        </div>

        <div className="advanced-filter-grid">
          <FilterField label="City">
            <input
              name="city"
              value={filters.city}
              onChange={handleFilterChange}
              placeholder="Hyderabad"
            />
          </FilterField>

          <FilterField label="Membership">
            <select
              name="membership"
              value={filters.membership}
              onChange={handleFilterChange}
            >
              <option value="">All Memberships</option>
              <option value="Gold">Gold</option>
              <option value="Silver">Silver</option>
              <option value="Platinum">Platinum</option>
              <option value="Not Available">No Membership</option>
            </select>
          </FilterField>

          <FilterField label="Preferred Channel">
            <select
              name="preferredChannel"
              value={filters.preferredChannel}
              onChange={handleFilterChange}
            >
              <option value="">All Channels</option>
              <option value="Email">Email</option>
              <option value="SMS">SMS</option>
              <option value="WhatsApp">WhatsApp</option>
              <option value="Phone">Phone</option>
              <option value="Not Available">Not Available</option>
            </select>
          </FilterField>

          <FilterField label="Sort By">
            <select
              name="sortBy"
              value={filters.sortBy}
              onChange={handleFilterChange}
            >
              <option value="customerId">Customer ID</option>
              <option value="name">Name</option>
              <option value="city">City</option>
              <option value="membership">Membership</option>
              <option value="preferredChannel">Preferred Channel</option>
              <option value="totalOrders">Total Orders</option>
              <option value="totalOrderAmount">Total Order Amount</option>
              <option value="netOrderAmount">Net Order Amount</option>
              <option value="rewardPoints">Reward Points</option>
              <option value="customerSegment">Customer Segment</option>
              <option value="dataQualityScore">Data Quality Score</option>
            </select>
          </FilterField>

          <FilterField label="Direction">
            <select
              name="sortDir"
              value={filters.sortDir}
              onChange={handleFilterChange}
            >
              <option value="asc">Asc</option>
              <option value="desc">Desc</option>
            </select>
          </FilterField>

          <FilterField label="Rows">
            <select
              name="rowsPerPage"
              value={filters.rowsPerPage}
              onChange={handleFilterChange}
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={15}>15</option>
              <option value={20}>20</option>
            </select>
          </FilterField>

          <button onClick={() => loadCustomers(0)}>Apply Filters</button>
          <button className="light-button" onClick={clearFilters}>
            Clear Filters
          </button>
        </div>
      </section>

      {error && <div className="error-box">{error}</div>}

      <section className="dashboard-card list-panel">
        <div className="panel-title-row sort-row">
          <div className="sort-controls">
            <h2>☷ Backend Pagination</h2>
            <span>
              Page {pagination.page + 1} of {pagination.totalPages}
            </span>
          </div>

          <div className="export-buttons">
            <button className="success-button" onClick={() => downloadExport("csv")}>
              ⇩ Export CSV
            </button>
            <button className="purple-button" onClick={exportCurrentPageJson}>
              ⇩ Export JSON
            </button>
            <button onClick={() => downloadExport("excel")}>Export Excel</button>
            <button onClick={() => downloadExport("pdf")}>Export PDF</button>
          </div>
        </div>

        <div className="customer-table-wrap">
          <table className="customer-table">
            <thead>
              <tr>
                <th>Customer ID</th>
                <th>Name</th>
                <th>City</th>
                <th>Membership</th>
                <th>Preferred Channel</th>
                <th>Total Orders</th>
                <th>Total Order Amount</th>
                <th>Net Amount</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {loading && (
                <tr>
                  <td colSpan="10" className="empty-cell">
                    Loading customers...
                  </td>
                </tr>
              )}

              {!loading && customers.length === 0 && (
                <tr>
                  <td colSpan="10" className="empty-cell">
                    No customers found.
                  </td>
                </tr>
              )}

              {!loading &&
                customers.map((customer) => (
                  <tr
                    key={customer.customerId}
                    onClick={() => handleViewCustomer(customer.customerId)}
                    className={
                      selectedCustomer?.customerId === customer.customerId
                        ? "active-row"
                        : ""
                    }
                  >
                    <td>{customer.customerId}</td>
                    <td>{customer.name}</td>
                    <td>{customer.city}</td>

                    <td>
                      <span className={getMembershipClass(customer.membership)}>
                        {formatMembershipDisplay(customer.membership)}
                      </span>
                    </td>

                    <td>
                      <span className={getPreferredChannelClass(customer.preferredChannel)}>
                        {formatPreferredChannel(customer.preferredChannel)}
                      </span>
                    </td>

                    <td>{customer.totalOrders}</td>
                    <td>{formatCurrency(customer.totalOrderAmount)}</td>
                    <td>{formatCurrency(customer.netOrderAmount)}</td>

                    <td>
                      <span
                        className={
                          customer.warnings?.length > 0
                            ? "status-warning"
                            : "status-complete"
                        }
                      >
                        {customer.warnings?.length > 0 ? "Missing Data" : "Complete"}
                      </span>
                    </td>

                    <td>
                      <div className="row-actions">
                        <button
                          title="View Profile"
                          onClick={(event) => {
                            event.stopPropagation();
                            handleViewCustomer(customer.customerId);
                          }}
                        >
                          👁
                        </button>

                        <button
                          title="Generate AI Summary"
                          onClick={(event) => {
                            event.stopPropagation();
                            handleGenerateSummary(customer.customerId);
                          }}
                        >
                          ✦
                        </button>

                        <button
                          title="Download Customer CSV"
                          onClick={(event) => {
                            event.stopPropagation();
                            exportCustomerCsv(customer.customerId);
                          }}
                        >
                          ⇩
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>

        <div className="pagination-row">
          <span>
            Showing{" "}
            {pagination.totalElements === 0
              ? 0
              : pagination.page * pagination.size + 1}{" "}
            to{" "}
            {Math.min(
              (pagination.page + 1) * pagination.size,
              pagination.totalElements
            )}{" "}
            of {pagination.totalElements} records
          </span>

          <div className="pagination-buttons">
            <button disabled={pagination.first} onClick={() => loadCustomers(0)}>
              «
            </button>

            <button
              disabled={!pagination.hasPrevious}
              onClick={() => loadCustomers(Math.max(0, pagination.page - 1))}
            >
              ‹
            </button>

            {buildPageNumbers(pagination.page + 1, pagination.totalPages).map(
              (pageNumber) => (
                <button
                  key={pageNumber}
                  className={pageNumber === pagination.page + 1 ? "active-page" : ""}
                  onClick={() => loadCustomers(pageNumber - 1)}
                >
                  {pageNumber}
                </button>
              )
            )}

            <button
              disabled={!pagination.hasNext}
              onClick={() => loadCustomers(pagination.page + 1)}
            >
              ›
            </button>

            <button
              disabled={pagination.last}
              onClick={() => loadCustomers(pagination.totalPages - 1)}
            >
              »
            </button>
          </div>
        </div>
      </section>

      <section id="customer-profile-section" className="dashboard-card profile-panel">
        <div className="panel-title-row">
          <h2>Customer Profile</h2>

          {selectedCustomer && (
            <button
              className="close-button"
              onClick={() => {
                setSelectedCustomer(null);
                setSummary("");
              }}
            >
              ×
            </button>
          )}
        </div>

        {detailLoading && <p className="muted-text">Loading customer profile...</p>}

        {!selectedCustomer && !detailLoading && (
          <p className="muted-text">
            Click any customer row or the eye action button to open the full customer profile.
          </p>
        )}

        {selectedCustomer && (
          <>
            <div className="profile-header">
              <div className="profile-avatar">{getInitials(selectedCustomer.name)}</div>

              <div>
                <h3>{selectedCustomer.name}</h3>
                <div className="profile-tags">
                  <span className={getMembershipClass(selectedCustomer.membership)}>
                    {formatMembershipLabel(selectedCustomer.membership)}
                  </span>
                  <span>{safe(selectedCustomer.customerType)}</span>
                  <span>{safe(selectedCustomer.customerSegment)}</span>
                </div>
              </div>

              <div className="profile-actions">
                <button onClick={() => exportCustomerCsv(selectedCustomer.customerId)}>
                  Export Profile CSV
                </button>

                <button onClick={exportSelectedCustomerPdf}>Print / PDF</button>

                <button
                  className="star-action-button"
                  title="Generate AI Summary"
                  onClick={() => handleGenerateSummary(selectedCustomer.customerId)}
                >
                  {summaryLoading ? "..." : "✦"}
                </button>
              </div>
            </div>

            <div className="profile-grid">
              <div className="profile-info-card">
                <h4>Customer Information</h4>
                <InfoItem label="Customer ID" value={selectedCustomer.customerId} />
                <InfoItem label="Email" value={selectedCustomer.email} />
                <InfoItem label="Mobile" value={selectedCustomer.mobile} />
                <InfoItem label="City" value={selectedCustomer.city} />
                <InfoItem label="Gender" value={selectedCustomer.gender} />
                <InfoItem label="Age" value={selectedCustomer.age} />
                <InfoItem label="Date of Birth" value={selectedCustomer.dateOfBirth} />
                <InfoItem label="Address" value={selectedCustomer.address} />
              </div>

              <div className="profile-info-card">
                <h4>Order Summary</h4>
                <InfoItem label="Total Orders" value={selectedCustomer.totalOrders} />
                <InfoItem
                  label="Total Order Amount"
                  value={formatCurrency(selectedCustomer.totalOrderAmount)}
                />
                <InfoItem
                  label="Total Discount"
                  value={formatCurrency(selectedCustomer.totalDiscountAmount)}
                />
                <InfoItem
                  label="Net Order Amount"
                  value={formatCurrency(selectedCustomer.netOrderAmount)}
                />
                <InfoItem label="Reward Points" value={selectedCustomer.rewardPoints} />
                <InfoItem
                  label="Quality"
                  value={`${selectedCustomer.dataQualityScore} - ${selectedCustomer.dataQualityStatus}`}
                />
              </div>

              <div className="profile-info-card">
                <h4>Preferences</h4>
                <InfoItem
                  label="Membership"
                  value={formatMembership(selectedCustomer.membership)}
                />
                <InfoItem
                  label="Preferred Channel"
                  value={formatPreferredChannel(selectedCustomer.preferredChannel)}
                />
                <InfoItem
                  label="Preferred Language"
                  value={selectedCustomer.preferredLanguage}
                />
                <InfoItem
                  label="Notification Opt-In"
                  value={formatBoolean(selectedCustomer.notificationOptIn)}
                />
                <InfoItem
                  label="Marketing Consent"
                  value={formatBoolean(selectedCustomer.marketingConsent)}
                />
                <InfoItem
                  label="Preferred Contact Time"
                  value={selectedCustomer.preferredContactTime}
                />
              </div>
            </div>

            <div className="compact-metrics">
              <Metric label="Total Orders" value={selectedCustomer.totalOrders} />
              <Metric
                label="Total Amount"
                value={formatCurrency(selectedCustomer.totalOrderAmount)}
              />
              <Metric
                label="Discount"
                value={formatCurrency(selectedCustomer.totalDiscountAmount)}
              />
              <Metric
                label="Net Amount"
                value={formatCurrency(selectedCustomer.netOrderAmount)}
              />
              <Metric label="Reward" value={selectedCustomer.rewardPoints} />
              <Metric label="Segment" value={selectedCustomer.customerSegment} />
              <Metric label="Quality Score" value={selectedCustomer.dataQualityScore} />
              <Metric label="Quality Status" value={selectedCustomer.dataQualityStatus} />
            </div>

            <div className="recent-orders-card">
              <div className="panel-title-row">
                <h3>Recent Orders</h3>
              </div>

              <div className="customer-table-wrap">
                <table className="customer-table compact-order-table">
                  <thead>
                    <tr>
                      <th>Order ID</th>
                      <th>Order Date</th>
                      <th>Amount</th>
                      <th>Discount</th>
                      <th>Net Amount</th>
                      <th>Category</th>
                      <th>Status</th>
                      <th>Payment</th>
                    </tr>
                  </thead>

                  <tbody>
                    {selectedCustomer.orders?.length > 0 ? (
                      selectedCustomer.orders.map((order) => (
                        <tr key={order.orderId}>
                          <td>{order.orderId}</td>
                          <td>{order.orderDate}</td>
                          <td>{formatCurrency(order.amount)}</td>
                          <td>{formatCurrency(order.discountAmount)}</td>
                          <td>{formatCurrency(order.netAmount)}</td>
                          <td>{order.productCategory}</td>
                          <td>
                            <span className="status-complete">{order.orderStatus}</span>
                          </td>
                          <td>{order.paymentMode}</td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan="8" className="empty-cell">
                          No orders available.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>

            {(summaryLoading || summary) && (
              <div id="ai-summary-card" className="ai-summary-card">
                <h3>✦ AI-Powered Summary</h3>

                {summaryLoading ? (
                  <p className="muted-text">Generating AI summary...</p>
                ) : (
                  <p>{summary}</p>
                )}
              </div>
            )}
          </>
        )}
      </section>

      <footer className="dashboard-footer">
        <span>Note: Email and mobile are masked for data privacy and security.</span>
        <span>Last Data Refresh: {new Date().toLocaleString()}</span>
      </footer>
    </div>
  );
}

function StatCard({ icon, label, value, hint }) {
  return (
    <div className="stat-card">
      <div className="stat-icon">{icon}</div>
      <div>
        <span>{label}</span>
        <strong>{value}</strong>
        <p>{hint}</p>
      </div>
    </div>
  );
}

function FilterField({ label, children }) {
  return (
    <label className="filter-field">
      <span>{label}</span>
      {children}
    </label>
  );
}

function InfoItem({ label, value }) {
  return (
    <div className="info-item">
      <span>{label}</span>
      <strong>{value ?? "Not Available"}</strong>
    </div>
  );
}

function Metric({ label, value }) {
  return (
    <div className="metric-box">
      <span>{label}</span>
      <strong>{value ?? "Not Available"}</strong>
    </div>
  );
}

function buildPageNumbers(currentPage, totalPages) {
  const pages = [];
  const safeTotalPages = Math.max(1, totalPages || 1);
  const start = Math.max(1, currentPage - 2);
  const end = Math.min(safeTotalPages, currentPage + 2);

  for (let page = start; page <= end; page++) {
    pages.push(page);
  }

  return pages;
}

function countAppliedFilters(filters) {
  let count = 0;

  if (filters.search) count++;
  if (filters.city) count++;
  if (filters.membership) count++;
  if (filters.preferredChannel) count++;

  return count;
}

function formatCurrency(value) {
  const amount = Number(value || 0);

  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(amount);
}

function formatBoolean(value) {
  return value ? "Yes" : "No";
}

function safe(value) {
  if (value === null || value === undefined || value === "") {
    return "Not Available";
  }

  return String(value);
}

function escapeCsv(value) {
  if (value === null || value === undefined) {
    return "";
  }

  const text = String(value).replaceAll('"', '""');
  return `"${text}"`;
}

function getInitials(name) {
  if (!name) {
    return "CU";
  }

  return name
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();
}

function formatMembership(value) {
  const membership = safe(value);

  if (
    membership === "Not Available" ||
    membership.toLowerCase() === "not available" ||
    membership.toLowerCase() === "na" ||
    membership.toLowerCase() === "none"
  ) {
    return "No Membership";
  }

  return membership;
}

function formatMembershipDisplay(value) {
  const membership = formatMembership(value);

  if (membership === "No Membership") {
    return "No Membership";
  }

  return `♛ ${membership}`;
}

function formatMembershipLabel(value) {
  const membership = formatMembership(value);

  if (membership === "No Membership") {
    return "No Membership";
  }

  return `♛ ${membership} Member`;
}

function getMembershipClass(value) {
  const membership = formatMembership(value).toLowerCase();

  if (membership.includes("gold")) {
    return "membership-pill membership-gold";
  }

  if (membership.includes("silver")) {
    return "membership-pill membership-silver";
  }

  if (membership.includes("platinum") || membership.includes("plat")) {
    return "membership-pill membership-platinum";
  }

  return "membership-pill membership-none";
}

function formatPreferredChannel(value) {
  const channel = safe(value);

  if (
    channel === "Not Available" ||
    channel.toLowerCase() === "not available" ||
    channel.toLowerCase() === "na" ||
    channel.toLowerCase() === "none"
  ) {
    return "Not Available";
  }

  return channel;
}

function getPreferredChannelClass(value) {
  const channel = formatPreferredChannel(value).toLowerCase();

  if (channel === "not available") {
    return "channel-pill channel-missing";
  }

  if (channel.includes("whatsapp")) {
    return "channel-pill channel-whatsapp";
  }

  if (channel.includes("email")) {
    return "channel-pill channel-email";
  }

  if (channel.includes("sms")) {
    return "channel-pill channel-sms";
  }

  return "channel-pill channel-default";
}

export default CustomerListPage;
