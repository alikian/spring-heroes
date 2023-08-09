package com.alikian;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.BootstrapRegistryInitializer;
import org.yaml.snakeyaml.Yaml;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class LocalstackBootstrapConfiguration implements BootstrapRegistryInitializer {
    Yaml yaml = new Yaml();

    @Override
    public void initialize(BootstrapRegistry registry) {
        String profile = System.getProperty("spring.profiles.active", "unknown");
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("localstack.yaml")) {
            LocalstackConfig localstackConfig = yaml.loadAs(input, LocalstackConfig.class);
            if(localstackConfig.getActivateOnProfiles().contains(profile)) {
                LocalstackManager localstackManager= LocalstackManager.getInstance();
                SecretsManagerClient secretsClient = localstackManager.getAwsClientBuilder().getSecretsManagerClient();
                registry.register(SecretsManagerClient.class, context -> {
                    return secretsClient;
                });

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
