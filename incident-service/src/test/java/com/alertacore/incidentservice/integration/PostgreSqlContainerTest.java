package com.alertacore.incidentservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("testcontainers")
@Testcontainers
class PostgreSqlContainerTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer(
                    "postgres:17-alpine"
            )
                    .withDatabaseName(
                            "alertacore_test"
                    )
                    .withUsername(
                            "alertacore_test"
                    )
                    .withPassword(
                            "alertacore_test"
                    );

    @Autowired
    private DataSource dataSource;

    @Test
    void debeConectarseAPostgreSqlReal()
            throws Exception {

        assertTrue(postgres.isRunning());

        try (
                Connection conexion =
                        dataSource.getConnection()
        ) {
            assertFalse(conexion.isClosed());

            assertEquals(
                    "PostgreSQL",
                    conexion.getMetaData()
                            .getDatabaseProductName()
            );

            assertTrue(
                    conexion.getMetaData()
                            .getDatabaseProductVersion()
                            .startsWith("17")
            );

            assertNotNull(
                    conexion.getMetaData()
                            .getURL()
            );
        }
    }

    @Test
    void debeCrearElEsquemaIncidentsConFlyway()
            throws Exception {

        String consulta = """
                SELECT EXISTS (
                    SELECT 1
                    FROM information_schema.schemata
                    WHERE schema_name = 'incidents'
                )
                """;

        try (
                Connection conexion =
                        dataSource.getConnection();

                var sentencia =
                        conexion.prepareStatement(consulta);

                ResultSet resultado =
                        sentencia.executeQuery()
        ) {
            assertTrue(resultado.next());

            assertTrue(
                    resultado.getBoolean(1)
            );
        }
    }

    @Test
    void debeCrearLasTablasPrincipalesConFlyway()
            throws Exception {

        String consulta = """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'incidents'
                  AND table_name IN (
                      'incidentes',
                      'historial_estados_incidente'
                  )
                """;

        try (
                Connection conexion =
                        dataSource.getConnection();

                var sentencia =
                        conexion.prepareStatement(consulta);

                ResultSet resultado =
                        sentencia.executeQuery()
        ) {
            assertTrue(resultado.next());

            assertEquals(
                    2,
                    resultado.getInt(1)
            );
        }
    }
}