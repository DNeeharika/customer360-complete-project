package com.customer360.backend.config;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    private static final String DATABASE_NAME = "customer360_db";

    @Bean
    public MongoClient mongoClient() {

        String connectionString = System.getenv("MONGODB_URI");

        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalStateException("MONGODB_URI environment variable is not configured.");
        }

        return MongoClients.create(new ConnectionString(connectionString));
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), DATABASE_NAME);
    }
}