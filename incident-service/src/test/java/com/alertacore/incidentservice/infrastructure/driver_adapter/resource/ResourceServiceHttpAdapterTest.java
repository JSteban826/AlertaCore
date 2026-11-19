package com.alertacore.incidentservice.infrastructure
        .driver_adapter.resource;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.exception
        .ServicioRecursosException;
import com.alertacore.incidentservice.domain.model.LiberacionRecurso;
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

        adapter =
                new ResourceServiceHttpAdapter(
                        builder.build()
                );
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
                        21,
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
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(
                        content().json(
                                """
                                {
                                  "incidenteId": "%s",
                                  "motivo": "Atencion finalizada"
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

        LiberacionRecurso resultado =
                adapter.liberar(
                        recursoId,
                        incidenteId,
                        "Atencion finalizada"
                );

        assertEquals(
                recursoId,
                resultado.recursoId()
        );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                resultado.estado()
        );

        assertEquals(
                fechaLiberacion,
                resultado.fechaLiberacion()
        );

        servidor.verify();
    }

    @Test
    void debeRechazarRecursoNuloAlLiberar() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> adapter.liberar(
                                null,
                                UUID.randomUUID(),
                                "Atencion finalizada"
                        )
                );

        assertEquals(
                "El identificador del recurso es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarIncidenteNuloAlLiberar() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> adapter.liberar(
                                UUID.randomUUID(),
                                null,
                                "Atencion finalizada"
                        )
                );

        assertEquals(
                "El identificador del incidente es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarMotivoNuloAlLiberar() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> adapter.liberar(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                null
                        )
                );

        assertEquals(
                "El motivo de liberacion es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeConvertirConflictoAlLiberar() {
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
                                HttpStatus.CONFLICT
                        )
                );

        ServicioRecursosException excepcion =
                assertThrows(
                        ServicioRecursosException.class,
                        () -> adapter.liberar(
                                recursoId,
                                incidenteId,
                                "Atencion finalizada"
                        )
                );

        assertEquals(
                "El recurso no pudo liberarse: "
                        + recursoId,
                excepcion.getMessage()
        );

        servidor.verify();
    }

    @Test
    void debeConvertirErrorCincoXxAlLiberar() {
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
                        withServerError()
                );

        ServicioRecursosException excepcion =
                assertThrows(
                        ServicioRecursosException.class,
                        () -> adapter.liberar(
                                recursoId,
                                incidenteId,
                                "Atencion finalizada"
                        )
                );

        assertEquals(
                "Resource Service no pudo liberar el recurso",
                excepcion.getMessage()
        );

        servidor.verify();
    }

    @Test
    void debeRechazarRespuestaVaciaAlLiberar() {
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
                        withSuccess(
                                "",
                                MediaType.APPLICATION_JSON
                        )
                );

        ServicioRecursosException excepcion =
                assertThrows(
                        ServicioRecursosException.class,
                        () -> adapter.liberar(
                                recursoId,
                                incidenteId,
                                "Atencion finalizada"
                        )
                );

        assertEquals(
                "Resource Service devolvio una respuesta "
                        + "vacia al liberar el recurso",
                excepcion.getMessage()
        );

        servidor.verify();
    }
}