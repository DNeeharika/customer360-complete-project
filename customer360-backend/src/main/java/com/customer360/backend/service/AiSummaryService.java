package com.customer360.backend.service;

import com.customer360.backend.dto.CustomerDetailResponse;
import com.customer360.backend.dto.OrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiSummaryService {

    private static final Logger logger = LoggerFactory.getLogger(AiSummaryService.class);

    private final RestClient restClient;

    @Value("${ai.summary.enabled:false}")
    private boolean aiSummaryEnabled;

    @Value("${ai.ollama.url:http://localhost:11434/api/generate}")
    private String ollamaUrl;

    @Value("${ai.ollama.model:llama3.2}")
    private String ollamaModel;

    public AiSummaryService() {
        this.restClient = RestClient.create();
    }

    public String generateAiSummary(CustomerDetailResponse customer) {
        if (!aiSummaryEnabled) {
            throw new IllegalStateException("AI summary is disabled.");
        }

        String prompt = buildPrompt(customer);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ollamaModel);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);

        try {
            Map response = restClient.post()
                    .uri(ollamaUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null || response.get("response") == null) {
                throw new IllegalStateException("Empty response received from Ollama.");
            }

            String aiResponse = cleanAiResponse(response.get("response").toString());

            if (aiResponse.isBlank()) {
                throw new IllegalStateException("Blank summary received from Ollama.");
            }

            logger.info("AI summary generated successfully for customerId: {}", customer.getCustomerId());
            return aiResponse;

        } catch (Exception ex) {
            logger.warn(
                    "AI summary generation failed for customerId: {}. Falling back to rule-based summary.",
                    customer.getCustomerId(),
                    ex
            );
            throw ex;
        }
    }

    private String cleanAiResponse(String aiResponse) {
        if (aiResponse == null) {
            return "";
        }

        String cleaned = aiResponse.trim();

        cleaned = cleaned.replaceFirst("(?i)^here is a concise customer summary in 3 to 4 sentences:\\s*", "");
        cleaned = cleaned.replaceFirst("(?i)^here is a concise customer summary:\\s*", "");
        cleaned = cleaned.replaceFirst("(?i)^here is the customer summary:\\s*", "");
        cleaned = cleaned.replaceFirst("(?i)^customer summary:\\s*", "");
        cleaned = cleaned.replaceFirst("(?i)^summary:\\s*", "");

        cleaned = cleaned.replace("\"", "");
        cleaned = cleaned.replace("[preferred channel]", "");
        cleaned = cleaned.replace("[", "");
        cleaned = cleaned.replace("]", "");

        return cleaned.trim();
    }

    private String buildPrompt(CustomerDetailResponse customer) {
        String ordersText = buildOrdersText(customer);
        String warnings = buildWarningsText(customer);

        return """
                You are an AI assistant for a Customer360 application.

                Write only the final customer summary.
                Do not add any introduction.
                Do not say "Here is".
                Do not use placeholders.
                Do not use square brackets.
                Do not wrap the response in quotation marks.
                Do not use bullet points.

                Generate a concise, professional customer summary in exactly 3 sentences.

                Rules:
                - Use Indian Rupees symbol ₹ for all monetary values.
                - Do not use dollar symbol.
                - Do not mention masked email or masked mobile.
                - Do not expose sensitive data.
                - Use the exact preferred channel, preferred language, and contact time given in the customer data.
                - Mention customer profile, customer type, order activity, net order value, reward points, customer segment, membership, preferred channel, and data quality status.
                - Mention data quality warnings only if warnings are present.
                - Keep the tone business-friendly and factual.
                - Do not invent loyalty, trust, risk, satisfaction, or business importance unless directly supported by the input data.
                - Do not use words like valued, loyal, trust, strong history, important customer, consistent purchase history, or satisfactory unless explicitly present in the input data.
                - Use only the facts provided in the customer data.

                Customer Profile:
                Customer ID: %s
                Name: %s
                City: %s
                Gender: %s
                Age: %s
                Date of Birth: %s
                Customer Type: %s

                Preference Data:
                Membership: %s
                Preferred Channel: %s
                Preferred Language: %s
                Notification Opt-In: %s
                Marketing Consent: %s
                Preferred Contact Time: %s

                Business Metrics:
                Total Orders: %d
                Total Order Amount: ₹%s
                Total Discount Amount: ₹%s
                Net Order Amount: ₹%s
                Reward Points: %d
                Customer Segment: %s
                Data Quality Score: %d
                Data Quality Status: %s

                Orders:
                %s

                Data Quality Warnings:
                %s
                """.formatted(
                safe(customer.getCustomerId()),
                safe(customer.getName()),
                safe(customer.getCity()),
                safe(customer.getGender()),
                customer.getAge() != null ? customer.getAge().toString() : "Not Available",
                safe(customer.getDateOfBirth()),
                safe(customer.getCustomerType()),

                safe(customer.getMembership()),
                safe(customer.getPreferredChannel()),
                safe(customer.getPreferredLanguage()),
                formatBoolean(customer.getNotificationOptIn()),
                formatBoolean(customer.getMarketingConsent()),
                safe(customer.getPreferredContactTime()),

                customer.getTotalOrders(),
                safe(customer.getTotalOrderAmount()),
                safe(customer.getTotalDiscountAmount()),
                safe(customer.getNetOrderAmount()),
                customer.getRewardPoints(),
                safe(customer.getCustomerSegment()),
                customer.getDataQualityScore(),
                safe(customer.getDataQualityStatus()),

                ordersText,
                warnings
        );
    }

    private String buildOrdersText(CustomerDetailResponse customer) {
        StringBuilder ordersText = new StringBuilder();

        if (customer.getOrders() != null && !customer.getOrders().isEmpty()) {
            for (OrderResponse order : customer.getOrders()) {
                ordersText.append("- Order ID: ")
                        .append(safe(order.getOrderId()))
                        .append(", Date: ")
                        .append(safe(order.getOrderDate()))
                        .append(", Amount: ₹")
                        .append(safe(order.getAmount()))
                        .append(", Discount: ₹")
                        .append(safe(order.getDiscountAmount()))
                        .append(", Net Amount: ₹")
                        .append(safe(order.getNetAmount()))
                        .append(", Product Category: ")
                        .append(safe(order.getProductCategory()))
                        .append(", Order Status: ")
                        .append(safe(order.getOrderStatus()))
                        .append(", Payment Mode: ")
                        .append(safe(order.getPaymentMode()))
                        .append("\n");
            }
        } else {
            ordersText.append("No orders available.\n");
        }

        return ordersText.toString();
    }

    private String buildWarningsText(CustomerDetailResponse customer) {
        if (customer.getWarnings() == null || customer.getWarnings().isEmpty()) {
            return "No data quality warnings.";
        }

        return String.join("; ", customer.getWarnings());
    }

    private String safe(Object value) {
        if (value == null) {
            return "Not Available";
        }

        String text = value.toString();

        if (text.isBlank()) {
            return "Not Available";
        }

        return text;
    }

    private String formatBoolean(Boolean value) {
        return Boolean.TRUE.equals(value) ? "Yes" : "No";
    }
}
