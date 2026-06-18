package com.customer360.backend.loader;

import com.customer360.backend.model.CustomerOrder;
import com.customer360.backend.model.CustomerPreference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DataCache {

    private Map<String, List<CustomerOrder>> ordersByCustomerId = new HashMap<>();
    private Map<String, CustomerPreference> preferencesByCustomerId = new HashMap<>();

    public Map<String, List<CustomerOrder>> getOrdersByCustomerId() {
        return ordersByCustomerId;
    }

    public void setOrdersByCustomerId(Map<String, List<CustomerOrder>> ordersByCustomerId) {
        this.ordersByCustomerId = ordersByCustomerId != null ? ordersByCustomerId : new HashMap<>();
    }

    public Map<String, CustomerPreference> getPreferencesByCustomerId() {
        return preferencesByCustomerId;
    }

    public void setPreferencesByCustomerId(Map<String, CustomerPreference> preferencesByCustomerId) {
        this.preferencesByCustomerId = preferencesByCustomerId != null ? preferencesByCustomerId : new HashMap<>();
    }

    public void addOrder(CustomerOrder order) {
        if (order == null || order.getCustomerId() == null || order.getCustomerId().isBlank()) {
            return;
        }

        ordersByCustomerId
                .computeIfAbsent(order.getCustomerId(), key -> new ArrayList<>())
                .add(order);
    }

    public void addPreference(CustomerPreference preference) {
        if (preference == null || preference.getCustomerId() == null || preference.getCustomerId().isBlank()) {
            return;
        }

        preferencesByCustomerId.put(preference.getCustomerId(), preference);
    }

    public List<CustomerOrder> getOrdersForCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return Collections.emptyList();
        }

        return ordersByCustomerId.getOrDefault(customerId, Collections.emptyList());
    }

    public CustomerPreference getPreferenceForCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return null;
        }

        return preferencesByCustomerId.get(customerId);
    }

    public Set<String> getOrderCustomerIds() {
        return new HashSet<>(ordersByCustomerId.keySet());
    }

    public Set<String> getPreferenceCustomerIds() {
        return new HashSet<>(preferencesByCustomerId.keySet());
    }

    /*
     * Backward-compatible method aliases.
     */

    public Map<String, List<CustomerOrder>> getCustomerOrdersByCustomerId() {
        return getOrdersByCustomerId();
    }

    public void setCustomerOrdersByCustomerId(Map<String, List<CustomerOrder>> ordersByCustomerId) {
        setOrdersByCustomerId(ordersByCustomerId);
    }

    public Map<String, CustomerPreference> getCustomerPreferencesByCustomerId() {
        return getPreferencesByCustomerId();
    }

    public void setCustomerPreferencesByCustomerId(Map<String, CustomerPreference> preferencesByCustomerId) {
        setPreferencesByCustomerId(preferencesByCustomerId);
    }

    public Map<String, List<CustomerOrder>> getOrders() {
        return getOrdersByCustomerId();
    }

    public void setOrders(Map<String, List<CustomerOrder>> ordersByCustomerId) {
        setOrdersByCustomerId(ordersByCustomerId);
    }

    public Map<String, CustomerPreference> getPreferences() {
        return getPreferencesByCustomerId();
    }

    public void setPreferences(Map<String, CustomerPreference> preferencesByCustomerId) {
        setPreferencesByCustomerId(preferencesByCustomerId);
    }
}