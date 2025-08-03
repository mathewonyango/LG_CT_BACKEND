# Dockerfile for backend

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/livinggoodsbackend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]




# # Build stage
# FROM maven:3.9-eclipse-temurin-17 AS builder
# WORKDIR /app
# COPY . .
# RUN mvn clean package -DskipTests

# # Runtime stage
# FROM openjdk:17-jdk-slim
# WORKDIR /app
# COPY --from=builder /app/target/livinggoodsbackend-0.0.1-SNAPSHOT.jar app.jar
# EXPOSE 9000
# ENTRYPOINT ["java", "-jar", "app.jar"]
