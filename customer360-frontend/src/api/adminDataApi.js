import axios from "axios";

const ADMIN_DATA_API_BASE_URL = "http://localhost:8080/api/admin/data";

const getAuthHeaders = () => {
  const token = localStorage.getItem("customer360_token");

  if (!token) {
    return {};
  }

  return {
    Authorization: `Bearer ${token}`,
  };
};

export const getAdminDataStatus = async () => {
  const response = await axios.get(`${ADMIN_DATA_API_BASE_URL}/status`, {
    headers: getAuthHeaders(),
  });

  return response.data;
};

export const uploadOrdersCsv = async (file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await axios.post(
    `${ADMIN_DATA_API_BASE_URL}/upload/orders`,
    formData,
    {
      headers: {
        ...getAuthHeaders(),
        "Content-Type": "multipart/form-data",
      },
    }
  );

  return response.data;
};

export const uploadPreferencesJson = async (file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await axios.post(
    `${ADMIN_DATA_API_BASE_URL}/upload/preferences`,
    formData,
    {
      headers: {
        ...getAuthHeaders(),
        "Content-Type": "multipart/form-data",
      },
    }
  );

  return response.data;
};

export const resetDefaultData = async () => {
  const response = await axios.post(
    `${ADMIN_DATA_API_BASE_URL}/reset-defaults`,
    {},
    {
      headers: getAuthHeaders(),
    }
  );

  return response.data;
};
