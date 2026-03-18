FROM maven:3.8-eclipse-temurin-21 AS builder

WORKDIR /build

COPY . .

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/target/*.jar /app/

CMD ["java", "-jar", "/app/*.jar"]