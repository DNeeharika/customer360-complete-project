package com.customer360.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CustomerOrder {

    private String customerId;
    private String orderId;
    private LocalDate orderDate;
    private BigDecimal amount;

    private String productCategory;
    private String orderStatus;
    private String paymentMode;
    private BigDecimal discountAmount;

    public CustomerOrder() {
    }

    // Old constructor kept for backward compatibility with existing tests
    public CustomerOrder(String customerId, String orderId, LocalDate orderDate, BigDecimal amount) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.amount = amount;
        this.productCategory = "Not Available";
        this.orderStatus = "Completed";
        this.paymentMode = "Not Available";
        this.discountAmount = BigDecimal.ZERO;
    }

    // New constructor with enriched order fields
    public CustomerOrder(
            String customerId,
            String orderId,
            LocalDate orderDate,
            BigDecimal amount,
            String productCategory,
            String orderStatus,
            String paymentMode,
            BigDecimal discountAmount
    ) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.amount = amount;
        this.productCategory = productCategory;
        this.orderStatus = orderStatus;
        this.paymentMode = paymentMode;
        this.discountAmount = discountAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
