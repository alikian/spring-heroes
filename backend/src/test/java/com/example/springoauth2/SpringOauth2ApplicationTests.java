package com.example.springoauth2;

import com.alikian.LocalstackBootstrapConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "org.springframework.boot.BootstrapRegistryInitializer=com.alikian.LocalstackBootstrapConfiguration")
@ActiveProfiles("test")
class SpringOauth2ApplicationTests {

//	@Test
//	void contextLoads() {
//	}

}

