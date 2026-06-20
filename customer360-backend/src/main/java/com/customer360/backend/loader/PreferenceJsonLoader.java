package com.customer360.backend.loader;

import com.customer360.backend.dto.DataUploadResponse;
import com.customer360.backend.model.CustomerPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
public class PreferenceJsonLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PreferenceJsonLoader.class);

    private static final String DEFAULT_PREFERENCE_FILE_PATH = "data/customer_preferences.json";

    private final DataCache dataCache;

    public PreferenceJsonLoader(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    @Override
    public void run(String... args) {
        try {
            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream(DEFAULT_PREFERENCE_FILE_PATH);

            if (inputStream == null) {
                logger.error("customer_preferences.json file not found.");
                return;
            }

            loadPreferencesFromInputStream(
                    inputStream,
                    "Default JSON: " + DEFAULT_PREFERENCE_FILE_PATH
            );

        } catch (Exception ex) {
            logger.error("Error while loading default customer_preferences.json", ex);
        }
    }

    public DataUploadResponse reloadPreferencesFromUpload(
            InputStream inputStream,
            String fileName
    ) {
        return loadPreferencesFromInputStream(inputStream, "Uploaded JSON: " + fileName);
    }

    private DataUploadResponse loadPreferencesFromInputStream(
            InputStream inputStream,
            String sourceName
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            List<CustomerPreference> preferences = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<CustomerPreference>>() {}
            );

            Map<String, CustomerPreference> preferencesByCustomerId = new HashMap<>();

            int loadedCount = 0;
            int skippedCount = 0;

            for (CustomerPreference preference : preferences) {
                if (preference.getCustomerId() == null
                        || preference.getCustomerId().isBlank()) {
                    logger.warn("Invalid preference skipped because customerId is missing.");
                    skippedCount++;
                    continue;
                }

                preferencesByCustomerId.put(preference.getCustomerId(), preference);
                loadedCount++;
            }

            dataCache.setPreferencesByCustomerId(preferencesByCustomerId);

            logger.info(
                    "Customer preferences JSON loaded successfully from {}. Loaded rows: {}, Skipped rows: {}",
                    sourceName,
                    loadedCount,
                    skippedCount
            );

            return new DataUploadResponse(
                    "PREFERENCES_JSON",
                    sourceName,
                    loadedCount,
                    skippedCount,
                    "Preferences data refreshed successfully."
            );

        } catch (Exception ex) {
            logger.error("Error while loading customer preferences JSON from {}", sourceName, ex);
            throw new IllegalStateException("Failed to load customer preferences JSON.", ex);
        }
    }
}
