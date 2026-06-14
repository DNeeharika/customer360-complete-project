package com.customer360.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaskingServiceTest {

    private MaskingService maskingService;

    @BeforeEach
    void setUp() {
        maskingService = new MaskingService();
    }

    @Test
    void shouldMaskEmailSuccessfully() {
        String result = maskingService.maskEmail("rahul.sharma@gmail.com");

        assertEquals("r***********@gmail.com", result);
    }

    @Test
    void shouldMaskMobileSuccessfully() {
        String result = maskingService.maskMobile("9876543210");

        assertEquals("9876******", result);
    }

    @Test
    void shouldReturnNotAvailableForNullEmail() {
        String result = maskingService.maskEmail(null);

        assertEquals("Not Available", result);
    }

    @Test
    void shouldReturnNotAvailableForBlankEmail() {
        String result = maskingService.maskEmail("");

        assertEquals("Not Available", result);
    }

    @Test
    void shouldReturnNotAvailableForInvalidEmail() {
        String result = maskingService.maskEmail("invalid-email");

        assertEquals("Not Available", result);
    }

    @Test
    void shouldReturnNotAvailableForNullMobile() {
        String result = maskingService.maskMobile(null);

        assertEquals("Not Available", result);
    }

    @Test
    void shouldReturnNotAvailableForBlankMobile() {
        String result = maskingService.maskMobile("");

        assertEquals("Not Available", result);
    }

    @Test
    void shouldMaskShortMobileCompletely() {
        String result = maskingService.maskMobile("123");

        assertEquals("***", result);
    }
}