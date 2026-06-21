package com.customer360.backend.loader;

import com.customer360.backend.dto.DataUploadResponse;
import com.customer360.backend.model.CustomerOrder;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderCsvLoader {

    private static final Logger logger = LoggerFactory.getLogger(OrderCsvLoader.class);

    private static final String DEFAULT_ORDER_FILE_PATH = "data/customer_orders.csv";
    private static final String UPLOAD_FILE_NAME = "customer_orders.csv";

    private final DataCache dataCache;

    public OrderCsvLoader(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    @PostConstruct
    public void loadOrders() {
        try {
            Path uploadedFilePath = getUploadedFilePath();

            if (Files.exists(uploadedFilePath)) {
                logger.info("Loading customer orders from persistent uploaded file: {}", uploadedFilePath);
                loadOrdersFromInputStream(
                        Files.newInputStream(uploadedFilePath),
                        "Persistent Uploaded CSV: " + uploadedFilePath
                );
                return;
            }

            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream(DEFAULT_ORDER_FILE_PATH);

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Customer orders CSV file not found: " + DEFAULT_ORDER_FILE_PATH
                );
            }

            logger.info("Loading customer orders from default resource file: {}", DEFAULT_ORDER_FILE_PATH);
            loadOrdersFromInputStream(inputStream, "Default CSV: " + DEFAULT_ORDER_FILE_PATH);

        } catch (Exception ex) {
            logger.error("Failed to load customer orders CSV file.", ex);
            throw new IllegalStateException("Failed to load customer orders CSV file.", ex);
        }
    }

    public DataUploadResponse reloadOrdersFromUpload(
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
                    "Uploaded orders CSV saved successfully. Original file: {}, Saved file: {}",
                    fileName,
                    uploadedFilePath
            );

            return loadOrdersFromInputStream(
                    Files.newInputStream(uploadedFilePath),
                    "Uploaded CSV: " + uploadedFilePath
            );

        } catch (Exception ex) {
            logger.error("Failed to persist uploaded orders CSV file.", ex);
            throw new IllegalStateException("Failed to persist uploaded orders CSV file.", ex);
        }
    }

    private DataUploadResponse loadOrdersFromInputStream(
            InputStream inputStream,
            String sourceName
    ) {
        Map<String, List<CustomerOrder>> ordersByCustomerId = new HashMap<>();

        int loadedRows = 0;
        int skippedRows = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        )) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.isBlank()) {
                    continue;
                }

                String[] columns = line.split(",", -1);

                if (columns.length < 8) {
                    skippedRows++;
                    logger.warn(
                            "Invalid CSV row skipped. Expected 8 columns but found {}. Row: {}",
                            columns.length,
                            line
                    );
                    continue;
                }

                try {
                    String customerId = clean(columns[0]);
                    String orderId = clean(columns[1]);
                    LocalDate orderDate = LocalDate.parse(clean(columns[2]));
                    BigDecimal amount = parseAmount(clean(columns[3]));
                    String productCategory = clean(columns[4]);
                    String orderStatus = clean(columns[5]);
                    String paymentMode = clean(columns[6]);
                    BigDecimal discountAmount = parseDiscountAmount(clean(columns[7]));

                    if (customerId.isBlank() || orderId.isBlank()) {
                        skippedRows++;
                        logger.warn("Invalid CSV row skipped. customerId/orderId is blank. Row: {}", line);
                        continue;
                    }

                    if (amount.compareTo(BigDecimal.ZERO) < 0) {
                        skippedRows++;
                        logger.warn("Invalid CSV row skipped. Negative amount is not allowed. Row: {}", line);
                        continue;
                    }

                    if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
                        skippedRows++;
                        logger.warn("Invalid CSV row skipped. Negative discount amount is not allowed. Row: {}", line);
                        continue;
                    }

                    if (discountAmount.compareTo(amount) > 0) {
                        skippedRows++;
                        logger.warn("Invalid CSV row skipped. Discount amount cannot be greater than order amount. Row: {}", line);
                        continue;
                    }

                    CustomerOrder order = new CustomerOrder();
                    order.setCustomerId(customerId);
                    order.setOrderId(orderId);
                    order.setOrderDate(orderDate);
                    order.setAmount(amount);
                    order.setProductCategory(defaultIfBlank(productCategory));
                    order.setOrderStatus(defaultIfBlank(orderStatus));
                    order.setPaymentMode(defaultIfBlank(paymentMode));
                    order.setDiscountAmount(discountAmount);

                    ordersByCustomerId
                            .computeIfAbsent(customerId, key -> new ArrayList<>())
                            .add(order);

                    loadedRows++;

                } catch (Exception ex) {
                    skippedRows++;
                    logger.warn("Invalid CSV data skipped. Row: {}", line);
                }
            }

            dataCache.setOrdersByCustomerId(ordersByCustomerId);

            logger.info(
                    "Customer orders CSV loaded successfully from {}. Loaded rows: {}, Skipped rows: {}",
                    sourceName,
                    loadedRows,
                    skippedRows
            );

            return new DataUploadResponse(
                    "ORDERS_CSV",
                    sourceName,
                    loadedRows,
                    skippedRows,
                    "Orders data refreshed and saved successfully."
            );

        } catch (Exception ex) {
            logger.error("Failed to load customer orders CSV file from {}.", sourceName, ex);
            throw new IllegalStateException("Failed to load customer orders CSV file.", ex);
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

    private BigDecimal parseAmount(String value) {
        if (value == null || value.isBlank()) {
            throw new NumberFormatException("Amount is blank");
        }

        return new BigDecimal(value);
    }

    private BigDecimal parseDiscountAmount(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(value);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultIfBlank(String value) {
        return value == null || value.isBlank() ? "Not Available" : value.trim();
    }
}
