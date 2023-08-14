package io.alikian.springoauth2;

import io.github.alikian.LocalstackManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Configuration
@Profile({"test", "local"})
public class AwsConfig {

    LocalstackManager localstackManager;

    public AwsConfig() {
        localstackManager = LocalstackManager.builder()
                .withSimpleCloudformation("simple-cloudformation.yaml").buildSimple();
    }

    @Primary
    @Bean
    public DynamoDbClient getDynamoDbClient() {
        return localstackManager.getDynamoDbClient();
    }

    public SecretsManagerClient getSecretsManagerClient(){
        return localstackManager.getSecretsManagerClient();
    }

    @Primary
    @Bean
    public DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getDynamoDbClient())
                .build();
    }
}
