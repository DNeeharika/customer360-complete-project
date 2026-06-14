package com.customer360.backend.service;

import com.customer360.backend.dto.CustomerDetailResponse;
import com.customer360.backend.dto.CustomerSummaryResponse;
import com.customer360.backend.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CustomerSummaryServiceTest {

    private CustomerConsolidationService customerConsolidationService;
    private AiSummaryService aiSummaryService;
    private CustomerSummaryService customerSummaryService;

    @BeforeEach
    void setUp() {
        customerConsolidationService = Mockito.mock(CustomerConsolidationService.class);
        aiSummaryService = Mockito.mock(AiSummaryService.class);
        customerSummaryService = new CustomerSummaryService(
                customerConsolidationService,
                aiSummaryService
        );
    }

    @Test
    void shouldGenerateFallbackSummaryForValidCustomerWhenAiFails() {
        CustomerDetailResponse customerDetail = new CustomerDetailResponse(
                "C1001",
                "Rahul Sharma",
                "r***********@gmail.com",
                "9876******",
                "Hyderabad",
                "Gold",
                "Email",
                List.of(
                        new OrderResponse("O1001", LocalDate.of(2026, 1, 10), new BigDecimal("2500")),
                        new OrderResponse("O1002", LocalDate.of(2026, 2, 15), new BigDecimal("1200"))
                ),
                2,
                new BigDecimal("3700"),
                List.of()
        );

        when(customerConsolidationService.getCustomerDetails("C1001")).thenReturn(customerDetail);
        when(aiSummaryService.generateAiSummary(customerDetail))
                .thenThrow(new RuntimeException("Ollama unavailable"));

        CustomerSummaryResponse response = customerSummaryService.generateSummary("C1001");

        assertEquals("C1001", response.getCustomerId());
        assertTrue(response.getSummary().contains("Rahul Sharma is a customer from Hyderabad."));
        assertTrue(response.getSummary().contains("The customer has placed 2 orders with a total order value of 3700."));
        assertTrue(response.getSummary().contains("Membership status is Gold."));
        assertTrue(response.getSummary().contains("Preferred communication channel is Email."));
    }

    @Test
    void shouldGenerateFallbackSummaryForCustomerWithMissingProfileWhenAiFails() {
        CustomerDetailResponse customerDetail = new CustomerDetailResponse(
                "C1006",
                "Not Available",
                "Not Available",
                "Not Available",
                "Not Available",
                "Gold",
                "Email",
                List.of(
                        new OrderResponse("O1006", LocalDate.of(2026, 4, 1), new BigDecimal("3000"))
                ),
                1,
                new BigDecimal("3000"),
                List.of("Customer profile not found in MongoDB.")
        );

        when(customerConsolidationService.getCustomerDetails("C1006")).thenReturn(customerDetail);
        when(aiSummaryService.generateAiSummary(customerDetail))
                .thenThrow(new RuntimeException("Ollama unavailable"));

        CustomerSummaryResponse response = customerSummaryService.generateSummary("C1006");

        assertEquals("C1006", response.getCustomerId());
        assertTrue(response.getSummary().contains("Customer profile details are not available for customer ID C1006."));
        assertTrue(response.getSummary().contains("The customer has placed 1 order with a total order value of 3000."));
        assertTrue(response.getSummary().contains("Membership status is Gold."));
        assertTrue(response.getSummary().contains("Preferred communication channel is Email."));
        assertTrue(response.getSummary().contains("Data quality note: Customer profile not found in MongoDB."));
    }

    @Test
    void shouldGenerateFallbackSummaryForCustomerWithNoOrdersWhenAiFails() {
        CustomerDetailResponse customerDetail = new CustomerDetailResponse(
                "C1007",
                "Not Available",
                "Not Available",
                "Not Available",
                "Not Available",
                "Bronze",
                "Phone",
                List.of(),
                0,
                BigDecimal.ZERO,
                List.of(
                        "Customer profile not found in MongoDB.",
                        "No orders found in CSV file."
                )
        );

        when(customerConsolidationService.getCustomerDetails("C1007")).thenReturn(customerDetail);
        when(aiSummaryService.generateAiSummary(customerDetail))
                .thenThrow(new RuntimeException("Ollama unavailable"));

        CustomerSummaryResponse response = customerSummaryService.generateSummary("C1007");

        assertEquals("C1007", response.getCustomerId());

        String summary = response.getSummary();

        assertTrue(summary.contains("C1007"));
        assertTrue(summary.contains("Bronze"));
        assertTrue(summary.contains("Phone"));
        assertTrue(summary.contains("Customer profile not found in MongoDB."));
        assertTrue(summary.contains("No orders found in CSV file."));
    }

    @Test
    void shouldGenerateFallbackSummaryForCustomerWithMissingPreferenceWhenAiFails() {
        CustomerDetailResponse customerDetail = new CustomerDetailResponse(
                "C1004",
                "Sneha Reddy",
                "s**********@gmail.com",
                "9876******",
                "Hyderabad",
                "Not Available",
                "Not Available",
                List.of(
                        new OrderResponse("O1007", LocalDate.of(2026, 4, 10), BigDecimal.ZERO)
                ),
                1,
                BigDecimal.ZERO,
                List.of("Customer preference not found in JSON file.")
        );

        when(customerConsolidationService.getCustomerDetails("C1004")).thenReturn(customerDetail);
        when(aiSummaryService.generateAiSummary(customerDetail))
                .thenThrow(new RuntimeException("Ollama unavailable"));

        CustomerSummaryResponse response = customerSummaryService.generateSummary("C1004");

        assertEquals("C1004", response.getCustomerId());
        assertTrue(response.getSummary().contains("Sneha Reddy is a customer from Hyderabad."));
        assertTrue(response.getSummary().contains("The customer has placed 1 order with a total order value of 0."));
        assertTrue(response.getSummary().contains("Membership information is not available."));
        assertTrue(response.getSummary().contains("Preferred communication channel is not available."));
        assertTrue(response.getSummary().contains("Data quality note: Customer preference not found in JSON file."));
    }

    @Test
    void shouldReturnAiSummaryWhenAiServiceWorks() {
        CustomerDetailResponse customerDetail = new CustomerDetailResponse(
                "C1001",
                "Rahul Sharma",
                "r***********@gmail.com",
                "9876******",
                "Hyderabad",
                "Gold",
                "Email",
                List.of(
                        new OrderResponse("O1001", LocalDate.of(2026, 1, 10), new BigDecimal("2500")),
                        new OrderResponse("O1002", LocalDate.of(2026, 2, 15), new BigDecimal("1200"))
                ),
                2,
                new BigDecimal("3700"),
                List.of()
        );

        String aiSummary = "Rahul Sharma is a Gold customer from Hyderabad with two orders totaling 3700.";

        when(customerConsolidationService.getCustomerDetails("C1001")).thenReturn(customerDetail);
        when(aiSummaryService.generateAiSummary(customerDetail)).thenReturn(aiSummary);

        CustomerSummaryResponse response = customerSummaryService.generateSummary("C1001");

        assertEquals("C1001", response.getCustomerId());
        assertEquals(aiSummary, response.getSummary());
    }
}
