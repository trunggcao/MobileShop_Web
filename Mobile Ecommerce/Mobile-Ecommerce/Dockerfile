# Build stage
FROM maven:3.8.5-openjdk-17 AS build

# Tạo thư mục làm việc
WORKDIR /app

# Bước 1: Copy POM file đầu tiên (tận dụng Docker cache)
COPY Mobile\ Ecommerce/Mobile-Ecommerce/pom.xml .

# Bước 2: Tải dependencies
RUN mvn dependency:go-offline -B

# Bước 3: Copy source code
COPY Mobile\ Ecommerce/Mobile-Ecommerce/src ./src

# Bước 4: Build với kiểm tra rõ ràng
RUN ls -la /app && \
    [ -f "pom.xml" ] && \
    mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
