FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY processing-service/target/processing-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","app.jar"]
