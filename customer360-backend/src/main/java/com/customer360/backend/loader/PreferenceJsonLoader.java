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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
public class PreferenceJsonLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PreferenceJsonLoader.class);

    private static final String DEFAULT_PREFERENCE_FILE_PATH = "data/customer_preferences.json";
    private static final String UPLOAD_FILE_NAME = "customer_preferences.json";

    private final DataCache dataCache;

    public PreferenceJsonLoader(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    @Override
    public void run(String... args) {
        try {
            Path uploadedFilePath = getUploadedFilePath();

            if (Files.exists(uploadedFilePath)) {
                logger.info("Loading customer preferences from persistent uploaded file: {}", uploadedFilePath);
                loadPreferencesFromInputStream(
                        Files.newInputStream(uploadedFilePath),
                        "Persistent Uploaded JSON: " + uploadedFilePath
                );
                return;
            }

            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream(DEFAULT_PREFERENCE_FILE_PATH);

            if (inputStream == null) {
                logger.error("customer_preferences.json file not found.");
                return;
            }

            logger.info("Loading customer preferences from default resource file: {}", DEFAULT_PREFERENCE_FILE_PATH);
            loadPreferencesFromInputStream(
                    inputStream,
                    "Default JSON: " + DEFAULT_PREFERENCE_FILE_PATH
            );

        } catch (Exception ex) {
            logger.error("Error while loading customer preferences JSON file.", ex);
        }
    }

    public DataUploadResponse reloadPreferencesFromUpload(
            InputStream inputStream,
            String fileName
    ) {
        try {
            Path uploadedFilePath = getUploadedFilePath();

            Files.createDirectories(uploadedFilePath.getParent());

            Files.copy(
                    inputStream,
                    uploadedFilePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            logger.info(
                    "Uploaded preferences JSON saved successfully. Original file: {}, Saved file: {}",
                    fileName,
                    uploadedFilePath
            );

            return loadPreferencesFromInputStream(
                    Files.newInputStream(uploadedFilePath),
                    "Uploaded JSON: " + uploadedFilePath
            );

        } catch (Exception ex) {
            logger.error("Failed to persist uploaded preferences JSON file.", ex);
            throw new IllegalStateException("Failed to persist uploaded preferences JSON file.", ex);
        }
    }

    public DataUploadResponse resetPreferencesToDefault() {
        try {
            Path uploadedFilePath = getUploadedFilePath();

            if (Files.exists(uploadedFilePath)) {
                Files.delete(uploadedFilePath);
                logger.info("Deleted persistent uploaded preferences JSON file: {}", uploadedFilePath);
            }

            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream(DEFAULT_PREFERENCE_FILE_PATH);

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Default customer preferences JSON file not found: " + DEFAULT_PREFERENCE_FILE_PATH
                );
            }

            DataUploadResponse response = loadPreferencesFromInputStream(
                    inputStream,
                    "Default JSON after reset: " + DEFAULT_PREFERENCE_FILE_PATH
            );

            response.setMessage("Preferences data reset to default JSON successfully.");
            return response;

        } catch (Exception ex) {
            logger.error("Failed to reset preferences data to default JSON.", ex);
            throw new IllegalStateException("Failed to reset preferences data to default JSON.", ex);
        }
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
                    "Preferences data refreshed and saved successfully."
            );

        } catch (Exception ex) {
            logger.error("Error while loading customer preferences JSON from {}", sourceName, ex);
            throw new IllegalStateException("Failed to load customer preferences JSON.", ex);
        }
    }

    private Path getUploadedFilePath() {
        return getUploadDirectory().resolve(UPLOAD_FILE_NAME);
    }

    private Path getUploadDirectory() {
        Path currentDirectory = Paths.get(System.getProperty("user.dir"))
                .toAbsolutePath()
                .normalize();

        Path backendDirectoryFromRoot = currentDirectory.resolve("customer360-backend");

        if (Files.exists(backendDirectoryFromRoot)) {
            return backendDirectoryFromRoot.resolve("uploads");
        }

        return currentDirectory.resolve("uploads");
    }
}
