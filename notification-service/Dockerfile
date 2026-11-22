# ============================================================
# ETAPA 1: COMPILACION
# ============================================================
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

# Copiar Maven Wrapper y POM principal
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copiar los POM de todos los modulos del reactor
COPY incident-service/pom.xml incident-service/pom.xml
COPY resource-service/pom.xml resource-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml

# Dar permiso de ejecucion al Maven Wrapper
RUN chmod +x mvnw

# Descargar las dependencias antes de copiar el codigo fuente
RUN ./mvnw -pl notification-service -am dependency:go-offline -DskipTests

# Copiar el codigo fuente de los modulos
COPY incident-service/src incident-service/src
COPY resource-service/src resource-service/src
COPY notification-service/src notification-service/src

# Compilar notification-service y sus dependencias
RUN ./mvnw -pl notification-service -am clean package -DskipTests


# ============================================================
# ETAPA 2: EJECUCION
# ============================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear un usuario sin privilegios para ejecutar la aplicacion
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar el JAR compilado
COPY --from=build /workspace/notification-service/target/*.jar app.jar

# Ejecutar como usuario sin privilegios
USER spring:spring

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
