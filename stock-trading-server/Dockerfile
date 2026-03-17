FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy server pom.xml and source (relative to context)
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081 6565
ENTRYPOINT ["java", "-jar", "app.jar"]













