import { useEffect, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import {
  getAdminDataStatus,
  uploadOrdersCsv,
  uploadPreferencesJson,
} from "../api/adminDataApi";
import { useAuth } from "../context/AuthContext";

function AdminDataUploadPage() {
  const navigate = useNavigate();
  const { user, hasRole, logout } = useAuth();

  const [ordersFile, setOrdersFile] = useState(null);
  const [preferencesFile, setPreferencesFile] = useState(null);

  const [status, setStatus] = useState(null);
  const [ordersUploadResult, setOrdersUploadResult] = useState(null);
  const [preferencesUploadResult, setPreferencesUploadResult] = useState(null);

  const [loadingStatus, setLoadingStatus] = useState(false);
  const [uploadingOrders, setUploadingOrders] = useState(false);
  const [uploadingPreferences, setUploadingPreferences] = useState(false);

  const [error, setError] = useState("");

  if (!hasRole("ADMIN")) {
    return <Navigate to="/customers" replace />;
  }

  const loadStatus = async () => {
    try {
      setLoadingStatus(true);
      setError("");

      const data = await getAdminDataStatus();
      setStatus(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load data status.");
    } finally {
      setLoadingStatus(false);
    }
  };

  useEffect(() => {
    loadStatus();
  }, []);

  const handleOrdersUpload = async () => {
    if (!ordersFile) {
      setError("Please select an orders CSV file.");
      return;
    }

    try {
      setUploadingOrders(true);
      setError("");
      setOrdersUploadResult(null);

      const data = await uploadOrdersCsv(ordersFile);
      setOrdersUploadResult(data);
      setOrdersFile(null);

      await loadStatus();
    } catch (err) {
      console.error(err);
      setError("Unable to upload orders CSV file. Please check the file format.");
    } finally {
      setUploadingOrders(false);
    }
  };

  const handlePreferencesUpload = async () => {
    if (!preferencesFile) {
      setError("Please select a preferences JSON file.");
      return;
    }

    try {
      setUploadingPreferences(true);
      setError("");
      setPreferencesUploadResult(null);

      const data = await uploadPreferencesJson(preferencesFile);
      setPreferencesUploadResult(data);
      setPreferencesFile(null);

      await loadStatus();
    } catch (err) {
      console.error(err);
      setError("Unable to upload preferences JSON file. Please check the file format.");
    } finally {
      setUploadingPreferences(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <div className="admin-upload-page">
      <header className="admin-upload-header">
        <div>
          <h1>Admin Data Upload</h1>
          <p>
            Upload new CSV and JSON source files to dynamically refresh the
            Customer360 dashboard.
          </p>
        </div>

        <div className="admin-upload-actions">
          <div className="admin-upload-user">
            <strong>{user?.fullName || user?.username}</strong>
            <span>{user?.role}</span>
          </div>

          <button type="button" onClick={() => navigate("/customers")}>
            Back to Dashboard
          </button>

          <button type="button" className="danger-button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      {error && <div className="admin-upload-error">{error}</div>}

      <section className="admin-status-grid">
        <div className="admin-status-card">
          <span>Orders Customer Count</span>
          <strong>
            {loadingStatus ? "Loading..." : status?.ordersCustomerCount ?? "-"}
          </strong>
          <p>Customer IDs currently available from CSV orders source.</p>
        </div>

        <div className="admin-status-card">
          <span>Preferences Customer Count</span>
          <strong>
            {loadingStatus ? "Loading..." : status?.preferencesCustomerCount ?? "-"}
          </strong>
          <p>Customer IDs currently available from JSON preferences source.</p>
        </div>

        <div className="admin-status-card">
          <span>Dynamic Cache Status</span>
          <strong>{loadingStatus ? "Loading..." : "Active"}</strong>
          <p>{status?.message || "Dynamic CSV and JSON cache status."}</p>
        </div>
      </section>

      <section className="admin-upload-grid">
        <div className="admin-upload-card">
          <div className="upload-card-header">
            <h2>Upload Orders CSV</h2>
            <p>
              Replaces the current in-memory orders cache with the uploaded CSV
              file.
            </p>
          </div>

          <div className="upload-format-box">
            Required columns:
            <code>
              customerId, orderId, orderDate, amount, productCategory,
              orderStatus, paymentMode, discountAmount
            </code>
          </div>

          <input
            type="file"
            accept=".csv"
            onChange={(event) => setOrdersFile(event.target.files?.[0] || null)}
          />

          {ordersFile && (
            <p className="selected-file">Selected: {ordersFile.name}</p>
          )}

          <button
            type="button"
            onClick={handleOrdersUpload}
            disabled={uploadingOrders}
          >
            {uploadingOrders ? "Uploading..." : "Upload Orders CSV"}
          </button>

          {ordersUploadResult && (
            <UploadResult result={ordersUploadResult} />
          )}
        </div>

        <div className="admin-upload-card">
          <div className="upload-card-header">
            <h2>Upload Preferences JSON</h2>
            <p>
              Replaces the current in-memory preferences cache with the uploaded
              JSON file.
            </p>
          </div>

          <div className="upload-format-box">
            Required format:
            <code>
              JSON array of preference objects with customerId, membership,
              preferredChannel and consent fields.
            </code>
          </div>

          <input
            type="file"
            accept=".json"
            onChange={(event) =>
              setPreferencesFile(event.target.files?.[0] || null)
            }
          />

          {preferencesFile && (
            <p className="selected-file">Selected: {preferencesFile.name}</p>
          )}

          <button
            type="button"
            onClick={handlePreferencesUpload}
            disabled={uploadingPreferences}
          >
            {uploadingPreferences ? "Uploading..." : "Upload Preferences JSON"}
          </button>

          {preferencesUploadResult && (
            <UploadResult result={preferencesUploadResult} />
          )}
        </div>
      </section>

      <section className="admin-upload-note">
        <h3>Important</h3>
        <p>
          Uploaded CSV/JSON data is refreshed in backend memory/cache. If the
          backend is restarted, default files from the backend resources folder
          will be loaded again.
        </p>
      </section>
    </div>
  );
}

function UploadResult({ result }) {
  return (
    <div className="upload-result-box">
      <h3>{result.message}</h3>
      <p>
        <strong>Type:</strong> {result.dataType}
      </p>
      <p>
        <strong>Source:</strong> {result.sourceName}
      </p>
      <p>
        <strong>Loaded Rows:</strong> {result.loadedRows}
      </p>
      <p>
        <strong>Skipped Rows:</strong> {result.skippedRows}
      </p>
      <p>
        <strong>Uploaded At:</strong> {result.uploadedAt}
      </p>
    </div>
  );
}

export default AdminDataUploadPage;
