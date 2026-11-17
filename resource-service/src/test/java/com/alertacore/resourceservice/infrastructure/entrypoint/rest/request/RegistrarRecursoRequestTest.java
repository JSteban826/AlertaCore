package com.alertacore.resourceservice.infrastructure.entrypoint.rest.request;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrarRecursoRequestTest {

    @Test
    void deberiaCrearRequest() {

        RegistrarRecursoRequest request =
                new RegistrarRecursoRequest(
                        "AMB-001",
                        "Ambulancia",
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596
                );

        assertEquals(
                "AMB-001",
                request.codigo()
        );

        assertEquals(
                TipoRecurso.AMBULANCIA,
                request.tipo()
        );
    }
}