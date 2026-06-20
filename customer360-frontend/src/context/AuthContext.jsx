import { createContext, useContext, useEffect, useState } from "react";
import { getLoggedInUser, loginUser } from "../api/authApi";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() =>
    localStorage.getItem("customer360_token")
  );

  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("customer360_user");
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const [authLoading, setAuthLoading] = useState(false);

  const isAuthenticated = Boolean(token && user);

  const login = async (username, password) => {
    const data = await loginUser({
      username,
      password,
    });

    const loggedInUser = {
      username: data.username,
      fullName: data.fullName,
      role: data.role,
    };

    localStorage.setItem("customer360_token", data.token);
    localStorage.setItem("customer360_user", JSON.stringify(loggedInUser));

    setToken(data.token);
    setUser(loggedInUser);

    return data;
  };

  const logout = () => {
    localStorage.removeItem("customer360_token");
    localStorage.removeItem("customer360_user");

    setToken(null);
    setUser(null);
  };

  const hasRole = (...allowedRoles) => {
    if (!user?.role) {
      return false;
    }

    return allowedRoles.includes(user.role);
  };

  useEffect(() => {
    const validateSavedToken = async () => {
      if (!token) {
        return;
      }

      try {
        setAuthLoading(true);

        const data = await getLoggedInUser(token);

        const refreshedUser = {
          username: data.username,
          fullName: data.fullName,
          role: data.role,
        };

        localStorage.setItem("customer360_user", JSON.stringify(refreshedUser));
        setUser(refreshedUser);
      } catch (error) {
        console.error(error);
        logout();
      } finally {
        setAuthLoading(false);
      }
    };

    validateSavedToken();
  }, []);

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        authLoading,
        isAuthenticated,
        login,
        logout,
        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}