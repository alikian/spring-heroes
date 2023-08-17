package io.example.springoauth2;

import io.alikian.springoauth2.Hero;
import io.alikian.springoauth2.HeroService;
import io.alikian.springoauth2.SpringOauth2Application;
import io.github.alikian.LocalstackManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {SpringOauth2Application.class},
        properties = {
                "spring.cloud.aws.credentials.access-key=noop",
                "spring.cloud.aws.credentials.secret-key=noop",
                "spring.cloud.aws.endpoint=http://localhost:4566/",
                "spring.cloud.aws.region.static=us-west-2"
        })
@ActiveProfiles("test")
class SpringOauth2ApplicationTests {

    @Autowired
    HeroService heroService;

    @BeforeAll
    public static void setup() {
        LocalstackManager localstackManager = LocalstackManager.builder()
                .withSimpleCloudformation("simple-cloudformation.yaml").buildSimple();
    }

    @Test
    void saveAndReadTest() {
        Assertions.assertNotNull(heroService);
        Hero savedHero = heroService.saveHero(new Hero("Ali"));
        Hero readHero = heroService.getHero(savedHero.getId());
        Assertions.assertEquals(savedHero.getName(), readHero.getName());
    }

    @Test
    void deleteTest() {
        Assertions.assertNotNull(heroService);
        Hero savedHero = heroService.saveHero(new Hero("Ali"));
        heroService.deleteHero(savedHero.getId());
        Hero readHero = heroService.getHero(savedHero.getId());
        Assertions.assertNull(readHero);
    }

}

