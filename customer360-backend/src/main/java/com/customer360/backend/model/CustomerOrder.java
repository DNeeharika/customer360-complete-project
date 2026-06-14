package com.customer360.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

    private String customerId;

    private String orderId;

    private LocalDate orderDate;

    private BigDecimal amount;
}