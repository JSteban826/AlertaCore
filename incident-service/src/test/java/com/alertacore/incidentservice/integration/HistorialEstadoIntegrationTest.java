package com.alertacore.incidentservice.integration;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.gateway.RepositorioHistorialEstadoPort;
import com.alertacore.incidentservice.domain.gateway.IncidenteGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("testcontainers")
@Testcontainers
@Transactional
class HistorialEstadoIntegrationTest {

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
    private IncidenteGateway
            repositorioIncidente;

    @Autowired
    private RepositorioHistorialEstadoPort
            repositorioHistorial;

    @Test
    void debeGuardarYConsultarRegistroDeHistorial() {
        Incidente incidente =
                repositorioIncidente.guardar(
                        crearIncidente()
                );

        LocalDateTime fechaCambio =
                LocalDateTime.of(
                        2026,
                        11,
                        16,
                        14,
                        0
                );

        RegistroHistorialEstado registro =
                crearRegistro(
                        incidente.getId(),
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        "Informacion verificada",
                        "OPERADOR_API",
                        fechaCambio
                );

        RegistroHistorialEstado guardado =
                repositorioHistorial.guardar(
                        registro
                );

        assertNotNull(
                guardado
        );

        assertEquals(
                registro.id(),
                guardado.id()
        );

        assertEquals(
                incidente.getId(),
                guardado.incidenteId()
        );

        assertEquals(
                EstadoIncidente.REPORTADO,
                guardado.estadoAnterior()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                guardado.nuevoEstado()
        );

        assertEquals(
                "Informacion verificada",
                guardado.motivo()
        );

        assertEquals(
                "OPERADOR_API",
                guardado.responsable()
        );

        assertEquals(
                fechaCambio,
                guardado.fechaCambio()
        );
    }

    @Test
    void debeConsultarHistorialEnOrdenCronologico() {
        Incidente incidente =
                repositorioIncidente.guardar(
                        crearIncidente()
                );

        LocalDateTime primeraFecha =
                LocalDateTime.of(
                        2026,
                        11,
                        16,
                        10,
                        0
                );

        LocalDateTime segundaFecha =
                LocalDateTime.of(
                        2026,
                        11,
                        16,
                        10,
                        5
                );

        /*
         * Guardamos primero el registro más reciente
         * para comprobar que PostgreSQL lo devuelve
         * ordenado por fecha ascendente.
         */
        repositorioHistorial.guardar(
                crearRegistro(
                        incidente.getId(),
                        EstadoIncidente.VALIDADO,
                        EstadoIncidente.CLASIFICADO,
                        "Clasificacion automatica de riesgo",
                        "SISTEMA_CLASIFICACION",
                        segundaFecha
                )
        );

        repositorioHistorial.guardar(
                crearRegistro(
                        incidente.getId(),
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        "Informacion verificada",
                        "OPERADOR_API",
                        primeraFecha
                )
        );

        List<RegistroHistorialEstado> historial =
                repositorioHistorial
                        .buscarPorIncidenteIdOrdenado(
                                incidente.getId()
                        );

        assertEquals(
                2,
                historial.size()
        );

        RegistroHistorialEstado primerRegistro =
                historial.get(0);

        RegistroHistorialEstado segundoRegistro =
                historial.get(1);

        assertEquals(
                EstadoIncidente.REPORTADO,
                primerRegistro.estadoAnterior()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                primerRegistro.nuevoEstado()
        );

        assertEquals(
                primeraFecha,
                primerRegistro.fechaCambio()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                segundoRegistro.estadoAnterior()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                segundoRegistro.nuevoEstado()
        );

        assertEquals(
                segundaFecha,
                segundoRegistro.fechaCambio()
        );

        assertTrue(
                primerRegistro.fechaCambio()
                        .isBefore(
                                segundoRegistro.fechaCambio()
                        )
        );
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayHistorial() {
        Incidente incidente =
                repositorioIncidente.guardar(
                        crearIncidente()
                );

        List<RegistroHistorialEstado> historial =
                repositorioHistorial
                        .buscarPorIncidenteIdOrdenado(
                                incidente.getId()
                        );

        assertTrue(
                historial.isEmpty()
        );
    }

    @Test
    void debeSepararElHistorialDeCadaIncidente() {
        Incidente primerIncidente =
                repositorioIncidente.guardar(
                        crearIncidente()
                );

        Incidente segundoIncidente =
                repositorioIncidente.guardar(
                        crearIncidente()
                );

        repositorioHistorial.guardar(
                crearRegistro(
                        primerIncidente.getId(),
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        "Validacion del primer incidente",
                        "OPERADOR_API",
                        LocalDateTime.of(
                                2026,
                                11,
                                16,
                                11,
                                0
                        )
                )
        );

        repositorioHistorial.guardar(
                crearRegistro(
                        segundoIncidente.getId(),
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        "Validacion del segundo incidente",
                        "OPERADOR_API",
                        LocalDateTime.of(
                                2026,
                                11,
                                16,
                                11,
                                5
                        )
                )
        );

        List<RegistroHistorialEstado> primerHistorial =
                repositorioHistorial
                        .buscarPorIncidenteIdOrdenado(
                                primerIncidente.getId()
                        );

        List<RegistroHistorialEstado> segundoHistorial =
                repositorioHistorial
                        .buscarPorIncidenteIdOrdenado(
                                segundoIncidente.getId()
                        );

        assertEquals(
                1,
                primerHistorial.size()
        );

        assertEquals(
                1,
                segundoHistorial.size()
        );

        assertEquals(
                primerIncidente.getId(),
                primerHistorial.get(0)
                        .incidenteId()
        );

        assertEquals(
                segundoIncidente.getId(),
                segundoHistorial.get(0)
                        .incidenteId()
        );

        assertEquals(
                "Validacion del primer incidente",
                primerHistorial.get(0).motivo()
        );

        assertEquals(
                "Validacion del segundo incidente",
                segundoHistorial.get(0).motivo()
        );
    }

    private Incidente crearIncidente() {
        return Incidente.reportar(
                TipoIncidente.INUNDACION,
                "Inundacion usada para probar el historial real",
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

    private RegistroHistorialEstado crearRegistro(
            UUID incidenteId,
            EstadoIncidente estadoAnterior,
            EstadoIncidente nuevoEstado,
            String motivo,
            String responsable,
            LocalDateTime fechaCambio
    ) {
        return new RegistroHistorialEstado(
                UUID.randomUUID(),
                incidenteId,
                estadoAnterior,
                nuevoEstado,
                motivo,
                responsable,
                fechaCambio
        );
    }
}