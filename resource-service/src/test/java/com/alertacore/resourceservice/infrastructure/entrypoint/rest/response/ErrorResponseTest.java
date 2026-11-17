package com.alertacore.resourceservice.infrastructure.entrypoint.rest.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void deberiaCrearErrorResponse() {

        LocalDateTime timestamp =
                LocalDateTime.now();

        ErrorResponse response =
                new ErrorResponse(
                        timestamp,
                        404,
                        "RECURSO_NO_ENCONTRADO",
                        "No existe recurso",
                        "/api/v1/recursos/1",
                        Map.of(
                                "codigo",
                                "invalido"
                        )
                );

        assertEquals(
                timestamp,
                response.timestamp()
        );

        assertEquals(
                404,
                response.estado()
        );

        assertEquals(
                "RECURSO_NO_ENCONTRADO",
                response.codigo()
        );

        assertEquals(
                "No existe recurso",
                response.mensaje()
        );
    }
}