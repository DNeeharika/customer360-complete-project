package com.customer360.backend.service;

import org.springframework.stereotype.Service;

@Service
public class MaskingService {

    public String maskEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return "Not Available";
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 1) {
            return "*@" + domain;
        }

        return localPart.charAt(0) + "*".repeat(localPart.length() - 1) + "@" + domain;
    }

    public String maskMobile(String mobile) {
        if (mobile == null || mobile.isBlank()) {
            return "Not Available";
        }

        if (mobile.length() <= 4) {
            return "*".repeat(mobile.length());
        }

        return mobile.substring(0, 4) + "*".repeat(mobile.length() - 4);
    }
}