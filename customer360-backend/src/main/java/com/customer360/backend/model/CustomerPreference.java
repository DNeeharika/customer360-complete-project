package com.customer360.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPreference {

    private String customerId;

    private String membership;

    private String preferredChannel;
}