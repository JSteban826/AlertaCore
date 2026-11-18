package com.alertacore.incidentservice.integration;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.model.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.domain.model.gateway.HistorialEstadoGateway;
import com.alertacore.incidentservice.domain.model.gateway.IncidenteGateway;
import com.alertacore.incidentservice.domain.model.gateway.TransaccionGateway;
import com.alertacore.incidentservice.domain.usecase.IncidenteUseCase;
import com.alertacore.incidentservice.infrastructure.transaction.SpringTransaccionAdapter;
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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    private IncidenteGateway incidenteGateway;

    /*
     * Esta es la entrada única utilizada por el controlador.
     *
     * Para cambiar el estado, IncidenteUseCase utiliza
     * TransaccionGateway antes de delegar en
     * CambiarEstadoIncidenteUseCase.
     */
    @Autowired
    private IncidenteUseCase incidenteUseCase;

    @Autowired
    private TransaccionGateway transaccionGateway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void debeRevertirCambioDeEstadoSiFallaElHistorial() {
        Incidente incidente =
                incidenteGateway.guardar(
                        crearIncidente()
                );

        UUID incidenteId =
                incidente.getId();

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
                        () ->
                                incidenteUseCase
                                        .cambiarEstadoIncidente(
                                                comando
                                        )
                );

        assertEquals(
                "Fallo intencional al guardar el historial",
                excepcion.getMessage()
        );

        /*
         * CambiarEstadoIncidenteUseCase guarda primero
         * el incidente con estado VALIDADO.
         *
         * Después intenta guardar el historial y el gateway
         * de prueba lanza una RuntimeException.
         *
         * SpringTransaccionAdapter debe revertir el primer
         * guardado y conservar el estado REPORTADO.
         */
        assertEquals(
                EstadoIncidente.REPORTADO,
                consultarEstadoEnBaseDeDatos(
                        incidenteId
                )
        );

        /*
         * No debe quedar ningún registro parcial en la tabla
         * del historial.
         */
        assertEquals(
                0,
                contarRegistrosHistorial(
                        incidenteId
                )
        );

        /*
         * La consulta mediante el gateway también debe
         * reconstruir el incidente con el estado original.
         */
        Incidente recuperado =
                incidenteGateway.buscarPorId(
                                incidenteId
                        )
                        .orElseThrow();

        assertEquals(
                EstadoIncidente.REPORTADO,
                recuperado.getEstado()
        );
    }

    @Test
    void debeUsarElAdaptadorTransaccionalDeSpring() {
        /*
         * La transacción ya no depende de un decorador
         * anotado con @Transactional.
         *
         * Ahora la implementación concreta del gateway
         * utiliza TransactionTemplate.
         */
        assertInstanceOf(
                SpringTransaccionAdapter.class,
                transaccionGateway
        );
    }

    private EstadoIncidente consultarEstadoEnBaseDeDatos(
            UUID incidenteId
    ) {
        String estado =
                jdbcTemplate.queryForObject(
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
         * Este bean sustituye como opción principal
         * al adaptador normal de historial solamente
         * dentro de esta prueba.
         *
         * CambiarEstadoIncidenteUseCase recibe este gateway
         * y provoca intencionalmente el fallo después de
         * modificar el incidente.
         */
        @Bean
        @Primary
        HistorialEstadoGateway historialEstadoGatewayQueFalla() {
            return new HistorialEstadoGateway() {

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