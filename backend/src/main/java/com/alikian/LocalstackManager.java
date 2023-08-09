package com.alikian;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.yaml.snakeyaml.Yaml;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.OnFailure;
import software.amazon.awssdk.services.cloudformation.waiters.CloudFormationWaiter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j

public class LocalstackManager {

    private LocalstackConfig localstackConfig;

    private LocalstackManager() {
        loadConfig();
        String profile = System.getProperty("spring.profiles.active", "unknown");
        if(localstackConfig.getActivateOnProfiles().contains(profile)) {
            startDocker();
            createResources();
        }
    }

    private LocalStackContainer localstack;
    private LocalstackClientBuilder localstackClientBuilder;

    public LocalstackClientBuilder getAwsClientBuilder() {
        return localstackClientBuilder;
    }

    private final static LocalstackManager instance = new LocalstackManager();

    public static LocalstackManager getInstance() {
        return instance;
    }

    private LocalStackContainer startDocker() {
        DockerImageName localstackImage = DockerImageName.parse(localstackConfig.imageName);

        localstack = new LocalStackContainer(localstackImage);
        localstack.start();
        localstackClientBuilder = new LocalstackClientBuilder(localstack);
        return localstack;
    }


    void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("localstack.yaml")) {
            localstackConfig = yaml.loadAs(input, LocalstackConfig.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void createResources() {
        Yaml yaml = new Yaml();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("cloudformation.yaml")) {
            Map cloudFormation = yaml.loadAs(input, Map.class);
            ResourceBuilder resourceBuilder = new ResourceBuilder(cloudFormation, localstackClientBuilder);
            resourceBuilder.buildResources();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createStack() {
        String stackName = "heroes";
        log.debug("creating stack");
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("cloudformation.yaml");
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            CloudFormationClient cfClient = localstackClientBuilder.getCfClient();
            CloudFormationWaiter waiter = cfClient.waiter();
            CreateStackRequest stackRequest = CreateStackRequest.builder()
                    .stackName(stackName)
                    .templateBody(template)
                    .onFailure(OnFailure.ROLLBACK)
                    .build();
            cfClient.createStack(stackRequest);
            DescribeStacksRequest stacksRequest = DescribeStacksRequest.builder()
                    .stackName(stackName)
                    .build();

            WaiterResponse<DescribeStacksResponse> waiterResponse = waiter.waitUntilStackCreateComplete(stacksRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println(stackName + " is ready");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
