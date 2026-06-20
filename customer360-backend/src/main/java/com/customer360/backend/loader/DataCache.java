package com.customer360.backend.loader;

import com.customer360.backend.model.CustomerOrder;
import com.customer360.backend.model.CustomerPreference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataCache {

    private Map<String, List<CustomerOrder>> ordersByCustomerId = new ConcurrentHashMap<>();
    private Map<String, CustomerPreference> preferencesByCustomerId = new ConcurrentHashMap<>();

    public synchronized void setOrdersByCustomerId(
            Map<String, List<CustomerOrder>> ordersByCustomerId
    ) {
        Map<String, List<CustomerOrder>> updatedOrders = new ConcurrentHashMap<>();

        if (ordersByCustomerId != null) {
            for (Map.Entry<String, List<CustomerOrder>> entry : ordersByCustomerId.entrySet()) {
                updatedOrders.put(entry.getKey(), List.copyOf(entry.getValue()));
            }
        }

        this.ordersByCustomerId = updatedOrders;
    }

    public List<CustomerOrder> getOrdersForCustomer(String customerId) {
        if (customerId == null) {
            return List.of();
        }

        return ordersByCustomerId.getOrDefault(customerId, List.of());
    }

    public synchronized void setPreferencesByCustomerId(
            Map<String, CustomerPreference> preferencesByCustomerId
    ) {
        this.preferencesByCustomerId = preferencesByCustomerId == null
                ? new ConcurrentHashMap<>()
                : new ConcurrentHashMap<>(preferencesByCustomerId);
    }

    public void addPreference(CustomerPreference preference) {
        if (preference == null
                || preference.getCustomerId() == null
                || preference.getCustomerId().isBlank()) {
            return;
        }

        preferencesByCustomerId.put(preference.getCustomerId(), preference);
    }

    public CustomerPreference getPreferenceForCustomer(String customerId) {
        if (customerId == null) {
            return null;
        }

        return preferencesByCustomerId.get(customerId);
    }

    public int getOrderCustomerCount() {
        return ordersByCustomerId.size();
    }

    public int getPreferenceCustomerCount() {
        return preferencesByCustomerId.size();
    }
}
