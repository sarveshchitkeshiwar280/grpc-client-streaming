FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy API JAR from client's lib into Maven repository
COPY stock-trading-client/lib/stock-trading-api-0.0.1-SNAPSHOT.jar /root/.m2/repository/com/javatechie/stock-trading-api/0.0.1-SNAPSHOT/
COPY stock-trading-client/lib/stock-trading-api-0.0.1-SNAPSHOT.pom /root/.m2/repository/com/javatechie/stock-trading-api/0.0.1-SNAPSHOT/

# Copy server pom.xml and source (with prefix because context is root)
COPY stock-trading-server/pom.xml .
COPY stock-trading-server/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081 6565
ENTRYPOINT ["java", "-jar", "app.jar"]












