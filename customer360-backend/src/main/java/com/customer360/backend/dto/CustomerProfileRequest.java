package com.customer360.backend.dto;

public class CustomerProfileRequest {

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

    public CustomerProfileRequest() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCity() {
        return city;
    }

    public String getGender() {
        return gender;
    }

    public Integer getAge() {
        return age;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
}
