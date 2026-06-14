package com.customer360.backend.loader;

import com.customer360.backend.model.CustomerOrder;
import com.customer360.backend.model.CustomerPreference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataCache {

    private final Map<String, List<CustomerOrder>> ordersByCustomerId = new HashMap<>();

    private final Map<String, CustomerPreference> preferencesByCustomerId = new HashMap<>();

    public Map<String, List<CustomerOrder>> getOrdersByCustomerId() {
        return ordersByCustomerId;
    }

    public Map<String, CustomerPreference> getPreferencesByCustomerId() {
        return preferencesByCustomerId;
    }

    public void addOrder(CustomerOrder order) {
        ordersByCustomerId
                .computeIfAbsent(order.getCustomerId(), key -> new ArrayList<>())
                .add(order);
    }

    public void addPreference(CustomerPreference preference) {
        preferencesByCustomerId.put(preference.getCustomerId(), preference);
    }
}