# Read Me First
The following was discovered as part of building this project:

* The JVM level was changed from '1.8' to '17', review the [JDK Version Range](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions#jdk-version-range) on the wiki for more details.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.1/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#web)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Build Docker image using Spring boot
```
mvn spring-boot:build-image -Dspring-boot.build-image.imageName=alikian/spring-oauth2
```

### Run Docker image
```
docker run -p 8080:8080 -t alikian/spring-oauth2
```

### Pushing Docker image Docker Hub

Login:
```
% docker login 
Username: codeburps
Password: 
```

Push:
```
docker push alikian/spring-oauth2  
```

### AWS ECR

```
aws ecr get-login-password --region us-west-2 --profile alikian | docker login --username AWS --password-stdin 495224360068.dkr.ecr.us-west-2.amazonaws.com
```

Identify the local image to push. Run the docker images command to list the container images on your system.
```
docker images
```

The following example tags a local image with the ID e9ae3c220b23 as aws_account_id.dkr.ecr.us-west-2.amazonaws.com/my-repository:tag.
```
docker tag dc4b18206d42 495224360068.dkr.ecr.us-west-2.amazonaws.com/spring-oauth2:1
```

```
docker push 495224360068.dkr.ecr.us-west-2.amazonaws.com/spring-oauth2:1
```
