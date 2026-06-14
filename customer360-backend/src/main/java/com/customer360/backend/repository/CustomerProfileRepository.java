package com.customer360.backend.repository;

import com.customer360.backend.model.CustomerProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerProfileRepository extends MongoRepository<CustomerProfile, String> {

    Optional<CustomerProfile> findByCustomerId(String customerId);
}