package com.alikian.aws;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;

public class SecretsManagers extends Resources {

    void create(SecretsManagerClient secretsManagerClient) {
        String name = (String) properties.get("Name");
        String secretString = (String) properties.get("SecretString");
        CreateSecretRequest createSecretRequest= CreateSecretRequest.builder().secretString(secretString).name(name).build();
        secretsManagerClient.createSecret(createSecretRequest);
    }
}
