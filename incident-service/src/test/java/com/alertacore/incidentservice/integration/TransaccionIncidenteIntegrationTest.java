package com.alertacore.incidentservice.integration;

import com.alertacore.incidentservice.domain.model.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.port.out.RepositorioHistorialEstadoPort;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("testcontainers")
@Testcontainers
@Import(
        TransaccionIncidenteIntegrationTest
                .ConfiguracionFalloHistorial.class
)
class TransaccionIncidenteIntegrationTest {

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
    private RepositorioIncidentePort
            repositorioIncidente;

    /*
     * Spring inyectará CambiarEstadoTransaccional porque está
     * marcado como @Primary en UseCaseConfig.
     */
    @Autowired
    private CambiarEstadoIncidentePort
            cambiarEstadoIncidente;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void debeRevertirCambioDeEstadoSiFallaElHistorial() {
        Incidente incidente =
                repositorioIncidente.guardar(
                        crearIncidente()
                );

        UUID incidenteId = incidente.getId();

        assertEquals(
                EstadoIncidente.REPORTADO,
                consultarEstadoEnBaseDeDatos(
                        incidenteId
                )
        );

        assertEquals(
                0,
                contarRegistrosHistorial(
                        incidenteId
                )
        );

        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        "Informacion verificada por el operador"
                );

        RuntimeException excepcion =
                assertThrows(
                        RuntimeException.class,
                        () -> cambiarEstadoIncidente.ejecutar(
                                comando
                        )
                );

        assertEquals(
                "Fallo intencional al guardar el historial",
                excepcion.getMessage()
        );

        /*
         * Aunque el caso de uso ejecutó primero:
         *
         * repositorioIncidente.guardar(incidente)
         *
         * la RuntimeException posterior debe provocar rollback.
         */
        assertEquals(
                EstadoIncidente.REPORTADO,
                consultarEstadoEnBaseDeDatos(
                        incidenteId
                )
        );

        /*
         * El repositorio de historial falló, por lo que tampoco
         * debe existir ningún registro histórico.
         */
        assertEquals(
                0,
                contarRegistrosHistorial(
                        incidenteId
                )
        );

        /*
         * La consulta por el puerto también debe reconstruir
         * el incidente con el estado original.
         */
        Incidente recuperado =
                repositorioIncidente.buscarPorId(
                                incidenteId
                        )
                        .orElseThrow();

        assertEquals(
                EstadoIncidente.REPORTADO,
                recuperado.getEstado()
        );
    }

    @Test
    void debeConfirmarQueElBeanPrincipalEsTransaccional() {
        /*
         * Spring crea un proxy alrededor del decorador por la
         * anotación @Transactional.
         */
        assertTrue(
                org.springframework.aop.support
                        .AopUtils.isAopProxy(
                                cambiarEstadoIncidente
                        )
        );
    }

    private EstadoIncidente consultarEstadoEnBaseDeDatos(
            UUID incidenteId
    ) {
        String estado = jdbcTemplate.queryForObject(
                """
                SELECT estado
                FROM incidents.incidentes
                WHERE id = ?
                """,
                String.class,
                incidenteId
        );

        return EstadoIncidente.valueOf(
                estado
        );
    }

    private int contarRegistrosHistorial(
            UUID incidenteId
    ) {
        Integer cantidad =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM incidents.historial_estados_incidente
                        WHERE incidente_id = ?
                        """,
                        Integer.class,
                        incidenteId
                );

        return cantidad == null
                ? 0
                : cantidad;
    }

    private Incidente crearIncidente() {
        return Incidente.reportar(
                TipoIncidente.INUNDACION,
                "Inundacion creada para probar rollback transaccional",
                new Ubicacion(
                        4.6579,
                        -74.0596,
                        "Carrera 7 con Calle 72, Bogota"
                ),
                new InformacionContacto(
                        "Operador de integracion",
                        "integracion@alertacore.com",
                        "3001234567"
                ),
                10,
                true
        );
    }

    @TestConfiguration(
            proxyBeanMethods = false
    )
    static class ConfiguracionFalloHistorial {

        /*
         * Este bean reemplaza como opción principal al adaptador
         * normal del historial únicamente dentro de esta prueba.
         *
         * El caso de uso lo recibe porque ambos implementan
         * RepositorioHistorialEstadoPort y este está marcado
         * como @Primary.
         */
        @Bean
        @Primary
        RepositorioHistorialEstadoPort
        repositorioHistorialQueFalla() {

            return new RepositorioHistorialEstadoPort() {

                @Override
                public RegistroHistorialEstado guardar(
                        RegistroHistorialEstado registro
                ) {
                    throw new RuntimeException(
                            "Fallo intencional al guardar el historial"
                    );
                }

                @Override
                public List<RegistroHistorialEstado>
                buscarPorIncidenteIdOrdenado(
                        UUID incidenteId
                ) {
                    return List.of();
                }
            };
        }
    }
}