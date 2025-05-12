# Build stage
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Chỉ cần copy từ chính thư mục gốc
COPY pom.xml .

# Lấy dependencies
RUN mvn dependency:go-offline -B

# Copy toàn bộ mã nguồn
COPY src ./src

# Build
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
