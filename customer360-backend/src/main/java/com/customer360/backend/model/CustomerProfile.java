package com.customer360.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_profile")
public class CustomerProfile {

    @Id
    private String id;

    private String customerId;

    private String name;

    private String email;

    private String mobile;

    private String city;
}