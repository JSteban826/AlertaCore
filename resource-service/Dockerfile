# Etapa de compilacion
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY incident-service/pom.xml incident-service/pom.xml
COPY resource-service/pom.xml resource-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml

RUN chmod +x mvnw

RUN ./mvnw -pl resource-service -am dependency:go-offline -DskipTests

COPY incident-service/src incident-service/src
COPY resource-service/src resource-service/src
COPY notification-service/src notification-service/src

RUN ./mvnw -pl resource-service -am clean package -DskipTests

# Etapa de ejecucion
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /workspace/resource-service/target/*.jar app.jar

USER spring:spring

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]