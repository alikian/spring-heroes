tag=$(date "+%s")
echo imagetag: $tag
cd ~/git/spring-heroes/
cd frontend
ng build
rm ~/git/spring-heroes/backend/src/main/resources/static/*
cp ~/git/spring-heroes/frontend/dist/heroes-app/* ~/git/spring-heroes/backend/src/main/resources/static/
cd ~/git/spring-heroes/backend/
echo build Docker Image spring-oauth2:$tag
#mvn spring-boot:build-image -Dspring-boot.build-image.imageName=alikian/spring-oauth2:$tag
mvn clean install
docker buildx build --platform=linux/amd64 . -t alikian/spring-oauth2:$tag
docker push alikian/spring-oauth2:$tag
cd ~/git/spring-heroes/aws/ecs_java/
mvn package
cdk deploy --parameters dockerImageName=alikian/spring-oauth2:$tag --parameters secretManagerName=/HeroesSecrets --parameters dynamoDBName=hero
cd ~/git/spring-heroes/