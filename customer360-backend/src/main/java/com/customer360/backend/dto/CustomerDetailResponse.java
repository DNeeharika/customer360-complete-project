package com.customer360.backend.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CustomerDetailResponse {

    private String customerId;
    private String name;
    private String email;
    private String mobile;
    private String city;

    private String gender;
    private Integer age;
    private String dateOfBirth;
    private String address;
    private String customerType;

    private String membership;
    private String preferredChannel;
    private String preferredLanguage;
    private Boolean notificationOptIn;
    private Boolean marketingConsent;
    private String preferredContactTime;

    private List<OrderResponse> orders = new ArrayList<>();
    private int totalOrders;
    private BigDecimal totalOrderAmount;
    private BigDecimal totalDiscountAmount;
    private BigDecimal netOrderAmount;

    private int rewardPoints;
    private String customerSegment;
    private int dataQualityScore;
    private String dataQualityStatus;

    private List<String> warnings = new ArrayList<>();

    public CustomerDetailResponse() {
    }

    // Old constructor kept for backward compatibility with existing tests/code
    public CustomerDetailResponse(
            String customerId,
            String name,
            String email,
            String mobile,
            String city,
            String membership,
            String preferredChannel,
            List<OrderResponse> orders,
            int totalOrders,
            BigDecimal totalOrderAmount,
            List<String> warnings
    ) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.city = city;

        this.gender = "Not Available";
        this.age = null;
        this.dateOfBirth = "Not Available";
        this.address = "Not Available";
        this.customerType = "Not Available";

        this.membership = membership;
        this.preferredChannel = preferredChannel;
        this.preferredLanguage = "Not Available";
        this.notificationOptIn = false;
        this.marketingConsent = false;
        this.preferredContactTime = "Not Available";

        this.orders = orders != null ? orders : new ArrayList<>();
        this.totalOrders = totalOrders;
        this.totalOrderAmount = totalOrderAmount != null ? totalOrderAmount : BigDecimal.ZERO;
        this.totalDiscountAmount = BigDecimal.ZERO;
        this.netOrderAmount = this.totalOrderAmount;

        this.rewardPoints = 0;
        this.customerSegment = "Not Available";
        this.dataQualityScore = 100;
        this.dataQualityStatus = "Complete";

        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }

    public CustomerDetailResponse(
            String customerId,
            String name,
            String email,
            String mobile,
            String city,
            String gender,
            Integer age,
            String dateOfBirth,
            String address,
            String customerType,
            String membership,
            String preferredChannel,
            String preferredLanguage,
            Boolean notificationOptIn,
            Boolean marketingConsent,
            String preferredContactTime,
            List<OrderResponse> orders,
            int totalOrders,
            BigDecimal totalOrderAmount,
            BigDecimal totalDiscountAmount,
            BigDecimal netOrderAmount,
            int rewardPoints,
            String customerSegment,
            int dataQualityScore,
            String dataQualityStatus,
            List<String> warnings
    ) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.city = city;
        this.gender = gender;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.customerType = customerType;
        this.membership = membership;
        this.preferredChannel = preferredChannel;
        this.preferredLanguage = preferredLanguage;
        this.notificationOptIn = notificationOptIn;
        this.marketingConsent = marketingConsent;
        this.preferredContactTime = preferredContactTime;
        this.orders = orders != null ? orders : new ArrayList<>();
        this.totalOrders = totalOrders;
        this.totalOrderAmount = totalOrderAmount != null ? totalOrderAmount : BigDecimal.ZERO;
        this.totalDiscountAmount = totalDiscountAmount != null ? totalDiscountAmount : BigDecimal.ZERO;
        this.netOrderAmount = netOrderAmount != null ? netOrderAmount : BigDecimal.ZERO;
        this.rewardPoints = rewardPoints;
        this.customerSegment = customerSegment;
        this.dataQualityScore = dataQualityScore;
        this.dataQualityStatus = dataQualityStatus;
        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getPreferredChannel() {
        return preferredChannel;
    }

    public void setPreferredChannel(String preferredChannel) {
        this.preferredChannel = preferredChannel;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Boolean getNotificationOptIn() {
        return notificationOptIn;
    }

    public void setNotificationOptIn(Boolean notificationOptIn) {
        this.notificationOptIn = notificationOptIn;
    }

    public Boolean getMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(Boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

    public String getPreferredContactTime() {
        return preferredContactTime;
    }

    public void setPreferredContactTime(String preferredContactTime) {
        this.preferredContactTime = preferredContactTime;
    }

    public List<OrderResponse> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponse> orders) {
        this.orders = orders;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    public BigDecimal getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public BigDecimal getNetOrderAmount() {
        return netOrderAmount;
    }

    public void setNetOrderAmount(BigDecimal netOrderAmount) {
        this.netOrderAmount = netOrderAmount;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public String getCustomerSegment() {
        return customerSegment;
    }

    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
    }

    public int getDataQualityScore() {
        return dataQualityScore;
    }

    public void setDataQualityScore(int dataQualityScore) {
        this.dataQualityScore = dataQualityScore;
    }

    public String getDataQualityStatus() {
        return dataQualityStatus;
    }

    public void setDataQualityStatus(String dataQualityStatus) {
        this.dataQualityStatus = dataQualityStatus;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}