import { useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function LoginPage() {
  const navigate = useNavigate();
  const { login, isAuthenticated } = useAuth();

  const [formData, setFormData] = useState({
    username: "admin",
    password: "Admin@123",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  if (isAuthenticated) {
    return <Navigate to="/customers" replace />;
  }

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      setLoading(true);
      setError("");

      await login(formData.username, formData.password);

      navigate("/customers", {
        replace: true,
      });
    } catch (err) {
      console.error(err);
      setError("Invalid username or password.");
    } finally {
      setLoading(false);
    }
  };

  const fillDemoUser = (username, password) => {
    setFormData({
      username,
      password,
    });
  };

  return (
    <div className="login-page">
      <div className="login-left-panel">
        <div className="login-brand-box">
          <div className="login-logo">C360</div>
          <h1>Customer360</h1>
          <p>
            Secure customer dashboard with authentication, role-based access,
            customer profile, AI summary and exports.
          </p>
        </div>

        <div className="login-feature-list">
          <span>✓ JWT Authentication</span>
          <span>✓ Protected Customer Dashboard</span>
          <span>✓ Admin / Manager / Viewer Roles</span>
        </div>
      </div>

      <div className="login-card">
        <div className="login-card-header">
          <h2>Login</h2>
          <p>Enter your credentials to continue.</p>
        </div>

        {error && <div className="login-error">{error}</div>}

        <form onSubmit={handleSubmit} className="login-form">
          <label>
            Username
            <input
              name="username"
              value={formData.username}
              onChange={handleChange}
              placeholder="admin"
              autoComplete="username"
            />
          </label>

          <label>
            Password
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Admin@123"
              autoComplete="current-password"
            />
          </label>

          <button type="submit" disabled={loading}>
            {loading ? "Signing in..." : "Login"}
          </button>
        </form>

        <div className="demo-users-box">
          <h3>Demo Users</h3>

          <button
            type="button"
            onClick={() => fillDemoUser("admin", "Admin@123")}
          >
            Admin
          </button>

          <button
            type="button"
            onClick={() => fillDemoUser("manager", "Manager@123")}
          >
            Manager
          </button>

          <button
            type="button"
            onClick={() => fillDemoUser("viewer", "Viewer@123")}
          >
            Viewer
          </button>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;