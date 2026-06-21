package com.customer360.backend.controller;

import com.customer360.backend.dto.DataUploadResponse;
import com.customer360.backend.loader.DataCache;
import com.customer360.backend.loader.OrderCsvLoader;
import com.customer360.backend.loader.PreferenceJsonLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/data")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDataController {

    private final OrderCsvLoader orderCsvLoader;
    private final PreferenceJsonLoader preferenceJsonLoader;
    private final DataCache dataCache;

    public AdminDataController(
            OrderCsvLoader orderCsvLoader,
            PreferenceJsonLoader preferenceJsonLoader,
            DataCache dataCache
    ) {
        this.orderCsvLoader = orderCsvLoader;
        this.preferenceJsonLoader = preferenceJsonLoader;
        this.dataCache = dataCache;
    }

    @PostMapping(
            value = "/upload/orders",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public DataUploadResponse uploadOrdersCsv(
            @RequestPart("file") MultipartFile file
    ) {
        validateFile(file, ".csv", "Orders file must be a CSV file.");

        try {
            return orderCsvLoader.reloadOrdersFromUpload(
                    file.getInputStream(),
                    file.getOriginalFilename()
            );
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unable to process orders CSV file."
            );
        }
    }

    @PostMapping(
            value = "/upload/preferences",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public DataUploadResponse uploadPreferencesJson(
            @RequestPart("file") MultipartFile file
    ) {
        validateFile(file, ".json", "Preferences file must be a JSON file.");

        try {
            return preferenceJsonLoader.reloadPreferencesFromUpload(
                    file.getInputStream(),
                    file.getOriginalFilename()
            );
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unable to process preferences JSON file."
            );
        }
    }

    @GetMapping("/status")
    public Map<String, Object> getDataStatus() {
        return Map.of(
                "ordersCustomerCount", dataCache.getOrderCustomerCount(),
                "preferencesCustomerCount", dataCache.getPreferenceCustomerCount(),
                "message", "Dynamic CSV and JSON cache is active."
        );
    }

    @PostMapping("/reset-defaults")
    public Map<String, Object> resetToDefaultData() {
        try {
            DataUploadResponse ordersResponse = orderCsvLoader.resetOrdersToDefault();
            DataUploadResponse preferencesResponse = preferenceJsonLoader.resetPreferencesToDefault();

            return Map.of(
                    "message", "Default CSV and JSON data restored successfully.",
                    "orders", ordersResponse,
                    "preferences", preferencesResponse,
                    "ordersCustomerCount", dataCache.getOrderCustomerCount(),
                    "preferencesCustomerCount", dataCache.getPreferenceCustomerCount()
            );

        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to reset data to default files."
            );
        }
    }

    private void validateFile(
            MultipartFile file,
            String expectedExtension,
            String errorMessage
    ) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is required."
            );
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null
                || !fileName.toLowerCase().endsWith(expectedExtension)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errorMessage
            );
        }
    }
}
