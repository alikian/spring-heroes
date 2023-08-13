package com.alikian;

import io.github.alikian.LocalstackManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.BootstrapRegistryInitializer;
import org.yaml.snakeyaml.Yaml;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class LocalstackBootstrapConfiguration implements BootstrapRegistryInitializer {
    Yaml yaml = new Yaml();

    @Override
    public void initialize(BootstrapRegistry registry) {
        List<String> profiles = Arrays.asList("test", "local");
        String profile = System.getProperty("spring.profiles.active", "unknown");
        if (profiles.contains(profile)) {
            LocalstackManager localstackManager = LocalstackManager.builder().buildSimple();
            SecretsManagerClient secretsClient = localstackManager.getSecretsManagerClient();
            registry.register(SecretsManagerClient.class, context -> {
                return secretsClient;
            });

        }

    }
}
