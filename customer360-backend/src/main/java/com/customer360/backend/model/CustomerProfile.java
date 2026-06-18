package com.customer360.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customer_profile")
public class CustomerProfile {

    @Id
    private String id;

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

    public CustomerProfile() {
    }

    // Old constructor kept for backward compatibility with existing tests
    public CustomerProfile(
            String id,
            String customerId,
            String name,
            String email,
            String mobile,
            String city
    ) {
        this.id = id;
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
    }

    // New constructor with enriched customer profile fields
    public CustomerProfile(
            String id,
            String customerId,
            String name,
            String email,
            String mobile,
            String city,
            String gender,
            Integer age,
            String dateOfBirth,
            String address,
            String customerType
    ) {
        this.id = id;
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
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
