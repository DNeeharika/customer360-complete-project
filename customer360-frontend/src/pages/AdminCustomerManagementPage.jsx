import { useEffect, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import {
  createCustomerProfile,
  deleteCustomerProfile,
  updateCustomerProfile,
} from "../api/adminCustomerApi";
import { getCustomersPage } from "../api/customerApi";
import { useAuth } from "../context/AuthContext";

const emptyForm = {
  customerId: "",
  name: "",
  email: "",
  mobile: "",
  city: "",
  gender: "",
  age: "",
  dateOfBirth: "",
  address: "",
  customerType: "",
};

function AdminCustomerManagementPage() {
  const navigate = useNavigate();
  const { user, hasRole, logout } = useAuth();

  const [customers, setCustomers] = useState([]);
  const [loadingCustomers, setLoadingCustomers] = useState(false);

  const [formData, setFormData] = useState(emptyForm);
  const [mode, setMode] = useState("create");
  const [editingCustomerId, setEditingCustomerId] = useState("");

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  const [deletingCustomerId, setDeletingCustomerId] = useState("");

  const isAdmin = hasRole("ADMIN");

  useEffect(() => {
    if (isAdmin) {
      loadCustomers();
    }
  }, [isAdmin]);

  if (!isAdmin) {
    return <Navigate to="/customers" replace />;
  }

  const loadCustomers = async () => {
    try {
      setLoadingCustomers(true);
      setError("");

      const data = await getCustomersPage({
        page: 0,
        size: 100,
        sortBy: "customerId",
        sortDir: "asc",
      });

      setCustomers(data?.content || []);
    } catch (err) {
      console.error(err);
      setError("Unable to load customer profiles.");
    } finally {
      setLoadingCustomers(false);
    }
  };

  const handleInputChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));

    setError("");
    setMessage("");
  };

  const validateForm = () => {
    if (mode === "create" && !formData.customerId.trim()) {
      setError("Customer ID is required.");
      return false;
    }

    if (!formData.name.trim()) {
      setError("Customer name is required.");
      return false;
    }

    if (!formData.email.trim()) {
      setError("Email is required.");
      return false;
    }

    if (!formData.mobile.trim()) {
      setError("Mobile is required.");
      return false;
    }

    if (!formData.city.trim()) {
      setError("City is required.");
      return false;
    }

    return true;
  };

  const buildPayload = () => {
    return {
      customerId: formData.customerId.trim(),
      name: formData.name.trim(),
      email: formData.email.trim(),
      mobile: formData.mobile.trim(),
      city: formData.city.trim(),
      gender: formData.gender.trim(),
      age: formData.age ? Number(formData.age) : null,
      dateOfBirth: formData.dateOfBirth.trim(),
      address: formData.address.trim(),
      customerType: formData.customerType.trim(),
    };
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      setSaving(true);
      setError("");
      setMessage("");

      const payload = buildPayload();

      if (mode === "create") {
        const response = await createCustomerProfile(payload);
        setMessage(response.message || "Customer profile created successfully.");
      } else {
        const response = await updateCustomerProfile(editingCustomerId, payload);
        setMessage(response.message || "Customer profile updated successfully.");
      }

      resetForm();
      await loadCustomers();
    } catch (err) {
      console.error(err);

      const serverMessage =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        "Unable to save customer profile.";

      setError(serverMessage);
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (customer) => {
    setMode("edit");
    setEditingCustomerId(customer.customerId);

    setFormData({
      customerId: customer.customerId || "",
      name: customer.name || "",
      email: customer.email || "",
      mobile: customer.mobile || "",
      city: customer.city || "",
      gender: customer.gender === "Not Available" ? "" : customer.gender || "",
      age: customer.age || "",
      dateOfBirth:
        customer.dateOfBirth === "Not Available" ? "" : customer.dateOfBirth || "",
      address: customer.address === "Not Available" ? "" : customer.address || "",
      customerType:
        customer.customerType === "Not Available" ? "" : customer.customerType || "",
    });

    setError("");
    setMessage("");
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleDelete = async (customerId) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete customer profile ${customerId}?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingCustomerId(customerId);
      setError("");
      setMessage("");

      const response = await deleteCustomerProfile(customerId);
      setMessage(response.message || "Customer profile deleted successfully.");

      if (editingCustomerId === customerId) {
        resetForm();
      }

      await loadCustomers();
    } catch (err) {
      console.error(err);

      const serverMessage =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        "Unable to delete customer profile.";

      setError(serverMessage);
    } finally {
      setDeletingCustomerId("");
    }
  };

  const resetForm = () => {
    setMode("create");
    setEditingCustomerId("");
    setFormData(emptyForm);
  };

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <div className="admin-customer-page">
      <header className="admin-customer-header">
        <div>
          <h1>Manage Customer Profiles</h1>
          <p>
            Add, edit, and delete MongoDB customer profile data. CSV orders and
            JSON preferences remain separate data sources.
          </p>
        </div>

        <div className="admin-customer-actions">
          <div className="admin-customer-user">
            <strong>{user?.fullName || user?.username || "Admin User"}</strong>
            <span>{user?.role || "ADMIN"}</span>
          </div>

          <button type="button" onClick={() => navigate("/customers")}>
            Back to Dashboard
          </button>

          <button type="button" onClick={() => navigate("/admin/data-upload")}>
            Upload Data
          </button>

          <button type="button" className="danger-button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      {error && <div className="admin-customer-error">{error}</div>}
      {message && <div className="admin-customer-success">{message}</div>}

      <section className="admin-customer-layout">
        <form className="customer-form-card" onSubmit={handleSubmit}>
          <div className="customer-form-header">
            <h2>{mode === "create" ? "Add Customer" : "Edit Customer"}</h2>
            <p>
              {mode === "create"
                ? "Create a new customer profile in MongoDB."
                : `Editing customer profile: ${editingCustomerId}`}
            </p>
          </div>

          <div className="customer-form-grid">
            <label>
              Customer ID *
              <input
                type="text"
                name="customerId"
                value={formData.customerId}
                onChange={handleInputChange}
                disabled={mode === "edit"}
                placeholder="Example: C2001"
              />
            </label>

            <label>
              Name *
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                placeholder="Customer name"
              />
            </label>

            <label>
              Email *
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                placeholder="customer@example.com"
              />
            </label>

            <label>
              Mobile *
              <input
                type="text"
                name="mobile"
                value={formData.mobile}
                onChange={handleInputChange}
                placeholder="9876543210"
              />
            </label>

            <label>
              City *
              <input
                type="text"
                name="city"
                value={formData.city}
                onChange={handleInputChange}
                placeholder="Hyderabad"
              />
            </label>

            <label>
              Gender
              <select
                name="gender"
                value={formData.gender}
                onChange={handleInputChange}
              >
                <option value="">Select</option>
                <option value="Female">Female</option>
                <option value="Male">Male</option>
                <option value="Other">Other</option>
              </select>
            </label>

            <label>
              Age
              <input
                type="number"
                name="age"
                value={formData.age}
                onChange={handleInputChange}
                placeholder="28"
                min="0"
              />
            </label>

            <label>
              Date of Birth
              <input
                type="date"
                name="dateOfBirth"
                value={formData.dateOfBirth}
                onChange={handleInputChange}
              />
            </label>

            <label>
              Customer Type
              <input
                type="text"
                name="customerType"
                value={formData.customerType}
                onChange={handleInputChange}
                placeholder="Retail / Premium / Corporate"
              />
            </label>

            <label className="full-width-field">
              Address
              <textarea
                name="address"
                value={formData.address}
                onChange={handleInputChange}
                placeholder="Customer address"
                rows="3"
              />
            </label>
          </div>

          <div className="customer-form-actions">
            <button type="submit" disabled={saving}>
              {saving
                ? "Saving..."
                : mode === "create"
                ? "Add Customer"
                : "Update Customer"}
            </button>

            {mode === "edit" && (
              <button type="button" className="light-button" onClick={resetForm}>
                Cancel Edit
              </button>
            )}
          </div>
        </form>

        <section className="customer-management-card">
          <div className="customer-management-header">
            <h2>Customer Profiles</h2>
            <button type="button" onClick={loadCustomers} disabled={loadingCustomers}>
              {loadingCustomers ? "Refreshing..." : "Refresh"}
            </button>
          </div>

          {loadingCustomers ? (
            <p className="admin-customer-muted">Loading customer profiles...</p>
          ) : (
            <div className="customer-management-table-wrap">
              <table className="customer-management-table">
                <thead>
                  <tr>
                    <th>Customer ID</th>
                    <th>Name</th>
                    <th>City</th>
                    <th>Type</th>
                    <th>Orders</th>
                    <th>Segment</th>
                    <th>Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {customers.length === 0 ? (
                    <tr>
                      <td colSpan="7">No customer profiles found.</td>
                    </tr>
                  ) : (
                    customers.map((customer) => (
                      <tr key={customer.customerId}>
                        <td>{customer.customerId}</td>
                        <td>{customer.name}</td>
                        <td>{customer.city}</td>
                        <td>{customer.customerType}</td>
                        <td>{customer.totalOrders}</td>
                        <td>{customer.customerSegment}</td>
                        <td>
                          <div className="customer-row-actions">
                            <button
                              type="button"
                              onClick={() => handleEdit(customer)}
                            >
                              Edit
                            </button>

                            <button
                              type="button"
                              className="danger-button"
                              onClick={() => handleDelete(customer.customerId)}
                              disabled={deletingCustomerId === customer.customerId}
                            >
                              {deletingCustomerId === customer.customerId
                                ? "Deleting..."
                                : "Delete"}
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </section>

      <section className="admin-customer-note">
        <h3>Important</h3>
        <p>
          This page manages customer profile data in MongoDB only. Orders still
          come from CSV and preferences still come from JSON. If a newly added
          customer does not exist in CSV/JSON, the dashboard will show warnings
          for missing orders or preferences.
        </p>
      </section>
    </div>
  );
}

export default AdminCustomerManagementPage;
