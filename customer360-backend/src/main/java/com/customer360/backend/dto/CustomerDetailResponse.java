package com.customer360.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailResponse {

    private String customerId;

    private String name;

    private String email;

    private String mobile;

    private String city;

    private String membership;

    private String preferredChannel;

    private List<OrderResponse> orders;

    private int totalOrders;

    private BigDecimal totalOrderAmount;

    private List<String> warnings;
}