import axios from "axios";

const ADMIN_CUSTOMER_API_BASE_URL = "http://localhost:8080/api/admin/customers";

const getAuthHeaders = () => {
  const token = localStorage.getItem("customer360_token");

  if (!token) {
    return {};
  }

  return {
    Authorization: `Bearer ${token}`,
  };
};

export const createCustomerProfile = async (customerData) => {
  const response = await axios.post(ADMIN_CUSTOMER_API_BASE_URL, customerData, {
    headers: {
      ...getAuthHeaders(),
      "Content-Type": "application/json",
    },
  });

  return response.data;
};

export const updateCustomerProfile = async (customerId, customerData) => {
  const response = await axios.put(
    `${ADMIN_CUSTOMER_API_BASE_URL}/${customerId}`,
    customerData,
    {
      headers: {
        ...getAuthHeaders(),
        "Content-Type": "application/json",
      },
    }
  );

  return response.data;
};

export const deleteCustomerProfile = async (customerId) => {
  const response = await axios.delete(
    `${ADMIN_CUSTOMER_API_BASE_URL}/${customerId}`,
    {
      headers: getAuthHeaders(),
    }
  );

  return response.data;
};
