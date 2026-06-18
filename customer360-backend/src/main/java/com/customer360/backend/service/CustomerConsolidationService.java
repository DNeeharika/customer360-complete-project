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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerConsolidationService {

    private static final String NOT_AVAILABLE = "Not Available";

    private final CustomerProfileRepository customerProfileRepository;
    private final DataCache dataCache;
    private final MaskingService maskingService;

    public CustomerConsolidationService(
            CustomerProfileRepository customerProfileRepository,
            DataCache dataCache,
            MaskingService maskingService
    ) {
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

        List<CustomerDetailResponse> customers = profiles.stream()
                .map(profile -> buildCustomerDetail(profile.getCustomerId(), profile))
                .collect(Collectors.toCollection(ArrayList::new));

        customers = applySearch(customers, search);
        customers = applyFilters(customers, city, membership, preferredChannel);
        customers = applySorting(customers, sortBy, sortDir);

        return customers;
    }

    public CustomerDetailResponse getCustomerDetails(String customerId) {
        Optional<CustomerProfile> profileOptional = customerProfileRepository.findByCustomerId(customerId);
        return buildCustomerDetail(customerId, profileOptional.orElse(null));
    }

    private CustomerDetailResponse buildCustomerDetail(String customerId, CustomerProfile profile) {
        List<String> warnings = new ArrayList<>();

        String name = NOT_AVAILABLE;
        String email = NOT_AVAILABLE;
        String mobile = NOT_AVAILABLE;
        String city = NOT_AVAILABLE;
        String gender = NOT_AVAILABLE;
        Integer age = null;
        String dateOfBirth = NOT_AVAILABLE;
        String address = NOT_AVAILABLE;
        String customerType = NOT_AVAILABLE;

        if (profile == null) {
            warnings.add("Customer profile not found in MongoDB.");
        } else {
            name = defaultIfBlank(profile.getName());
            email = maskingService.maskEmail(profile.getEmail());
            mobile = maskingService.maskMobile(profile.getMobile());
            city = defaultIfBlank(profile.getCity());
            gender = defaultIfBlank(profile.getGender());
            age = profile.getAge();
            dateOfBirth = defaultIfBlank(profile.getDateOfBirth());
            address = defaultIfBlank(profile.getAddress());
            customerType = defaultIfBlank(profile.getCustomerType());
        }

        CustomerPreference preference = dataCache.getPreferenceForCustomer(customerId);

        String membership = NOT_AVAILABLE;
        String preferredChannel = NOT_AVAILABLE;
        String preferredLanguage = NOT_AVAILABLE;
        Boolean notificationOptIn = false;
        Boolean marketingConsent = false;
        String preferredContactTime = NOT_AVAILABLE;

        if (preference == null) {
            warnings.add("Customer preference not found in JSON file.");
        } else {
            membership = defaultIfBlank(preference.getMembership());
            preferredChannel = defaultIfBlank(preference.getPreferredChannel());
            preferredLanguage = defaultIfBlank(preference.getPreferredLanguage());
            notificationOptIn = preference.getNotificationOptIn() != null && preference.getNotificationOptIn();
            marketingConsent = preference.getMarketingConsent() != null && preference.getMarketingConsent();
            preferredContactTime = defaultIfBlank(preference.getPreferredContactTime());
        }

        List<CustomerOrder> orders = dataCache.getOrdersForCustomer(customerId);

        if (orders == null || orders.isEmpty()) {
            warnings.add("No orders found in CSV file.");
            orders = new ArrayList<>();
        }

        List<OrderResponse> orderResponses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());

        BigDecimal totalOrderAmount = orderResponses.stream()
                .map(OrderResponse::getAmount)
                .map(this::defaultZero)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscountAmount = orderResponses.stream()
                .map(OrderResponse::getDiscountAmount)
                .map(this::defaultZero)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netOrderAmount = orderResponses.stream()
                .map(OrderResponse::getNetAmount)
                .map(this::defaultZero)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = orderResponses.size();
        int rewardPoints = calculateRewardPoints(netOrderAmount);
        String customerSegment = calculateCustomerSegment(netOrderAmount, totalOrders);
        int dataQualityScore = calculateDataQualityScore(warnings);
        String dataQualityStatus = calculateDataQualityStatus(dataQualityScore);

        return new CustomerDetailResponse(
                customerId,
                name,
                email,
                mobile,
                city,
                gender,
                age,
                dateOfBirth,
                address,
                customerType,
                membership,
                preferredChannel,
                preferredLanguage,
                notificationOptIn,
                marketingConsent,
                preferredContactTime,
                orderResponses,
                totalOrders,
                totalOrderAmount,
                totalDiscountAmount,
                netOrderAmount,
                rewardPoints,
                customerSegment,
                dataQualityScore,
                dataQualityStatus,
                warnings
        );
    }

    private OrderResponse toOrderResponse(CustomerOrder order) {
        BigDecimal amount = defaultZero(order.getAmount());
        BigDecimal discountAmount = defaultZero(order.getDiscountAmount());
        BigDecimal netAmount = amount.subtract(discountAmount);

        if (netAmount.compareTo(BigDecimal.ZERO) < 0) {
            netAmount = BigDecimal.ZERO;
        }

        return new OrderResponse(
                defaultIfBlank(order.getOrderId()),
                order.getOrderDate() != null ? order.getOrderDate().toString() : NOT_AVAILABLE,
                amount,
                defaultIfBlank(order.getProductCategory()),
                defaultIfBlank(order.getOrderStatus()),
                defaultIfBlank(order.getPaymentMode()),
                discountAmount,
                netAmount
        );
    }

    private List<CustomerDetailResponse> applySearch(List<CustomerDetailResponse> customers, String search) {
        if (search == null || search.isBlank()) {
            return customers;
        }

        String keyword = search.toLowerCase(Locale.ROOT);

        return customers.stream()
                .filter(customer ->
                        contains(customer.getCustomerId(), keyword)
                                || contains(customer.getName(), keyword)
                                || contains(customer.getCity(), keyword)
                                || contains(customer.getGender(), keyword)
                                || contains(customer.getCustomerType(), keyword)
                                || contains(customer.getMembership(), keyword)
                                || contains(customer.getPreferredChannel(), keyword)
                                || contains(customer.getPreferredLanguage(), keyword)
                                || contains(customer.getCustomerSegment(), keyword)
                                || contains(customer.getDataQualityStatus(), keyword)
                )
                .collect(Collectors.toList());
    }

    private List<CustomerDetailResponse> applyFilters(
            List<CustomerDetailResponse> customers,
            String city,
            String membership,
            String preferredChannel
    ) {
        return customers.stream()
                .filter(customer -> matchesFilter(customer.getCity(), city))
                .filter(customer -> matchesFilter(customer.getMembership(), membership))
                .filter(customer -> matchesFilter(customer.getPreferredChannel(), preferredChannel))
                .collect(Collectors.toList());
    }

    private List<CustomerDetailResponse> applySorting(
            List<CustomerDetailResponse> customers,
            String sortBy,
            String sortDir
    ) {
        String selectedSortBy = sortBy == null || sortBy.isBlank() ? "customerId" : sortBy;
        String selectedSortDir = sortDir == null || sortDir.isBlank() ? "asc" : sortDir;

        Comparator<CustomerDetailResponse> comparator = switch (selectedSortBy) {
            case "customerId" -> Comparator.comparing(CustomerDetailResponse::getCustomerId, Comparator.nullsLast(String::compareToIgnoreCase));
            case "name" -> Comparator.comparing(CustomerDetailResponse::getName, Comparator.nullsLast(String::compareToIgnoreCase));
            case "city" -> Comparator.comparing(CustomerDetailResponse::getCity, Comparator.nullsLast(String::compareToIgnoreCase));
            case "gender" -> Comparator.comparing(CustomerDetailResponse::getGender, Comparator.nullsLast(String::compareToIgnoreCase));
            case "customerType" -> Comparator.comparing(CustomerDetailResponse::getCustomerType, Comparator.nullsLast(String::compareToIgnoreCase));
            case "membership" -> Comparator.comparing(CustomerDetailResponse::getMembership, Comparator.nullsLast(String::compareToIgnoreCase));
            case "preferredChannel" -> Comparator.comparing(CustomerDetailResponse::getPreferredChannel, Comparator.nullsLast(String::compareToIgnoreCase));
            case "totalOrders" -> Comparator.comparing(CustomerDetailResponse::getTotalOrders);
            case "totalOrderAmount" -> Comparator.comparing(CustomerDetailResponse::getTotalOrderAmount);
            case "netOrderAmount" -> Comparator.comparing(CustomerDetailResponse::getNetOrderAmount);
            case "rewardPoints" -> Comparator.comparing(CustomerDetailResponse::getRewardPoints);
            case "dataQualityScore" -> Comparator.comparing(CustomerDetailResponse::getDataQualityScore);
            default -> throw new IllegalArgumentException("Invalid sortBy value: " + selectedSortBy);
        };

        if ("desc".equalsIgnoreCase(selectedSortDir)) {
            comparator = comparator.reversed();
        }

        return customers.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private boolean matchesFilter(String actualValue, String filterValue) {
        if (filterValue == null || filterValue.isBlank()) {
            return true;
        }

        return actualValue != null && actualValue.equalsIgnoreCase(filterValue.trim());
    }

    private String defaultIfBlank(String value) {
        return value == null || value.isBlank() ? NOT_AVAILABLE : value.trim();
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private int calculateRewardPoints(BigDecimal netOrderAmount) {
        if (netOrderAmount == null || netOrderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        return netOrderAmount
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN)
                .intValue();
    }

    private String calculateCustomerSegment(BigDecimal netOrderAmount, int totalOrders) {
        if (totalOrders == 0 || netOrderAmount == null || netOrderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return "No Purchase";
        }

        if (netOrderAmount.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            return "High Value";
        }

        if (netOrderAmount.compareTo(BigDecimal.valueOf(2000)) >= 0) {
            return "Medium Value";
        }

        return "Low Value";
    }

    private int calculateDataQualityScore(List<String> warnings) {
        if (warnings == null || warnings.isEmpty()) {
            return 100;
        }

        int score = 100;

        for (String warning : warnings) {
            if (warning.contains("profile")) {
                score -= 30;
            } else if (warning.contains("orders")) {
                score -= 20;
            } else if (warning.contains("preference")) {
                score -= 20;
            } else {
                score -= 10;
            }
        }

        return Math.max(score, 0);
    }

    private String calculateDataQualityStatus(int score) {
        if (score >= 90) {
            return "Complete";
        }

        if (score >= 60) {
            return "Partial";
        }

        return "Incomplete";
    }
}