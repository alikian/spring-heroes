package com.example.springoauth2;

import com.alikian.LocalstackManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@Profile({"test","local"})
public class AwsConfig {
    @Primary
    @Bean
    public DynamoDbClient getDynamoDbClient() {
        return LocalstackManager.getInstance().getAwsClientBuilder().getDynamoDbClient();
    }

    @Primary
    @Bean
    public DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getDynamoDbClient())
                .build();
    }
}
