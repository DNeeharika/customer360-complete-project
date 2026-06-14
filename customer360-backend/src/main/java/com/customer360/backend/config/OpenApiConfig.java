package com.customer360.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customer360OpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer360 Backend API")
                        .description("REST API for consolidating customer profile, order, and preference data from MongoDB, CSV, and JSON sources.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Neeharika D")));
    }
}