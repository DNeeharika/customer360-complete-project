package com.customer360.backend.service;

import com.customer360.backend.dto.CustomerDetailResponse;
import com.customer360.backend.dto.OrderResponse;
import com.customer360.backend.loader.DataCache;
import com.customer360.backend.model.CustomerOrder;
import com.customer360.backend.model.CustomerPreference;
import com.customer360.backend.model.CustomerProfile;
import com.customer360.backend.repository.CustomerProfileRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CustomerConsolidationService {

    private final CustomerProfileRepository customerProfileRepository;
    private final DataCache dataCache;
    private final MaskingService maskingService;

    public CustomerConsolidationService(CustomerProfileRepository customerProfileRepository,
                                        DataCache dataCache,
                                        MaskingService maskingService) {
        this.customerProfileRepository = customerProfileRepository;
        this.dataCache = dataCache;
        this.maskingService = maskingService;
    }

    public List<CustomerDetailResponse> getAllCustomers(
            String search,
            String city,
            String membership,
            String preferredChannel,
            String sortBy,
            String sortDir
    ) {
        List<CustomerProfile> profiles = customerProfileRepository.findAll();

        List<CustomerDetailResponse> customers = profiles
                .stream()
                .map(profile -> buildCustomerDetailResponse(profile.getCustomerId(), profile))
                .filter(customer -> matchesSearch(customer, search))
                .filter(customer -> matchesFilter(customer.getCity(), city))
                .filter(customer -> matchesFilter(customer.getMembership(), membership))
                .filter(customer -> matchesFilter(customer.getPreferredChannel(), preferredChannel))
                .toList();

        return sortCustomers(customers, sortBy, sortDir);
    }

    public CustomerDetailResponse getCustomerDetails(String customerId) {

        CustomerProfile profile = customerProfileRepository
                .findByCustomerId(customerId)
                .orElse(null);

        return buildCustomerDetailResponse(customerId, profile);
    }

    private CustomerDetailResponse buildCustomerDetailResponse(String customerId, CustomerProfile profile) {

        List<CustomerOrder> customerOrders = dataCache
                .getOrdersByCustomerId()
                .getOrDefault(customerId, new ArrayList<>());

        CustomerPreference preference = dataCache
                .getPreferencesByCustomerId()
                .get(customerId);

        List<String> warnings = new ArrayList<>();

        if (profile == null) {
            warnings.add("Customer profile not found in MongoDB.");
        }

        if (customerOrders.isEmpty()) {
            warnings.add("No orders found in CSV file.");
        }

        if (preference == null) {
            warnings.add("Customer preference not found in JSON file.");
        }

        List<OrderResponse> orderResponses = customerOrders
                .stream()
                .map(order -> new OrderResponse(
                        order.getOrderId(),
                        order.getOrderDate(),
                        order.getAmount()
                ))
                .toList();

        BigDecimal totalOrderAmount = customerOrders
                .stream()
                .map(CustomerOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CustomerDetailResponse(
                customerId,
                profile != null ? profile.getName() : "Not Available",
                profile != null ? maskingService.maskEmail(profile.getEmail()) : "Not Available",
                profile != null ? maskingService.maskMobile(profile.getMobile()) : "Not Available",
                profile != null ? profile.getCity() : "Not Available",
                preference != null ? preference.getMembership() : "Not Available",
                preference != null ? preference.getPreferredChannel() : "Not Available",
                orderResponses,
                orderResponses.size(),
                totalOrderAmount,
                warnings
        );
    }

    private List<CustomerDetailResponse> sortCustomers(
            List<CustomerDetailResponse> customers,
            String sortBy,
            String sortDir
    ) {
        if (sortBy == null || sortBy.isBlank()) {
            return customers;
        }

        Comparator<CustomerDetailResponse> comparator;

        switch (sortBy.toLowerCase()) {
            case "customerid":
                comparator = Comparator.comparing(
                        CustomerDetailResponse::getCustomerId,
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "name":
                comparator = Comparator.comparing(
                        CustomerDetailResponse::getName,
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "city":
                comparator = Comparator.comparing(
                        CustomerDetailResponse::getCity,
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "membership":
                comparator = Comparator.comparing(
                        CustomerDetailResponse::getMembership,
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "preferredchannel":
                comparator = Comparator.comparing(
                        CustomerDetailResponse::getPreferredChannel,
                        String.CASE_INSENSITIVE_ORDER
                );
                break;

            case "totalorders":
                comparator = Comparator.comparing(CustomerDetailResponse::getTotalOrders);
                break;

            case "totalorderamount":
                comparator = Comparator.comparing(CustomerDetailResponse::getTotalOrderAmount);
                break;

            default:
                throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
        }

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        return customers
                .stream()
                .sorted(comparator)
                .toList();
    }

    private boolean matchesSearch(CustomerDetailResponse customer, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }

        String searchText = search.toLowerCase();

        return containsIgnoreCase(customer.getCustomerId(), searchText)
                || containsIgnoreCase(customer.getName(), searchText)
                || containsIgnoreCase(customer.getCity(), searchText)
                || containsIgnoreCase(customer.getMembership(), searchText)
                || containsIgnoreCase(customer.getPreferredChannel(), searchText);
    }

    private boolean matchesFilter(String actualValue, String filterValue) {
        if (filterValue == null || filterValue.isBlank()) {
            return true;
        }

        return actualValue != null && actualValue.equalsIgnoreCase(filterValue);
    }

    private boolean containsIgnoreCase(String value, String searchText) {
        return value != null && value.toLowerCase().contains(searchText);
    }
}