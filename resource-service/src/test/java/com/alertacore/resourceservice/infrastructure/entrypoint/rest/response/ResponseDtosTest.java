package com.alertacore.resourceservice.infrastructure.entrypoint.rest.response;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResponseDtosTest {

    @Test
    void deberiaCrearRecursoResponse() {

        RecursoResponse response =
                new RecursoResponse(
                        UUID.randomUUID(),
                        "AMB-001",
                        "Ambulancia",
                        TipoRecurso.AMBULANCIA,
                        EstadoRecurso.DISPONIBLE,
                        4.6,
                        -74.0
                );

        assertEquals(
                "AMB-001",
                response.codigo()
        );
    }

    @Test
    void deberiaCrearReservaResponse() {

        ReservaRecursoResponse response =
                new ReservaRecursoResponse(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        EstadoRecurso.RESERVADO,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        assertEquals(
                EstadoRecurso.RESERVADO,
                response.estadoRecurso()
        );
    }

    @Test
    void deberiaCrearErrorResponse() {

        ErrorResponse response =
                new ErrorResponse(
                        LocalDateTime.now(),
                        404,
                        "NO_FOUND",
                        "No encontrado",
                        "/api",
                        java.util.Map.of()
                );

        assertEquals(
                404,
                response.estado()
        );
    }
}