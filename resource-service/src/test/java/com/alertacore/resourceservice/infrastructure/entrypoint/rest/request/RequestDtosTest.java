package com.alertacore.resourceservice.infrastructure.entrypoint.rest.request;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequestDtosTest {

    @Test
    void deberiaCrearRegistrarRecursoRequest() {

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
    }

    @Test
    void deberiaCrearBuscarMasCercanoRequest() {

        BuscarRecursoMasCercanoRequest request =
                new BuscarRecursoMasCercanoRequest(
                        UUID.randomUUID(),
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596,
                        30
                );

        assertEquals(
                30,
                request.distanciaMaximaKm()
        );
    }

    @Test
    void deberiaCrearReservarRecursoRequest() {

        ReservarRecursoRequest request =
                new ReservarRecursoRequest(
                        UUID.randomUUID(),
                        10
                );

        assertEquals(
                10,
                request.duracionReservaMinutos()
        );
    }

    @Test
    void deberiaCrearDespacharRecursoRequest() {

        DespacharRecursoRequest request =
                new DespacharRecursoRequest(
                        UUID.randomUUID(),
                        "OPERADOR-001"
                );

        assertEquals(
                "OPERADOR-001",
                request.responsable()
        );
    }

    @Test
    void deberiaCrearLiberarRecursoRequest() {

        LiberarRecursoRequest request =
                new LiberarRecursoRequest(
                        UUID.randomUUID(),
                        "Finalizado"
                );

        assertEquals(
                "Finalizado",
                request.motivo()
        );
    }

    @Test
    void deberiaCrearActualizarUbicacionRequest() {

        ActualizarUbicacionRequest request =
                new ActualizarUbicacionRequest(
                        4.6579,
                        -74.0596
                );

        assertEquals(
                4.6579,
                request.latitud()
        );
    }
}