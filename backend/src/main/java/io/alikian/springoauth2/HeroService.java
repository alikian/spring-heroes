package io.alikian.springoauth2;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.UUID;

@Service
public class HeroService {

    DynamoDbTemplate dynamoDbTemplate;

    public HeroService(@Autowired DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbTemplate = new DynamoDbTemplate(dynamoDbEnhancedClient);
    }

    public Hero saveHero(Hero hero) {
        hero.setId(UUID.randomUUID().toString());
        return dynamoDbTemplate.save(hero);
    }

    public Hero getHero(String keyVal) {
        AttributeValue attributeValue = AttributeValue.builder().n(keyVal).build();
        Key key = Key.builder().partitionValue(attributeValue).build();
        return dynamoDbTemplate.load(key, Hero.class);
    }

    public void deleteHero(String keyVal) {
        AttributeValue attributeValue = AttributeValue.builder().s(keyVal).build();
        Key key = Key.builder().partitionValue(attributeValue).build();
        dynamoDbTemplate.delete(key, Hero.class);
    }

    public void updateHero(Hero hero) {
        dynamoDbTemplate.update(hero);
    }

    public List<Hero> getAllHeroes() {
        PageIterable<Hero> pageIterable= dynamoDbTemplate.scanAll(Hero.class);
        return pageIterable.items().stream().toList();
    }

//    public List<Hero> findHeroes(String name){
//        QueryEnhancedRequest queryEnhancedRequest =
//                QueryEnhancedRequest.builder().build();
//        dynamoDbTemplate.query();
//    }
}
