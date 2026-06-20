import axios from "axios";

const AUTH_API_BASE_URL = "http://localhost:8080/api/auth";

export const loginUser = async (credentials) => {
  const response = await axios.post(`${AUTH_API_BASE_URL}/login`, credentials);
  return response.data;
};

export const getLoggedInUser = async (token) => {
  const response = await axios.get(`${AUTH_API_BASE_URL}/me`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return response.data;
};