package com.alertacore.incidentservice.infrastructure
        .driver_adapter.resource;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.exception
        .RecursoNoDisponibleException;
import com.alertacore.incidentservice.domain.exception
        .ServicioRecursosException;
import com.alertacore.incidentservice.domain.model.DespachoRecurso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client
        .match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client
        .match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client
        .match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client
        .response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client
        .response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client
        .response.MockRestResponseCreators.withSuccess;

class ResourceServiceHttpAdapterDespachoTest {

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

        adapter =
                new ResourceServiceHttpAdapter(
                        builder.build()
                );
    }

    @Test
    void debeDespacharRecursoCorrectamente() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        LocalDateTime fechaDespacho =
                LocalDateTime.of(
                        2026,
                        11,
                        18,
                        20,
                        30
                );

        String respuesta = """
                {
                  "recursoId": "%s",
                  "incidenteId": "%s",
                  "estado": "DESPACHADO",
                  "fechaDespacho": "%s"
                }
                """.formatted(
                recursoId,
                incidenteId,
                fechaDespacho
        );

        servidor.expect(
                        once(),
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + recursoId
                                        + "/despachar"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(
                        content().json(
                                """
                                {
                                  "incidenteId": "%s",
                                  "responsable": "OPERADOR_CENTRAL"
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

        DespachoRecurso resultado =
                adapter.despachar(
                        recursoId,
                        incidenteId,
                        "OPERADOR_CENTRAL"
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
                EstadoRecurso.DESPACHADO,
                resultado.estado()
        );

        assertEquals(
                fechaDespacho,
                resultado.fechaDespacho()
        );

        servidor.verify();
    }

    @Test
    void debeConvertirConflictoEnRecursoNoDisponible() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        servidor.expect(
                        once(),
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + recursoId
                                        + "/despachar"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andRespond(
                        withStatus(HttpStatus.CONFLICT)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .body(
                                        """
                                        {
                                          "codigo": "TRANSICION_INVALIDA",
                                          "mensaje": "El recurso no esta reservado"
                                        }
                                        """
                                )
                );

        RecursoNoDisponibleException excepcion =
                assertThrows(
                        RecursoNoDisponibleException.class,
                        () -> adapter.despachar(
                                recursoId,
                                incidenteId,
                                "OPERADOR_CENTRAL"
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
        UUID recursoId = UUID.randomUUID();

        servidor.expect(
                        once(),
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + recursoId
                                        + "/despachar"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andRespond(
                        withServerError()
                );

        ServicioRecursosException excepcion =
                assertThrows(
                        ServicioRecursosException.class,
                        () -> adapter.despachar(
                                recursoId,
                                incidenteId,
                                "OPERADOR_CENTRAL"
                        )
                );

        assertEquals(
                "Resource Service no pudo despachar el recurso",
                excepcion.getMessage()
        );

        servidor.verify();
    }

    @Test
    void debeRechazarRespuestaVaciaAlDespachar() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        servidor.expect(
                        once(),
                        requestTo(
                                BASE_URL
                                        + "/api/v1/recursos/"
                                        + recursoId
                                        + "/despachar"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andRespond(
                        withSuccess(
                                "",
                                MediaType.APPLICATION_JSON
                        )
                );

        ServicioRecursosException excepcion =
                assertThrows(
                        ServicioRecursosException.class,
                        () -> adapter.despachar(
                                recursoId,
                                incidenteId,
                                "OPERADOR_CENTRAL"
                        )
                );

        assertEquals(
                "Resource Service devolvio una respuesta "
                        + "vacia al despachar el recurso",
                excepcion.getMessage()
        );

        servidor.verify();
    }

    @Test
    void debeRechazarRecursoNuloAlDespachar() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> adapter.despachar(
                                null,
                                UUID.randomUUID(),
                                "OPERADOR_CENTRAL"
                        )
                );

        assertEquals(
                "El identificador del recurso es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarIncidenteNuloAlDespachar() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> adapter.despachar(
                                UUID.randomUUID(),
                                null,
                                "OPERADOR_CENTRAL"
                        )
                );

        assertEquals(
                "El identificador del incidente es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarResponsableNuloAlDespachar() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> adapter.despachar(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                null
                        )
                );

        assertEquals(
                "El responsable es obligatorio",
                excepcion.getMessage()
        );
    }
}