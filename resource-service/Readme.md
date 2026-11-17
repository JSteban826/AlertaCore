# AlertaCore - Resource Service

Microservicio responsable de la gestión, localización, reserva, despacho y liberación de recursos de emergencia dentro del ecosistema AlertaCore.

---

## Tecnologías

- Java 21
- Spring Boot 4.1.2
- PostgreSQL
- Flyway
- JPA / Hibernate
- Swagger OpenAPI
- Lombok
- JUnit 5
- Mockito

---

## Arquitectura

Clean Architecture.

```text
infrastructure
│
application
│
domain
```

Principios aplicados:

- SOLID
- Clean Architecture
- Ports and Adapters

---

## Puerto

```text
8082
```

---

## Base de datos

Base:

```text
Alertcore
```

Esquema:

```text
resources
```

---

## Flyway

Migraciones:

```text
V1__crear_tabla_recursos.sql
V2__crear_reservas_recursos.sql
V3__crear_despachos_recursos.sql
```

---

## Cómo ejecutar

### PostgreSQL

Asegúrese de tener PostgreSQL en ejecución.

### Compilar

```bash
./mvnw clean compile
```

### Ejecutar

```bash
./mvnw spring-boot:run
```

---

## Swagger

Disponible en:

```text
http://localhost:8082/swagger-ui/index.html
```

---

## Endpoints principales

### Recursos

```http
POST /api/v1/recursos
GET  /api/v1/recursos
GET  /api/v1/recursos/{id}
GET  /api/v1/recursos/disponibles
```

### Operaciones de negocio

```http
POST  /api/v1/recursos/buscar-mas-cercano
POST  /api/v1/recursos/{id}/reservar
POST  /api/v1/recursos/{id}/despachar
POST  /api/v1/recursos/{id}/liberar
PATCH /api/v1/recursos/{id}/ubicacion
GET   /api/v1/recursos/reporte-utilizacion
```

---

## Cobertura

### JaCoCo

Cobertura alcanzada:

```text
90 %
```

Cumple la meta mínima definida para el proyecto.

---

## Colección Postman

Ubicación:

```text
postman/
```

Archivo:

```text
AlertaCore-Resource-Service.postman_collection.json
```