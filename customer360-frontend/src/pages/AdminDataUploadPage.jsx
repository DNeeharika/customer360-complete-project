import { useEffect, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import {
  getAdminDataStatus,
  resetDefaultData,
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
  const [resetResult, setResetResult] = useState(null);

  const [loadingStatus, setLoadingStatus] = useState(false);
  const [uploadingOrders, setUploadingOrders] = useState(false);
  const [uploadingPreferences, setUploadingPreferences] = useState(false);
  const [resettingData, setResettingData] = useState(false);

  const [error, setError] = useState("");

  const isAdmin = hasRole("ADMIN");

  useEffect(() => {
    if (isAdmin) {
      loadStatus();
    }
  }, [isAdmin]);

  if (!isAdmin) {
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

  const handleOrdersFileChange = (event) => {
    const selectedFile = event.target.files?.[0] || null;
    setOrdersFile(selectedFile);
    setOrdersUploadResult(null);
    setResetResult(null);
    setError("");
  };

  const handlePreferencesFileChange = (event) => {
    const selectedFile = event.target.files?.[0] || null;
    setPreferencesFile(selectedFile);
    setPreferencesUploadResult(null);
    setResetResult(null);
    setError("");
  };

  const handleOrdersUpload = async () => {
    if (!ordersFile) {
      setError("Please select an orders CSV file.");
      return;
    }

    try {
      setUploadingOrders(true);
      setError("");
      setOrdersUploadResult(null);
      setResetResult(null);

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
      setResetResult(null);

      const data = await uploadPreferencesJson(preferencesFile);
      setPreferencesUploadResult(data);
      setPreferencesFile(null);

      await loadStatus();
    } catch (err) {
      console.error(err);
      setError(
        "Unable to upload preferences JSON file. Please check the file format."
      );
    } finally {
      setUploadingPreferences(false);
    }
  };

  const handleResetToDefault = async () => {
    const confirmed = window.confirm(
      "This will delete uploaded CSV/JSON files and restore default data. Continue?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setResettingData(true);
      setError("");
      setResetResult(null);
      setOrdersUploadResult(null);
      setPreferencesUploadResult(null);

      const data = await resetDefaultData();
      setResetResult(data);

      await loadStatus();
    } catch (err) {
      console.error(err);
      setError("Unable to reset data to default files.");
    } finally {
      setResettingData(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  const isAnyActionRunning =
    uploadingOrders || uploadingPreferences || resettingData;

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
            <strong>{user?.fullName || user?.username || "Admin User"}</strong>
            <span>{user?.role || "ADMIN"}</span>
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
            {loadingStatus
              ? "Loading..."
              : status?.preferencesCustomerCount ?? "-"}
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
              Upload a new orders CSV file. The backend will save it under the
              uploads folder and refresh the dashboard immediately.
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
            onChange={handleOrdersFileChange}
            disabled={isAnyActionRunning}
          />

          {ordersFile && (
            <p className="selected-file">Selected: {ordersFile.name}</p>
          )}

          <button
            type="button"
            onClick={handleOrdersUpload}
            disabled={uploadingOrders || resettingData}
          >
            {uploadingOrders ? "Uploading..." : "Upload Orders CSV"}
          </button>

          {ordersUploadResult && <UploadResult result={ordersUploadResult} />}
        </div>

        <div className="admin-upload-card">
          <div className="upload-card-header">
            <h2>Upload Preferences JSON</h2>
            <p>
              Upload a new preferences JSON file. The backend will save it under
              the uploads folder and refresh the dashboard immediately.
            </p>
          </div>

          <div className="upload-format-box">
            Required format:
            <code>
              JSON array of preference objects with customerId, membership,
              preferredChannel, preferredLanguage, notificationOptIn,
              marketingConsent and preferredContactTime.
            </code>
          </div>

          <input
            type="file"
            accept=".json"
            onChange={handlePreferencesFileChange}
            disabled={isAnyActionRunning}
          />

          {preferencesFile && (
            <p className="selected-file">Selected: {preferencesFile.name}</p>
          )}

          <button
            type="button"
            onClick={handlePreferencesUpload}
            disabled={uploadingPreferences || resettingData}
          >
            {uploadingPreferences
              ? "Uploading..."
              : "Upload Preferences JSON"}
          </button>

          {preferencesUploadResult && (
            <UploadResult result={preferencesUploadResult} />
          )}
        </div>
      </section>

      <section className="admin-upload-note">
        <h3>Reset to Default Data</h3>
        <p>
          This will delete the uploaded CSV/JSON files from the backend uploads
          folder and reload the original default data from backend resources.
        </p>

        <button
          type="button"
          className="reset-default-button"
          onClick={handleResetToDefault}
          disabled={resettingData || uploadingOrders || uploadingPreferences}
        >
          {resettingData ? "Resetting..." : "Reset to Default Data"}
        </button>

        {resetResult && (
          <div className="upload-result-box">
            <h3>{resetResult.message}</h3>
            <p>
              <strong>Orders Customer Count:</strong>{" "}
              {resetResult.ordersCustomerCount}
            </p>
            <p>
              <strong>Preferences Customer Count:</strong>{" "}
              {resetResult.preferencesCustomerCount}
            </p>
          </div>
        )}
      </section>

      <section className="admin-upload-note">
        <h3>Important</h3>
        <p>
          Uploaded CSV/JSON files are stored persistently under the backend
          uploads folder. On backend restart, uploaded files are loaded first. If
          uploaded files are not available, default files from backend resources
          are loaded.
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
