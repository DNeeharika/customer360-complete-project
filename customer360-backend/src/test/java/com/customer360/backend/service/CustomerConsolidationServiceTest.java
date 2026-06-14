package com.customer360.backend.service;

import com.customer360.backend.dto.CustomerDetailResponse;
import com.customer360.backend.loader.DataCache;
import com.customer360.backend.model.CustomerOrder;
import com.customer360.backend.model.CustomerPreference;
import com.customer360.backend.model.CustomerProfile;
import com.customer360.backend.repository.CustomerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CustomerConsolidationServiceTest {

    private CustomerProfileRepository customerProfileRepository;
    private DataCache dataCache;
    private MaskingService maskingService;
    private CustomerConsolidationService customerConsolidationService;

    @BeforeEach
    void setUp() {
        customerProfileRepository = Mockito.mock(CustomerProfileRepository.class);
        dataCache = new DataCache();
        maskingService = new MaskingService();

        customerConsolidationService = new CustomerConsolidationService(
                customerProfileRepository,
                dataCache,
                maskingService
        );

        loadTestOrders();
        loadTestPreferences();
        mockCustomerProfiles();
    }

    @Test
    void shouldGetCustomerDetailsWithOrdersPreferenceAndTotalAmount() {
        CustomerDetailResponse response = customerConsolidationService.getCustomerDetails("C1001");

        assertEquals("C1001", response.getCustomerId());
        assertEquals("Rahul Sharma", response.getName());
        assertEquals("Hyderabad", response.getCity());
        assertEquals("Gold", response.getMembership());
        assertEquals("Email", response.getPreferredChannel());
        assertEquals(2, response.getTotalOrders());
        assertEquals(new BigDecimal("3700"), response.getTotalOrderAmount());
        assertEquals(2, response.getOrders().size());
        assertTrue(response.getWarnings().isEmpty());

        assertEquals("r***********@gmail.com", response.getEmail());
        assertEquals("9876******", response.getMobile());
    }

    @Test
    void shouldReturnWarningWhenPreferenceIsMissing() {
        CustomerDetailResponse response = customerConsolidationService.getCustomerDetails("C1004");

        assertEquals("C1004", response.getCustomerId());
        assertEquals("Sneha Reddy", response.getName());
        assertEquals("Hyderabad", response.getCity());
        assertEquals("Not Available", response.getMembership());
        assertEquals("Not Available", response.getPreferredChannel());
        assertEquals(1, response.getTotalOrders());
        assertEquals(BigDecimal.ZERO, response.getTotalOrderAmount());
        assertTrue(response.getWarnings().contains("Customer preference not found in JSON file."));
    }

    @Test
    void shouldReturnWarningWhenProfileIsMissing() {
        CustomerDetailResponse response = customerConsolidationService.getCustomerDetails("C1006");

        assertEquals("C1006", response.getCustomerId());
        assertEquals("Not Available", response.getName());
        assertEquals("Not Available", response.getCity());
        assertEquals("Gold", response.getMembership());
        assertEquals("Email", response.getPreferredChannel());
        assertEquals(1, response.getTotalOrders());
        assertEquals(new BigDecimal("3000"), response.getTotalOrderAmount());
        assertTrue(response.getWarnings().contains("Customer profile not found in MongoDB."));
    }

    @Test
    void shouldReturnAllCustomersFromMongoDbProfiles() {
        List<CustomerDetailResponse> customers = customerConsolidationService.getAllCustomers(
                null,
                null,
                null,
                null,
                "customerId",
                "asc"
        );

        assertEquals(4, customers.size());
        assertEquals("C1001", customers.get(0).getCustomerId());
        assertEquals("C1002", customers.get(1).getCustomerId());
        assertEquals("C1003", customers.get(2).getCustomerId());
        assertEquals("C1004", customers.get(3).getCustomerId());
    }

    @Test
    void shouldSearchCustomersByName() {
        List<CustomerDetailResponse> customers = customerConsolidationService.getAllCustomers(
                "rahul",
                null,
                null,
                null,
                "customerId",
                "asc"
        );

        assertEquals(1, customers.size());
        assertEquals("C1001", customers.get(0).getCustomerId());
        assertEquals("Rahul Sharma", customers.get(0).getName());
    }

    @Test
    void shouldFilterCustomersByCity() {
        List<CustomerDetailResponse> customers = customerConsolidationService.getAllCustomers(
                null,
                "Hyderabad",
                null,
                null,
                "customerId",
                "asc"
        );

        assertEquals(2, customers.size());
        assertEquals("C1001", customers.get(0).getCustomerId());
        assertEquals("C1004", customers.get(1).getCustomerId());
    }

    @Test
    void shouldFilterCustomersByMembership() {
        List<CustomerDetailResponse> customers = customerConsolidationService.getAllCustomers(
                null,
                null,
                "Gold",
                null,
                "customerId",
                "asc"
        );

        assertEquals(1, customers.size());
        assertEquals("C1001", customers.get(0).getCustomerId());
    }

    @Test
    void shouldSortCustomersByTotalOrderAmountDescending() {
        List<CustomerDetailResponse> customers = customerConsolidationService.getAllCustomers(
                null,
                null,
                null,
                null,
                "totalOrderAmount",
                "desc"
        );

        assertEquals("C1002", customers.get(0).getCustomerId());
        assertEquals(new BigDecimal("5600"), customers.get(0).getTotalOrderAmount());

        assertEquals("C1001", customers.get(1).getCustomerId());
        assertEquals(new BigDecimal("3700"), customers.get(1).getTotalOrderAmount());
    }

    @Test
    void shouldThrowExceptionForInvalidSortBy() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerConsolidationService.getAllCustomers(
                        null,
                        null,
                        null,
                        null,
                        "wrongField",
                        "asc"
                )
        );

        assertEquals("Invalid sortBy value: wrongField", exception.getMessage());
    }

    private void loadTestOrders() {
        dataCache.addOrder(new CustomerOrder(
                "C1001",
                "O1001",
                LocalDate.of(2026, 1, 10),
                new BigDecimal("2500")
        ));

        dataCache.addOrder(new CustomerOrder(
                "C1001",
                "O1002",
                LocalDate.of(2026, 2, 15),
                new BigDecimal("1200")
        ));

        dataCache.addOrder(new CustomerOrder(
                "C1002",
                "O1003",
                LocalDate.of(2026, 1, 20),
                new BigDecimal("5600")
        ));

        dataCache.addOrder(new CustomerOrder(
                "C1004",
                "O1007",
                LocalDate.of(2026, 4, 10),
                BigDecimal.ZERO
        ));

        dataCache.addOrder(new CustomerOrder(
                "C1006",
                "O1006",
                LocalDate.of(2026, 4, 1),
                new BigDecimal("3000")
        ));
    }

    private void loadTestPreferences() {
        dataCache.addPreference(new CustomerPreference(
                "C1001",
                "Gold",
                "Email"
        ));

        dataCache.addPreference(new CustomerPreference(
                "C1002",
                "Silver",
                "SMS"
        ));

        dataCache.addPreference(new CustomerPreference(
                "C1003",
                "Platinum",
                "WhatsApp"
        ));

        dataCache.addPreference(new CustomerPreference(
                "C1006",
                "Gold",
                "Email"
        ));
    }

    private void mockCustomerProfiles() {
        CustomerProfile customer1 = new CustomerProfile(
                "1",
                "C1001",
                "Rahul Sharma",
                "rahul.sharma@gmail.com",
                "9876543210",
                "Hyderabad"
        );

        CustomerProfile customer2 = new CustomerProfile(
                "2",
                "C1002",
                "Priya Mehta",
                "priya.mehta@gmail.com",
                "9123456780",
                "Mumbai"
        );

        CustomerProfile customer3 = new CustomerProfile(
                "3",
                "C1003",
                "Amit Verma",
                "amit.verma@gmail.com",
                "9988776655",
                "Bengaluru"
        );

        CustomerProfile customer4 = new CustomerProfile(
                "4",
                "C1004",
                "Sneha Reddy",
                "sneha.reddy@gmail.com",
                "9876501234",
                "Hyderabad"
        );

        when(customerProfileRepository.findAll()).thenReturn(
                List.of(customer1, customer2, customer3, customer4)
        );

        when(customerProfileRepository.findByCustomerId("C1001")).thenReturn(Optional.of(customer1));
        when(customerProfileRepository.findByCustomerId("C1002")).thenReturn(Optional.of(customer2));
        when(customerProfileRepository.findByCustomerId("C1003")).thenReturn(Optional.of(customer3));
        when(customerProfileRepository.findByCustomerId("C1004")).thenReturn(Optional.of(customer4));
        when(customerProfileRepository.findByCustomerId("C1006")).thenReturn(Optional.empty());
    }
}