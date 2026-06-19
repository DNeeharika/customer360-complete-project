package com.customer360.backend.controller;

import com.customer360.backend.dto.CustomerDetailResponse;
import com.customer360.backend.dto.CustomerPageResponse;
import com.customer360.backend.dto.CustomerSummaryResponse;
import com.customer360.backend.service.CustomerConsolidationService;
import com.customer360.backend.service.CustomerSummaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
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

    @GetMapping("/page")
    public CustomerPageResponse getCustomersPage(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String membership,
            @RequestParam(required = false) String preferredChannel,
            @RequestParam(defaultValue = "customerId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return customerConsolidationService.getCustomersPage(
                search,
                city,
                membership,
                preferredChannel,
                sortBy,
                sortDir,
                page,
                size
        );
    }

    @GetMapping
    public List<CustomerDetailResponse> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String membership,
            @RequestParam(required = false) String preferredChannel,
            @RequestParam(defaultValue = "customerId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
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

    @GetMapping("/{customerId}/summary")
    public CustomerSummaryResponse generateCustomerSummary(@PathVariable String customerId) {
        return customerSummaryService.generateSummary(customerId);
    }

    @GetMapping("/{customerId}")
    public CustomerDetailResponse getCustomerDetails(@PathVariable String customerId) {
        return customerConsolidationService.getCustomerDetails(customerId);
    }
}
