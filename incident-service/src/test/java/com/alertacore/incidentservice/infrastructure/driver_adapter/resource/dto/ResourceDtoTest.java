package com.alertacore.incidentservice.infrastructure
        .driver_adapter.resource.dto;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.enums.TipoRecurso;
import com.alertacore.incidentservice.domain.model.RecursoDisponible;
import com.alertacore.incidentservice.domain.model.ReservaRecurso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceDtoTest {

    @Test
    void debeConvertirRecursoCercanoADominio() {
        UUID recursoId = UUID.randomUUID();

        RecursoCercanoResponse response =
                new RecursoCercanoResponse(
                        recursoId,
                        "AMB-001",
                        TipoRecurso.AMBULANCIA,
                        4.8,
                        12,
                        EstadoRecurso.DISPONIBLE
                );

        RecursoDisponible recurso =
                response.aDominio();

        assertEquals(
                recursoId,
                recurso.recursoId()
        );

        assertEquals(
                "AMB-001",
                recurso.codigo()
        );

        assertEquals(
                TipoRecurso.AMBULANCIA,
                recurso.tipo()
        );

        assertEquals(
                4.8,
                recurso.distanciaKm()
        );

        assertEquals(
                12,
                recurso.tiempoEstimadoLlegadaMinutos()
        );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                recurso.estado()
        );
    }

    @Test
    void debeConvertirReservaADominio() {
        UUID reservaId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();

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

        ReservaRecursoResponse response =
                new ReservaRecursoResponse(
                        reservaId,
                        recursoId,
                        incidenteId,
                        EstadoRecurso.RESERVADO,
                        fechaReserva,
                        fechaVencimiento
                );

        ReservaRecurso reserva =
                response.aDominio();

        assertEquals(
                reservaId,
                reserva.reservaId()
        );

        assertEquals(
                recursoId,
                reserva.recursoId()
        );

        assertEquals(
                incidenteId,
                reserva.incidenteId()
        );

        assertEquals(
                EstadoRecurso.RESERVADO,
                reserva.estadoRecurso()
        );

        assertEquals(
                fechaReserva,
                reserva.fechaReserva()
        );

        assertEquals(
                fechaVencimiento,
                reserva.fechaVencimiento()
        );
    }

    @Test
    void debeCrearRequestsDelContrato() {
        UUID incidenteId = UUID.randomUUID();

        BuscarRecursoMasCercanoRequest busqueda =
                new BuscarRecursoMasCercanoRequest(
                        incidenteId,
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596,
                        30.0
                );

        ReservarRecursoRequest reserva =
                new ReservarRecursoRequest(
                        incidenteId,
                        10
                );

        LiberarRecursoRequest liberacion =
                new LiberarRecursoRequest(
                        incidenteId,
                        "Compensacion por fallo local"
                );

        assertEquals(
                incidenteId,
                busqueda.incidenteId()
        );

        assertEquals(
                TipoRecurso.AMBULANCIA,
                busqueda.tipoRecurso()
        );

        assertEquals(
                incidenteId,
                reserva.incidenteId()
        );

        assertEquals(
                10,
                reserva.duracionReservaMinutos()
        );

        assertEquals(
                incidenteId,
                liberacion.incidenteId()
        );

        assertEquals(
                "Compensacion por fallo local",
                liberacion.motivo()
        );
    }

    @Test
    void debeCrearRespuestaDeLiberacion() {
        UUID recursoId = UUID.randomUUID();

        LocalDateTime fecha =
                LocalDateTime.now();

        LiberarRecursoResponse response =
                new LiberarRecursoResponse(
                        recursoId,
                        EstadoRecurso.DISPONIBLE,
                        fecha
                );

        assertEquals(
                recursoId,
                response.recursoId()
        );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                response.estado()
        );

        assertEquals(
                fecha,
                response.fechaLiberacion()
        );
    }
}