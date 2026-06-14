package com.customer360.backend.loader;

import com.customer360.backend.model.CustomerOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Order(1)
public class OrderCsvLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(OrderCsvLoader.class);

    private final DataCache dataCache;

    public OrderCsvLoader(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    @Override
    public void run(String... args) {
        loadOrders();
    }

    private void loadOrders() {
        try {
            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("data/customer_orders.csv");

            if (inputStream == null) {
                logger.error("customer_orders.csv file not found.");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            boolean isHeader = true;
            int loadedCount = 0;
            int skippedCount = 0;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] columns = line.split(",");

                if (columns.length != 4) {
                    logger.warn("Invalid CSV row skipped: {}", line);
                    skippedCount++;
                    continue;
                }

                try {
                    String customerId = columns[0].trim();
                    String orderId = columns[1].trim();
                    LocalDate orderDate = LocalDate.parse(columns[2].trim());
                    BigDecimal amount = new BigDecimal(columns[3].trim());

                    if (amount.compareTo(BigDecimal.ZERO) < 0) {
                        logger.warn("Negative amount row skipped: {}", line);
                        skippedCount++;
                        continue;
                    }

                    CustomerOrder order = new CustomerOrder(customerId, orderId, orderDate, amount);
                    dataCache.addOrder(order);
                    loadedCount++;

                } catch (Exception ex) {
                    logger.warn("Invalid CSV data skipped: {}", line);
                    skippedCount++;
                }
            }

            logger.info("Customer orders CSV loaded successfully. Loaded rows: {}, Skipped rows: {}",
                    loadedCount,
                    skippedCount
            );

        } catch (Exception ex) {
            logger.error("Error while loading customer_orders.csv", ex);
        }
    }
}