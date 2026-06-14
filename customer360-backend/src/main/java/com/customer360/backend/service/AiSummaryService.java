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
            logger.warn("AI summary generation failed for customerId: {}. Falling back to rule-based summary.",
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

        cleaned = cleaned.replace("Here is a concise customer summary in 3 to 4 sentences:", "");
        cleaned = cleaned.replace("Here is a concise customer summary:", "");
        cleaned = cleaned.replace("Here is the customer summary:", "");
        cleaned = cleaned.replace("Customer summary:", "");
        cleaned = cleaned.replace("Customer Summary:", "");

        cleaned = cleaned.replace("\"", "");
        cleaned = cleaned.replace("[preferred channel]", "");

        return cleaned.trim();
    }

    private String buildPrompt(CustomerDetailResponse customer) {
        StringBuilder ordersText = new StringBuilder();

        if (customer.getOrders() != null && !customer.getOrders().isEmpty()) {
            for (OrderResponse order : customer.getOrders()) {
                ordersText.append("- Order ID: ")
                        .append(order.getOrderId())
                        .append(", Date: ")
                        .append(order.getOrderDate())
                        .append(", Amount: ₹")
                        .append(order.getAmount())
                        .append("\n");
            }
        } else {
            ordersText.append("No orders available.\n");
        }

        String warnings = customer.getWarnings() == null || customer.getWarnings().isEmpty()
                ? "No data quality warnings."
                : String.join("; ", customer.getWarnings());

        return """
                You are an AI assistant for a Customer360 application.
        
                Write only the final customer summary.
                Do not add any introduction.
                Do not say "Here is".
                Do not use placeholders.
                Do not use square brackets.
                Do not wrap the response in quotation marks.
                Do not use bullet points.
        
                Generate a concise, professional customer summary in 3 sentences.
        
                Rules:
                - Use Indian Rupees symbol ₹ for all monetary values.
                - Do not use dollar symbol.
                - Do not mention masked email or masked mobile.
                - Do not expose sensitive data.
                - Use the exact preferred channel given in the customer data.
                - Mention customer profile, order activity, membership, preferred channel, and data quality warnings if available.
                - Keep the tone business-friendly and factual.
                - Do not invent loyalty, trust, risk, or business importance unless it is directly supported by the data.
                - Do not use words like valued, loyal, trust, strong history, important customer, consistent purchase history, or satisfactory unless explicitly present in the input data.
                - Use only the facts provided in the customer data.
        
                Customer Data:
                Customer ID: %s
                Name: %s
                City: %s
                Membership: %s
                Preferred Channel: %s
                Total Orders: %d
                Total Order Amount: ₹%s
        
                Orders:
                %s
        
                Data Quality Warnings:
                %s
                """.formatted(
                        customer.getCustomerId(),
                        customer.getName(),
                        customer.getCity(),
                        customer.getMembership(),
                        customer.getPreferredChannel(),
                        customer.getTotalOrders(),
                        customer.getTotalOrderAmount(),
                        ordersText,
                        warnings
        );
    }
}