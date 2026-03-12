FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY storage-service/target/storage-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082 9090
ENTRYPOINT ["java","-jar","app.jar"]
