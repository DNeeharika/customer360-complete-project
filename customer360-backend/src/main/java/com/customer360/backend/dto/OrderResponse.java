package com.customer360.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderResponse {

    private String orderId;
    private String orderDate;
    private BigDecimal amount;

    private String productCategory;
    private String orderStatus;
    private String paymentMode;
    private BigDecimal discountAmount;
    private BigDecimal netAmount;

    public OrderResponse() {
    }

    // Old constructor with String orderDate
    public OrderResponse(String orderId, String orderDate, BigDecimal amount) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.amount = amount;
        this.productCategory = "Not Available";
        this.orderStatus = "Completed";
        this.paymentMode = "Not Available";
        this.discountAmount = BigDecimal.ZERO;
        this.netAmount = amount != null ? amount : BigDecimal.ZERO;
    }

    // Old constructor with LocalDate orderDate, required by existing tests
    public OrderResponse(String orderId, LocalDate orderDate, BigDecimal amount) {
        this.orderId = orderId;
        this.orderDate = orderDate != null ? orderDate.toString() : "Not Available";
        this.amount = amount;
        this.productCategory = "Not Available";
        this.orderStatus = "Completed";
        this.paymentMode = "Not Available";
        this.discountAmount = BigDecimal.ZERO;
        this.netAmount = amount != null ? amount : BigDecimal.ZERO;
    }

    // New constructor with enriched order response fields
    public OrderResponse(
            String orderId,
            String orderDate,
            BigDecimal amount,
            String productCategory,
            String orderStatus,
            String paymentMode,
            BigDecimal discountAmount,
            BigDecimal netAmount
    ) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.amount = amount;
        this.productCategory = productCategory;
        this.orderStatus = orderStatus;
        this.paymentMode = paymentMode;
        this.discountAmount = discountAmount;
        this.netAmount = netAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
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

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }
}
