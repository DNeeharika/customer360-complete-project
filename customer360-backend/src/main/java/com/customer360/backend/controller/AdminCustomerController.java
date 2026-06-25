package com.customer360.backend.controller;

import com.customer360.backend.dto.CustomerProfileRequest;
import com.customer360.backend.model.CustomerProfile;
import com.customer360.backend.repository.CustomerProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    private final CustomerProfileRepository customerProfileRepository;

    public AdminCustomerController(CustomerProfileRepository customerProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createCustomerProfile(
            @RequestBody CustomerProfileRequest request
    ) {
        validateRequiredCustomerFields(request, true);

        String customerId = clean(request.getCustomerId());

        customerProfileRepository.findByCustomerId(customerId)
                .ifPresent(existingCustomer -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Customer profile already exists for customerId: " + customerId
                    );
                });

        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setCustomerId(customerId);

        applyRequestToProfile(customerProfile, request);

        CustomerProfile savedProfile = customerProfileRepository.save(customerProfile);

        return Map.of(
                "message", "Customer profile created successfully.",
                "customerId", savedProfile.getCustomerId()
        );
    }

    @PutMapping("/{customerId}")
    public Map<String, Object> updateCustomerProfile(
            @PathVariable String customerId,
            @RequestBody CustomerProfileRequest request
    ) {
        String cleanCustomerId = clean(customerId);

        if (cleanCustomerId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "customerId is required."
            );
        }

        CustomerProfile existingProfile = customerProfileRepository
                .findByCustomerId(cleanCustomerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer profile not found for customerId: " + cleanCustomerId
                ));

        validateRequiredCustomerFields(request, false);
        applyRequestToProfile(existingProfile, request);

        CustomerProfile savedProfile = customerProfileRepository.save(existingProfile);

        return Map.of(
                "message", "Customer profile updated successfully.",
                "customerId", savedProfile.getCustomerId()
        );
    }

    @DeleteMapping("/{customerId}")
    public Map<String, Object> deleteCustomerProfile(
            @PathVariable String customerId
    ) {
        String cleanCustomerId = clean(customerId);

        if (cleanCustomerId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "customerId is required."
            );
        }

        CustomerProfile existingProfile = customerProfileRepository
                .findByCustomerId(cleanCustomerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer profile not found for customerId: " + cleanCustomerId
                ));

        customerProfileRepository.delete(existingProfile);

        return Map.of(
                "message", "Customer profile deleted successfully.",
                "customerId", cleanCustomerId
        );
    }

    private void validateRequiredCustomerFields(
            CustomerProfileRequest request,
            boolean validateCustomerId
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Request body is required."
            );
        }

        if (validateCustomerId && clean(request.getCustomerId()).isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "customerId is required."
            );
        }

        if (clean(request.getName()).isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "name is required."
            );
        }

        if (clean(request.getEmail()).isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "email is required."
            );
        }

        if (clean(request.getMobile()).isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "mobile is required."
            );
        }

        if (clean(request.getCity()).isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "city is required."
            );
        }
    }

    private void applyRequestToProfile(
            CustomerProfile customerProfile,
            CustomerProfileRequest request
    ) {
        customerProfile.setName(clean(request.getName()));
        customerProfile.setEmail(clean(request.getEmail()));
        customerProfile.setMobile(clean(request.getMobile()));
        customerProfile.setCity(clean(request.getCity()));
        customerProfile.setGender(defaultIfBlank(request.getGender()));
        customerProfile.setAge(request.getAge());
        customerProfile.setDateOfBirth(defaultIfBlank(request.getDateOfBirth()));
        customerProfile.setAddress(defaultIfBlank(request.getAddress()));
        customerProfile.setCustomerType(defaultIfBlank(request.getCustomerType()));
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultIfBlank(String value) {
        return value == null || value.isBlank() ? "Not Available" : value.trim();
    }
}
