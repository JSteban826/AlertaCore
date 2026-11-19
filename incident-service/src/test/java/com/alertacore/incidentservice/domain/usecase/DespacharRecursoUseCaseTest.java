package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.exception.IncidenteNoClasificadoException;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.exception.RecursoNoDisponibleException;
import com.alertacore.incidentservice.domain.model.DespachoRecurso;
import com.alertacore.incidentservice.domain.model.DespachoRecursoResultado;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.command
        .DespacharRecursoCommand;
import com.alertacore.incidentservice.domain.model.gateway.IncidenteGateway;
import com.alertacore.incidentservice.domain.model.gateway.ServicioRecursosGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DespacharRecursoUseCaseTest {

    private IncidenteGateway incidenteGateway;
    private ServicioRecursosGateway recursosGateway;

    private DespacharRecursoUseCase casoUso;

    @BeforeEach
    void configurar() {
        incidenteGateway =
                mock(IncidenteGateway.class);

        recursosGateway =
                mock(ServicioRecursosGateway.class);

        casoUso =
                new DespacharRecursoUseCase(
                        incidenteGateway,
                        recursosGateway
                );
    }

    @Test
    void debeDespacharRecursoAsignadoCorrectamente() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        LocalDateTime fechaDespacho =
                LocalDateTime.of(
                        2026,
                        11,
                        18,
                        18,
                        30
                );

        Incidente incidente =
                mock(Incidente.class);

        when(
                incidente.getId()
        ).thenReturn(
                incidenteId
        );

        when(
                incidente.getEstado()
        ).thenReturn(
                EstadoIncidente.RECURSO_ASIGNADO
        );

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.of(incidente)
        );

        DespachoRecurso despacho =
                new DespachoRecurso(
                        recursoId,
                        incidenteId,
                        EstadoRecurso.DESPACHADO,
                        fechaDespacho
                );

        when(
                recursosGateway.despachar(
                        recursoId,
                        incidenteId,
                        "OPERADOR_CENTRAL"
                )
        ).thenReturn(
                despacho
        );

        DespacharRecursoCommand comando =
                new DespacharRecursoCommand(
                        incidenteId,
                        recursoId,
                        "OPERADOR_CENTRAL"
                );

        DespachoRecursoResultado resultado =
                casoUso.ejecutar(
                        comando
                );

        assertEquals(
                incidenteId,
                resultado.incidenteId()
        );

        assertEquals(
                recursoId,
                resultado.recursoId()
        );

        assertEquals(
                EstadoRecurso.DESPACHADO,
                resultado.estadoRecurso()
        );

        assertEquals(
                EstadoIncidente.RECURSO_ASIGNADO,
                resultado.estadoIncidente()
        );

        assertEquals(
                fechaDespacho,
                resultado.fechaDespacho()
        );

        verify(incidenteGateway)
                .buscarPorId(
                        incidenteId
                );

        verify(recursosGateway)
                .despachar(
                        recursoId,
                        incidenteId,
                        "OPERADOR_CENTRAL"
                );
    }

    @Test
    void debeRechazarIncidenteInexistente() {
        UUID incidenteId = UUID.randomUUID();

        DespacharRecursoCommand comando =
                new DespacharRecursoCommand(
                        incidenteId,
                        UUID.randomUUID(),
                        "OPERADOR_CENTRAL"
                );

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                IncidenteNoEncontradoException.class,
                () -> casoUso.ejecutar(
                        comando
                )
        );

        verifyNoInteractions(
                recursosGateway
        );
    }

    @Test
    void debeRechazarIncidenteSinRecursoAsignado() {
        UUID incidenteId = UUID.randomUUID();

        Incidente incidente =
                mock(Incidente.class);

        when(
                incidente.getId()
        ).thenReturn(
                incidenteId
        );

        when(
                incidente.getEstado()
        ).thenReturn(
                EstadoIncidente.CLASIFICADO
        );

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.of(incidente)
        );

        DespacharRecursoCommand comando =
                new DespacharRecursoCommand(
                        incidenteId,
                        UUID.randomUUID(),
                        "OPERADOR_CENTRAL"
                );

        assertThrows(
                IncidenteNoClasificadoException.class,
                () -> casoUso.ejecutar(
                        comando
                )
        );

        verifyNoInteractions(
                recursosGateway
        );
    }

    @Test
    void debePropagarConflictoDeResourceService() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        Incidente incidente =
                crearIncidenteConRecursoAsignado(
                        incidenteId
                );

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.of(incidente)
        );

        RecursoNoDisponibleException esperada =
                new RecursoNoDisponibleException(
                        recursoId
                );

        when(
                recursosGateway.despachar(
                        recursoId,
                        incidenteId,
                        "OPERADOR_CENTRAL"
                )
        ).thenThrow(
                esperada
        );

        RecursoNoDisponibleException resultado =
                assertThrows(
                        RecursoNoDisponibleException.class,
                        () -> casoUso.ejecutar(
                                new DespacharRecursoCommand(
                                        incidenteId,
                                        recursoId,
                                        "OPERADOR_CENTRAL"
                                )
                        )
                );

        assertSame(
                esperada,
                resultado
        );
    }

    @Test
    void debeRechazarRespuestaSinEstadoDespachado() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        Incidente incidente =
                crearIncidenteConRecursoAsignado(
                        incidenteId
                );

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.of(incidente)
        );

        DespachoRecurso respuestaInvalida =
                new DespachoRecurso(
                        recursoId,
                        incidenteId,
                        EstadoRecurso.RESERVADO,
                        LocalDateTime.now()
                );

        when(
                recursosGateway.despachar(
                        recursoId,
                        incidenteId,
                        "OPERADOR_CENTRAL"
                )
        ).thenReturn(
                respuestaInvalida
        );

        IllegalStateException excepcion =
                assertThrows(
                        IllegalStateException.class,
                        () -> casoUso.ejecutar(
                                new DespacharRecursoCommand(
                                        incidenteId,
                                        recursoId,
                                        "OPERADOR_CENTRAL"
                                )
                        )
                );

        assertEquals(
                "Resource Service no confirmo "
                        + "el despacho del recurso",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarComandoNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> casoUso.ejecutar(null)
                );

        assertEquals(
                "El comando de despacho es obligatorio",
                excepcion.getMessage()
        );

        verifyNoInteractions(
                incidenteGateway,
                recursosGateway
        );
    }

    @Test
    void debeRechazarGatewayDeIncidentesNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new DespacharRecursoUseCase(
                                null,
                                recursosGateway
                        )
                );

        assertEquals(
                "El gateway de incidentes es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarGatewayDeRecursosNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new DespacharRecursoUseCase(
                                incidenteGateway,
                                null
                        )
                );

        assertEquals(
                "El gateway de recursos es obligatorio",
                excepcion.getMessage()
        );
    }

    private Incidente crearIncidenteConRecursoAsignado(
            UUID incidenteId
    ) {
        Incidente incidente =
                mock(Incidente.class);

        when(
                incidente.getId()
        ).thenReturn(
                incidenteId
        );

        when(
                incidente.getEstado()
        ).thenReturn(
                EstadoIncidente.RECURSO_ASIGNADO
        );

        return incidente;
    }
}