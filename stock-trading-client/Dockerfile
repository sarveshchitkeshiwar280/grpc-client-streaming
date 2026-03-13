FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy API JAR and POM from the client's lib folder (relative to root)
COPY stock-trading-client/lib/stock-trading-api-0.0.1-SNAPSHOT.jar /root/.m2/repository/com/javatechie/stock-trading-api/0.0.1-SNAPSHOT/
COPY stock-trading-client/lib/stock-trading-api-0.0.1-SNAPSHOT.pom /root/.m2/repository/com/javatechie/stock-trading-api/0.0.1-SNAPSHOT/

# Copy client pom.xml and source
COPY stock-trading-client/pom.xml .
COPY stock-trading-client/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]