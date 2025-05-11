# # backend/Dockerfile
# FROM eclipse-temurin:22-jdk-alpine
# WORKDIR /app
# COPY target/*.jar app.jar
# ENTRYPOINT ["java", "-jar", "app.jar"]
# EXPOSE 8080

# Stage 1: Budowanie aplikacji
FROM maven:3.9.0-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Uruchomienie aplikacji
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# ENV DB_URL=jdbc:postgresql://ep-super-flower-a9ysxa63-pooler.gwc.azure.neon.tech:5432/neondb
# ENV DB_USER=neondb_owner
# ENV DB_PASSWORD=npg_qhGurVQnt51K
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080