import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import "./App.css";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./routes/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import CustomerListPage from "./pages/CustomerListPage";
import AdminDataUploadPage from "./pages/AdminDataUploadPage";
import AdminCustomerManagementPage from "./pages/AdminCustomerManagementPage";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route
            path="/customers"
            element={
              <ProtectedRoute>
                <CustomerListPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/admin/data-upload"
            element={
              <ProtectedRoute>
                <AdminDataUploadPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/admin/customers"
            element={
              <ProtectedRoute>
                <AdminCustomerManagementPage />
              </ProtectedRoute>
            }
          />

          <Route path="/" element={<Navigate to="/customers" replace />} />
          <Route path="*" element={<Navigate to="/customers" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
