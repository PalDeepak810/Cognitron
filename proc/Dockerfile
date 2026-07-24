
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app


COPY pom.xml .


RUN mvn dependency:resolve-plugins dependency:resolve


COPY src ./src


RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app


COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-XX:+UseG1GC", "-jar", "app.jar"]