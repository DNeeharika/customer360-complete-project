package com.customer360.backend.controller;

import com.customer360.backend.dto.CustomerDetailResponse;
import com.customer360.backend.dto.CustomerSummaryResponse;
import com.customer360.backend.service.CustomerConsolidationService;
import com.customer360.backend.service.CustomerSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Customer APIs", description = "APIs for customer search, details, and summary")
public class CustomerController {

    private final CustomerConsolidationService customerConsolidationService;
    private final CustomerSummaryService customerSummaryService;

    public CustomerController(
            CustomerConsolidationService customerConsolidationService,
            CustomerSummaryService customerSummaryService
    ) {
        this.customerConsolidationService = customerConsolidationService;
        this.customerSummaryService = customerSummaryService;
    }

    @GetMapping("/api/customers")
    @Operation(
            summary = "Get all consolidated customers",
            description = "Returns consolidated customer data by combining MongoDB profile, CSV order, and JSON preference data. Supports search, filtering, and sorting."
    )
    public List<CustomerDetailResponse> getAllCustomers(
            @RequestParam(required = false)
            @Parameter(description = "Search by customer ID, name, city, membership, or preferred channel")
            String search,

            @RequestParam(required = false)
            @Parameter(description = "Filter customers by city")
            String city,

            @RequestParam(required = false)
            @Parameter(description = "Filter customers by membership")
            String membership,

            @RequestParam(required = false)
            @Parameter(description = "Filter customers by preferred communication channel")
            String preferredChannel,

            @RequestParam(required = false, defaultValue = "customerId")
            @Parameter(description = "Sort field: customerId, name, city, membership, preferredChannel, totalOrders, totalOrderAmount")
            String sortBy,

            @RequestParam(required = false, defaultValue = "asc")
            @Parameter(description = "Sort direction: asc or desc")
            String sortDir
    ) {
        return customerConsolidationService.getAllCustomers(
                search,
                city,
                membership,
                preferredChannel,
                sortBy,
                sortDir
        );
    }

    @GetMapping("/api/customers/{customerId}")
    @Operation(
            summary = "Get customer details by customer ID",
            description = "Returns a consolidated customer profile including profile details, orders, total order amount, membership, preferred channel, and data quality warnings."
    )
    public CustomerDetailResponse getCustomerDetails(
            @PathVariable
            @Parameter(description = "Unique customer ID", example = "C1001")
            String customerId
    ) {
        return customerConsolidationService.getCustomerDetails(customerId);
    }

    @GetMapping("/api/customers/{customerId}/summary")
    @Operation(
            summary = "Get customer summary",
            description = "Generates a customer summary based on consolidated customer profile, order, and preference data."
    )
    public CustomerSummaryResponse getCustomerSummary(
            @PathVariable
            @Parameter(description = "Unique customer ID", example = "C1001")
            String customerId
    ) {
        return customerSummaryService.generateSummary(customerId);
    }
}
