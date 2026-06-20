import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import "./App.css";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./routes/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import CustomerListPage from "./pages/CustomerListPage";

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

          <Route path="/" element={<Navigate to="/customers" replace />} />
          <Route path="*" element={<Navigate to="/customers" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;