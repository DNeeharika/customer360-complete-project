package com.customer360.backend.loader;

import com.customer360.backend.model.CustomerPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Component
@Order(2)
public class PreferenceJsonLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PreferenceJsonLoader.class);

    private final DataCache dataCache;

    public PreferenceJsonLoader(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    @Override
    public void run(String... args) {
        loadPreferences();
    }

    private void loadPreferences() {
        try {
            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("data/customer_preferences.json");

            if (inputStream == null) {
                logger.error("customer_preferences.json file not found.");
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();

            List<CustomerPreference> preferences = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<CustomerPreference>>() {}
            );

            int loadedCount = 0;
            int skippedCount = 0;

            for (CustomerPreference preference : preferences) {
                if (preference.getCustomerId() == null || preference.getCustomerId().isBlank()) {
                    logger.warn("Invalid preference skipped because customerId is missing.");
                    skippedCount++;
                    continue;
                }

                dataCache.addPreference(preference);
                loadedCount++;
            }

            logger.info("Customer preferences JSON loaded successfully. Loaded rows: {}, Skipped rows: {}",
                    loadedCount,
                    skippedCount
            );

        } catch (Exception ex) {
            logger.error("Error while loading customer_preferences.json", ex);
        }
    }
}