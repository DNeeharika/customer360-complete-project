import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/customers";

export const getCustomers = async (params) => {
  const response = await axios.get(API_BASE_URL, { params });
  return response.data;
};

export const getCustomerDetails = async (customerId) => {
  const response = await axios.get(`${API_BASE_URL}/${customerId}`);
  return response.data;
};

export const getCustomerSummary = async (customerId) => {
  const response = await axios.get(`${API_BASE_URL}/${customerId}/summary`);
  return response.data;
};

export const downloadExport = (type) => {
  window.open(`${API_BASE_URL}/export/${type}`, "_blank");
};