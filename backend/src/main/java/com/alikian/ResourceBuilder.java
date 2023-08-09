package com.alikian;

import com.alikian.aws.DynamoDBProperties;
import com.alikian.aws.SecretManagerProperties;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResourceBuilder {
    ObjectMapper objectMapper;

    Map cloudFormation;
    LocalstackClientBuilder localstackClientBuilder;

    ResourceBuilder(Map cloudFormation,
                    LocalstackClientBuilder localstackClientBuilder) {
        this.cloudFormation = cloudFormation;
        this.localstackClientBuilder = localstackClientBuilder;
        objectMapper = new ObjectMapper();
//      Use this if all properties are not in the class
//      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

    }

    void buildResources() {
        Map<String, Object> resources = (Map<String, Object>) cloudFormation.get("Resources");
        for (Map.Entry<String, Object> entry : resources.entrySet()) {

            Map resource = (Map) entry.getValue();
            String type = (String) resource.get("Type");
            Map properties = (Map) resource.get("Properties");

            switch (type) {
                case "AWS::SecretsManager::Secret":
                    SecretManagerProperties secretManagerProperties
                            = objectMapper.convertValue(properties, SecretManagerProperties.class);
                    createSecrets(secretManagerProperties);
                    break;
                case "AWS::DynamoDB::Table":
                    DynamoDBProperties dynamoDBProperties
                            = objectMapper.convertValue(properties, DynamoDBProperties.class);
                    createDynamoDBs(dynamoDBProperties);
                    break;
            }
        }
    }

    private void createDynamoDBs(DynamoDBProperties ddbProperties) {
        CreateTableRequest.Builder requestBuilder = CreateTableRequest.builder();
        requestBuilder.tableName(ddbProperties.getTableName());
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        for (DynamoDBProperties.AttrNameType attrNameType : ddbProperties.getAttributeDefinitions()) {
            attributeDefinitions.add(AttributeDefinition.builder()
                    .attributeName(attrNameType.getAttributeName())
                    .attributeType(attrNameType.getAttributeType())
                    .build());
        }
        requestBuilder.attributeDefinitions(attributeDefinitions);

        List<KeySchemaElement> keySchemaElements = new ArrayList<>();
        for (DynamoDBProperties.AttrNameKeyType attrNameKeyType : ddbProperties.getKeySchema()) {
            keySchemaElements.add(KeySchemaElement.builder()
                    .attributeName(attrNameKeyType.getAttributeName())
                    .keyType(attrNameKeyType.getKeyType())
                    .build());
        }
        requestBuilder.keySchema(keySchemaElements);

        requestBuilder.provisionedThroughput(ProvisionedThroughput.builder()
                .readCapacityUnits(
                        Long.valueOf(ddbProperties.getProvisionedThroughput().getReadCapacityUnits()))
                .writeCapacityUnits(
                        Long.valueOf(ddbProperties.getProvisionedThroughput().getWriteCapacityUnits()))

                .build());
        List<GlobalSecondaryIndex> globalSecondaryIndices = createGSI(ddbProperties.getGlobalSecondaryIndexes());

        requestBuilder.globalSecondaryIndexes(globalSecondaryIndices);
        requestBuilder.build();
        localstackClientBuilder.getDynamoDbClient().createTable(requestBuilder.build());
    }

    private List<GlobalSecondaryIndex> createGSI(List<DynamoDBProperties.GlobalSecondaryIndex> globalSecondaryIndexes) {
        List<GlobalSecondaryIndex> globalSecondaryIndicesAws=new ArrayList<>();
        for (DynamoDBProperties.GlobalSecondaryIndex globalSecondaryIndex : globalSecondaryIndexes) {
            List<KeySchemaElement> keySchemaElementsGsi = new ArrayList<>();
            for (DynamoDBProperties.AttrNameKeyType attrNameKeyType : globalSecondaryIndex.getKeySchema()) {
                keySchemaElementsGsi.add(KeySchemaElement.builder()
                        .keyType(attrNameKeyType.getKeyType())
                        .attributeName(attrNameKeyType.getAttributeName()).build());
            }
            DynamoDBProperties.ProvisionedThroughput provisionedThroughput =
                    globalSecondaryIndex.getProvisionedThroughput();
            ProvisionedThroughput provisionedThroughputAWS= ProvisionedThroughput.builder()
                    .writeCapacityUnits(Long.valueOf(provisionedThroughput.getWriteCapacityUnits()))
                    .readCapacityUnits(Long.valueOf(provisionedThroughput.getReadCapacityUnits()))
                    .build();
            GlobalSecondaryIndex  globalSecondaryIndexAws= GlobalSecondaryIndex.builder()
                    .indexName(globalSecondaryIndex.getIndexName())
                    .keySchema(keySchemaElementsGsi)
                    .provisionedThroughput(provisionedThroughputAWS)
                    .projection(Projection.builder()
                            .projectionType(globalSecondaryIndex.getProjection().getProjectionType()).build())
                    .build();

            globalSecondaryIndicesAws.add(globalSecondaryIndexAws);
        }
        return globalSecondaryIndicesAws;
    }

    void createSecrets(SecretManagerProperties secretManagerProperties) {
        CreateSecretRequest createSecretRequest =
                CreateSecretRequest.builder()
                        .secretString(secretManagerProperties.getSecretString())
                        .name(secretManagerProperties.getName()).build();
        SecretsManagerClient secretsManagerClient = localstackClientBuilder.getSecretsManagerClient();
        secretsManagerClient.createSecret(createSecretRequest);
    }
}
