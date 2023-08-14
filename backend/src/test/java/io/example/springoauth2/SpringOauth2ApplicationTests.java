package io.example.springoauth2;

import io.alikian.springoauth2.AwsConfig;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@SpringBootTest(classes = AwsConfig.class)
@ActiveProfiles("test")
class SpringOauth2ApplicationTests {

	@Autowired
	DynamoDbClient dynamoDbClient;

	@Test
	void contextLoads() {
		Assert.assertNotNull(dynamoDbClient);
	}

}

