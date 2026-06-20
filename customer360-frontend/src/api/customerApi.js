import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/customers";

const getAuthHeaders = () => {
  const token = localStorage.getItem("customer360_token");

  if (!token) {
    return {};
  }

  return {
    Authorization: `Bearer ${token}`,
  };
};

export const getCustomers = async (params = {}) => {
  const response = await axios.get(API_BASE_URL, {
    params,
    headers: getAuthHeaders(),
  });

  return response.data;
};

export const getCustomersPage = async (params = {}) => {
  const response = await axios.get(`${API_BASE_URL}/page`, {
    params,
    headers: getAuthHeaders(),
  });

  return response.data;
};

export const getCustomerDetails = async (customerId) => {
  const response = await axios.get(`${API_BASE_URL}/${customerId}`, {
    headers: getAuthHeaders(),
  });

  return response.data;
};

export const getCustomerSummary = async (customerId) => {
  const response = await axios.get(`${API_BASE_URL}/${customerId}/summary`, {
    headers: getAuthHeaders(),
  });

  return response.data;
};

export const downloadExport = async (format) => {
  const response = await axios.get(`${API_BASE_URL}/export/${format}`, {
    responseType: "blob",
    headers: getAuthHeaders(),
  });

  const fileExtensionMap = {
    csv: "csv",
    excel: "xlsx",
    pdf: "pdf",
  };

  const fileNameMap = {
    csv: "customers.csv",
    excel: "customers.xlsx",
    pdf: "customers.pdf",
  };

  const extension = fileExtensionMap[format] || format;
  const fileName = fileNameMap[format] || `customers.${extension}`;

  const blob = new Blob([response.data]);
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");

  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();

  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};