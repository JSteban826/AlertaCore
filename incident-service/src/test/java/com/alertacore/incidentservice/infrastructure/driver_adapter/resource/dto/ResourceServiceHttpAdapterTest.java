package com.alertacore.incidentservice.infrastructure.driver_adapter.resource.dto;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.enums.TipoRecurso;
import com.alertacore.incidentservice.domain.exception.RecursoDisponibleNoEncontradoException;
import com.alertacore.incidentservice.domain.exception.RecursoNoDisponibleException;
import com.alertacore.incidentservice.domain.exception.ServicioRecursosException;
import com.alertacore.incidentservice.domain.model.RecursoDisponible;
import com.alertacore.incidentservice.domain.model.ReservaRecurso;
import com.alertacore.incidentservice.infrastructure.driver_adapter.resource.ResourceServiceHttpAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ResourceServiceHttpAdapterTest {

    private static final String BASE_URL =
            "http://localhost:8082";

    private MockRestServiceServer servidor;
    private ResourceServiceHttpAdapter adapter;

    @BeforeEach
    void configurar() {
        RestClient.Builder builder =
                RestClient.builder()
                        .baseUrl(BASE_URL);

        servidor =
                MockRestServiceServer
                        .bindTo(builder)
                        .build();

        RestClient restClient =
                builder.build();

        adapter =
                new ResourceServiceHttpAdapter(
                        restClient
                );
    }

    @Test
    void debeBuscarRecursoMasCercano() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        String respuesta = """
                {
                  "recursoId": "%s",
                  "codigo": "AMB-001",
                  "tipo": "AMBULANCIA",
                  "distanciaKm": 4.8,
                  "tiempoEstimadoLlegadaMinutos": 12,
                  "estado": "DISPONIBLE"
                }
                """.formatted(recursoId);

        servidor.expect(
                        once(),
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + "buscar-mas-cercano"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andExpect(
                        content().contentType(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(
                        content().json(
                                """
                                {
                                  "incidenteId": "%s",
                                  "tipoRecurso": "AMBULANCIA",
                                  "latitudIncidente": 4.6579,
                                  "longitudIncidente": -74.0596,
                                  "distanciaMaximaKm": 30.0
                                }
                                """.formatted(incidenteId)
                        )
                )
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        RecursoDisponible resultado =
                adapter.buscarMasCercano(
                        incidenteId,
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596,
                        30.0
                );

        assertEquals(
                recursoId,
                resultado.recursoId()
        );

        assertEquals(
                "AMB-001",
                resultado.codigo()
        );

        assertEquals(
                TipoRecurso.AMBULANCIA,
                resultado.tipo()
        );

        assertEquals(
                4.8,
                resultado.distanciaKm()
        );

        assertEquals(
                12,
                resultado.tiempoEstimadoLlegadaMinutos()
        );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                resultado.estado()
        );

        servidor.verify();
    }

    @Test
    void debeLanzarExcepcionCuandoNoHayRecursosDisponibles() {
        UUID incidenteId = UUID.randomUUID();

        servidor.expect(
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + "buscar-mas-cercano"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andRespond(
                        withStatus(
                                org.springframework.http.HttpStatus
                                        .NOT_FOUND
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .body(
                                        """
                                        {
                                          "codigo": "RECURSO_DISPONIBLE_NO_ENCONTRADO"
                                        }
                                        """
                                )
                );

        RecursoDisponibleNoEncontradoException excepcion =
                assertThrows(
                        RecursoDisponibleNoEncontradoException.class,
                        () -> adapter.buscarMasCercano(
                                incidenteId,
                                TipoRecurso.AMBULANCIA,
                                4.6579,
                                -74.0596,
                                30.0
                        )
                );

        assertTrue(
                excepcion.getMessage()
                        .contains("AMBULANCIA")
        );

        servidor.verify();
    }

    @Test
    void debeReservarRecursoConIdempotencyKey() {
        UUID recursoId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();

        String idempotencyKey =
                UUID.randomUUID().toString();

        LocalDateTime fechaReserva =
                LocalDateTime.of(
                        2026,
                        11,
                        18,
                        10,
                        0
                );

        LocalDateTime fechaVencimiento =
                fechaReserva.plusMinutes(10);

        String respuesta = """
                {
                  "reservaId": "%s",
                  "recursoId": "%s",
                  "incidenteId": "%s",
                  "estadoRecurso": "RESERVADO",
                  "fechaReserva": "%s",
                  "fechaVencimiento": "%s"
                }
                """.formatted(
                reservaId,
                recursoId,
                incidenteId,
                fechaReserva,
                fechaVencimiento
        );

        servidor.expect(
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + recursoId
                                        + "/reservar"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andExpect(
                        header(
                                "Idempotency-Key",
                                idempotencyKey
                        )
                )
                .andExpect(
                        content().contentType(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(
                        content().json(
                                """
                                {
                                  "incidenteId": "%s",
                                  "duracionReservaMinutos": 10
                                }
                                """.formatted(incidenteId)
                        )
                )
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        ReservaRecurso resultado =
                adapter.reservar(
                        recursoId,
                        incidenteId,
                        10,
                        idempotencyKey
                );

        assertEquals(
                reservaId,
                resultado.reservaId()
        );

        assertEquals(
                recursoId,
                resultado.recursoId()
        );

        assertEquals(
                incidenteId,
                resultado.incidenteId()
        );

        assertEquals(
                EstadoRecurso.RESERVADO,
                resultado.estadoRecurso()
        );

        assertEquals(
                fechaReserva,
                resultado.fechaReserva()
        );

        assertEquals(
                fechaVencimiento,
                resultado.fechaVencimiento()
        );

        servidor.verify();
    }

    @Test
    void debeLanzarConflictoCuandoRecursoNoEstaDisponible() {
        UUID recursoId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();

        servidor.expect(
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + recursoId
                                        + "/reservar"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andExpect(
                        header(
                                "Idempotency-Key",
                                "llave-prueba"
                        )
                )
                .andRespond(
                        withStatus(
                                org.springframework.http.HttpStatus
                                        .CONFLICT
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .body(
                                        """
                                        {
                                          "codigo": "RECURSO_NO_DISPONIBLE"
                                        }
                                        """
                                )
                );

        RecursoNoDisponibleException excepcion =
                assertThrows(
                        RecursoNoDisponibleException.class,
                        () -> adapter.reservar(
                                recursoId,
                                incidenteId,
                                10,
                                "llave-prueba"
                        )
                );

        assertTrue(
                excepcion.getMessage()
                        .contains(
                                recursoId.toString()
                        )
        );

        servidor.verify();
    }

    @Test
    void debeLiberarRecursoCorrectamente() {
        UUID recursoId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();

        LocalDateTime fechaLiberacion =
                LocalDateTime.of(
                        2026,
                        11,
                        18,
                        10,
                        30
                );

        String respuesta = """
                {
                  "recursoId": "%s",
                  "estado": "DISPONIBLE",
                  "fechaLiberacion": "%s"
                }
                """.formatted(
                recursoId,
                fechaLiberacion
        );

        servidor.expect(
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + recursoId
                                        + "/liberar"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andExpect(
                        content().json(
                                """
                                {
                                  "incidenteId": "%s",
                                  "motivo": "Compensacion por fallo local"
                                }
                                """.formatted(incidenteId)
                        )
                )
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        adapter.liberar(
                recursoId,
                incidenteId,
                "Compensacion por fallo local"
        );

        servidor.verify();
    }

    @Test
    void debeLanzarExcepcionCuandoLiberacionEsRechazada() {
        UUID recursoId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();

        servidor.expect(
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + recursoId
                                        + "/liberar"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andRespond(
                        withStatus(
                                org.springframework.http.HttpStatus
                                        .CONFLICT
                        )
                );

        ServicioRecursosException excepcion =
                assertThrows(
                        ServicioRecursosException.class,
                        () -> adapter.liberar(
                                recursoId,
                                incidenteId,
                                "Prueba de conflicto"
                        )
                );

        assertTrue(
                excepcion.getMessage()
                        .contains(
                                recursoId.toString()
                        )
        );

        servidor.verify();
    }

    @Test
    void debeConvertirErrorCincoXxEnExcepcionDeServicio() {
        UUID incidenteId = UUID.randomUUID();

        servidor.expect(
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + "buscar-mas-cercano"
                        )
                )
                .andRespond(
                        withServerError()
                );

        ServicioRecursosException excepcion =
                assertThrows(
                        ServicioRecursosException.class,
                        () -> adapter.buscarMasCercano(
                                incidenteId,
                                TipoRecurso.AMBULANCIA,
                                4.6579,
                                -74.0596,
                                30.0
                        )
                );

        assertEquals(
                "Resource Service no pudo buscar recursos",
                excepcion.getMessage()
        );

        servidor.verify();
    }

    @Test
    void debeRechazarRestClientNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new ResourceServiceHttpAdapter(
                                null
                        )
                );

        assertEquals(
                "El cliente HTTP de recursos es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarRecursoIdNuloAlReservar() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> adapter.reservar(
                                null,
                                UUID.randomUUID(),
                                10,
                                "llave-prueba"
                        )
                );

        assertEquals(
                "El identificador del recurso es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarIdempotencyKeyNula() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> adapter.reservar(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                10,
                                null
                        )
                );

        assertEquals(
                "La llave de idempotencia es obligatoria",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarRecursoIdNuloAlLiberar() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> adapter.liberar(
                                null,
                                UUID.randomUUID(),
                                "Compensacion"
                        )
                );

        assertEquals(
                "El identificador del recurso es obligatorio",
                excepcion.getMessage()
        );
    }
}