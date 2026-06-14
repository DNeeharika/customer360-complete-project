package com.customer360.backend.service;

import com.customer360.backend.dto.CustomerDetailResponse;
import com.customer360.backend.dto.CustomerSummaryResponse;
import org.springframework.stereotype.Service;

@Service
public class CustomerSummaryService {

    private static final String NOT_AVAILABLE = "Not Available";

    private final CustomerConsolidationService customerConsolidationService;
    private final AiSummaryService aiSummaryService;

    public CustomerSummaryService(
            CustomerConsolidationService customerConsolidationService,
            AiSummaryService aiSummaryService
    ) {
        this.customerConsolidationService = customerConsolidationService;
        this.aiSummaryService = aiSummaryService;
    }

    public CustomerSummaryResponse generateSummary(String customerId) {
        CustomerDetailResponse customerDetail =
                customerConsolidationService.getCustomerDetails(customerId);

        String summary;

        try {
            summary = aiSummaryService.generateAiSummary(customerDetail);
        } catch (Exception ex) {
            summary = generateRuleBasedSummary(customerDetail);
        }

        return new CustomerSummaryResponse(customerId, summary);
    }

    private String generateRuleBasedSummary(CustomerDetailResponse customerDetail) {
        StringBuilder summary = new StringBuilder();

        if (isNotAvailable(customerDetail.getName()) || isNotAvailable(customerDetail.getCity())) {
            summary.append("Customer profile details are not available for customer ID ")
                    .append(customerDetail.getCustomerId())
                    .append(". ");
        } else {
            summary.append(customerDetail.getName())
                    .append(" is a customer from ")
                    .append(customerDetail.getCity())
                    .append(". ");
        }

        if (customerDetail.getTotalOrders() > 0) {
            summary.append("The customer has placed ")
                    .append(customerDetail.getTotalOrders())
                    .append(customerDetail.getTotalOrders() == 1 ? " order" : " orders")
                    .append(" with a total order value of ")
                    .append(customerDetail.getTotalOrderAmount())
                    .append(". ");
        } else {
            summary.append("No order history is available. ");
        }

        if (isNotAvailable(customerDetail.getMembership())) {
            summary.append("Membership information is not available. ");
        } else {
            summary.append("Membership status is ")
                    .append(customerDetail.getMembership())
                    .append(". ");
        }

        if (isNotAvailable(customerDetail.getPreferredChannel())) {
            summary.append("Preferred communication channel is not available. ");
        } else {
            summary.append("Preferred communication channel is ")
                    .append(customerDetail.getPreferredChannel())
                    .append(". ");
        }

        if (customerDetail.getWarnings() != null && !customerDetail.getWarnings().isEmpty()) {
            summary.append("Data quality note: ")
                    .append(String.join("; ", customerDetail.getWarnings()));
        }

        return summary.toString().trim();
    }

    private boolean isNotAvailable(String value) {
        return value == null || value.isBlank() || NOT_AVAILABLE.equalsIgnoreCase(value);
    }
}