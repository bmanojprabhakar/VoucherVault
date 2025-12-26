# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml first to leverage Docker cache for dependencies
COPY backend/pom.xml .
# Download dependencies - this step will be cached if pom.xml doesn't change
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Koyeb expects the app to listen on port 8080 by default
ENV PORT=8080
EXPOSE 8080

# Environment variables should be passed at runtime by Koyeb
ENTRYPOINT ["java", "-jar", "app.jar"]
