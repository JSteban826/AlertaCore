package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.enums.TipoRecurso;
import com.alertacore.incidentservice.domain.exception.IncidenteNoClasificadoException;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.AsignacionRecursoResultado;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.RecursoDisponible;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.model.ReservaRecurso;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.model.command.AsignarRecursoCommand;
import com.alertacore.incidentservice.domain.model.gateway.HistorialEstadoGateway;
import com.alertacore.incidentservice.domain.model.gateway.IncidenteGateway;
import com.alertacore.incidentservice.domain.model.gateway.ServicioRecursosGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AsignarRecursoUseCaseTest {

    private IncidenteGateway incidenteGateway;
    private HistorialEstadoGateway historialGateway;
    private ServicioRecursosGateway recursosGateway;

    private AsignarRecursoUseCase casoUso;

    @BeforeEach
    void configurar() {
        incidenteGateway =
                mock(IncidenteGateway.class);

        historialGateway =
                mock(HistorialEstadoGateway.class);

        recursosGateway =
                mock(ServicioRecursosGateway.class);

        casoUso =
                new AsignarRecursoUseCase(
                        incidenteGateway,
                        historialGateway,
                        recursosGateway
                );
    }

    @Test
    void debeAsignarRecursoCorrectamente() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();

        LocalDateTime fechaReserva =
                LocalDateTime.of(
                        2026,
                        11,
                        18,
                        16,
                        0
                );

        LocalDateTime fechaVencimiento =
                fechaReserva.plusMinutes(10);

        AsignarRecursoCommand comando =
                crearComando(incidenteId);

        Incidente incidente =
                crearIncidenteClasificado(
                        incidenteId
                );

        RecursoDisponible recurso =
                new RecursoDisponible(
                        recursoId,
                        "AMB-001",
                        TipoRecurso.AMBULANCIA,
                        4.8,
                        12,
                        EstadoRecurso.DISPONIBLE
                );

        ReservaRecurso reserva =
                new ReservaRecurso(
                        reservaId,
                        recursoId,
                        incidenteId,
                        EstadoRecurso.RESERVADO,
                        fechaReserva,
                        fechaVencimiento
                );

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.of(incidente)
        );

        when(
                recursosGateway.buscarMasCercano(
                        incidenteId,
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596,
                        30.0
                )
        ).thenReturn(
                recurso
        );

        when(
                recursosGateway.reservar(
                        eq(recursoId),
                        eq(incidenteId),
                        eq(10),
                        anyString()
                )
        ).thenReturn(
                reserva
        );

        when(
                incidenteGateway.guardar(
                        incidente
                )
        ).thenReturn(
                incidente
        );

        AsignacionRecursoResultado resultado =
                casoUso.ejecutar(comando);

        assertEquals(
                incidenteId,
                resultado.incidenteId()
        );

        assertEquals(
                recursoId,
                resultado.recursoId()
        );

        assertEquals(
                reservaId,
                resultado.reservaId()
        );

        assertEquals(
                "AMB-001",
                resultado.codigoRecurso()
        );

        assertEquals(
                TipoRecurso.AMBULANCIA,
                resultado.tipoRecurso()
        );

        assertEquals(
                EstadoRecurso.RESERVADO,
                resultado.estadoRecurso()
        );

        assertEquals(
                EstadoIncidente.RECURSO_ASIGNADO,
                resultado.estadoIncidente()
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
                fechaReserva,
                resultado.fechaReserva()
        );

        assertEquals(
                fechaVencimiento,
                resultado.fechaVencimiento()
        );

        verify(incidenteGateway)
                .buscarPorId(incidenteId);

        verify(recursosGateway)
                .buscarMasCercano(
                        incidenteId,
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596,
                        30.0
                );

        verify(recursosGateway)
                .reservar(
                        eq(recursoId),
                        eq(incidenteId),
                        eq(10),
                        anyString()
                );

        verify(incidente)
                .cambiarEstado(
                        EstadoIncidente.RECURSO_ASIGNADO
                );

        verify(incidenteGateway)
                .guardar(incidente);

        verify(historialGateway)
                .guardar(
                        any(RegistroHistorialEstado.class)
                );

        verify(
                recursosGateway,
                never()
        ).liberar(
                any(),
                any(),
                anyString()
        );
    }

    @Test
    void debeLanzarExcepcionCuandoIncidenteNoExiste() {
        UUID incidenteId = UUID.randomUUID();

        AsignarRecursoCommand comando =
                crearComando(incidenteId);

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.empty()
        );

        IncidenteNoEncontradoException excepcion =
                assertThrows(
                        IncidenteNoEncontradoException.class,
                        () -> casoUso.ejecutar(comando)
                );

        verify(incidenteGateway)
                .buscarPorId(incidenteId);

        verifyNoInteractions(
                historialGateway,
                recursosGateway
        );
    }

    @Test
    void debeRechazarIncidenteNoClasificado() {
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
                EstadoIncidente.REPORTADO
        );

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.of(incidente)
        );

        IncidenteNoClasificadoException excepcion =
                assertThrows(
                        IncidenteNoClasificadoException.class,
                        () -> casoUso.ejecutar(
                                crearComando(incidenteId)
                        )
                );

        assertEquals(
                "El incidente debe estar CLASIFICADO "
                        + "antes de asignar un recurso: "
                        + incidenteId,
                excepcion.getMessage()
        );

        verifyNoInteractions(
                historialGateway,
                recursosGateway
        );
    }

    @Test
    void debeLiberarReservaCuandoFallaGuardarIncidente() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        Incidente incidente =
                crearIncidenteClasificado(
                        incidenteId
                );

        RecursoDisponible recurso =
                crearRecurso(recursoId);

        ReservaRecurso reserva =
                crearReserva(
                        incidenteId,
                        recursoId
                );

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.of(incidente)
        );

        when(
                recursosGateway.buscarMasCercano(
                        any(),
                        any(),
                        eq(4.6579),
                        eq(-74.0596),
                        eq(30.0)
                )
        ).thenReturn(
                recurso
        );

        when(
                recursosGateway.reservar(
                        eq(recursoId),
                        eq(incidenteId),
                        eq(10),
                        anyString()
                )
        ).thenReturn(
                reserva
        );

        RuntimeException esperada =
                new RuntimeException(
                        "Fallo guardando incidente"
                );

        when(
                incidenteGateway.guardar(
                        incidente
                )
        ).thenThrow(
                esperada
        );

        RuntimeException resultado =
                assertThrows(
                        RuntimeException.class,
                        () -> casoUso.ejecutar(
                                crearComando(incidenteId)
                        )
                );

        assertSame(
                esperada,
                resultado
        );

        verify(recursosGateway)
                .liberar(
                        recursoId,
                        incidenteId,
                        "Compensacion por fallo al guardar "
                                + "la asignacion en incident-service"
                );

        verify(
                historialGateway,
                never()
        ).guardar(any());
    }

    @Test
    void debeLiberarReservaCuandoFallaGuardarHistorial() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        Incidente incidente =
                crearIncidenteClasificado(
                        incidenteId
                );

        RecursoDisponible recurso =
                crearRecurso(recursoId);

        ReservaRecurso reserva =
                crearReserva(
                        incidenteId,
                        recursoId
                );

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.of(incidente)
        );

        when(
                recursosGateway.buscarMasCercano(
                        any(),
                        any(),
                        eq(4.6579),
                        eq(-74.0596),
                        eq(30.0)
                )
        ).thenReturn(
                recurso
        );

        when(
                recursosGateway.reservar(
                        eq(recursoId),
                        eq(incidenteId),
                        eq(10),
                        anyString()
                )
        ).thenReturn(
                reserva
        );

        when(
                incidenteGateway.guardar(
                        incidente
                )
        ).thenReturn(
                incidente
        );

        RuntimeException esperada =
                new RuntimeException(
                        "Fallo guardando historial"
                );

        doThrow(esperada)
                .when(historialGateway)
                .guardar(
                        any(
                                RegistroHistorialEstado.class
                        )
                );

        RuntimeException resultado =
                assertThrows(
                        RuntimeException.class,
                        () -> casoUso.ejecutar(
                                crearComando(incidenteId)
                        )
                );

        assertSame(
                esperada,
                resultado
        );

        verify(recursosGateway)
                .liberar(
                        recursoId,
                        incidenteId,
                        "Compensacion por fallo al guardar "
                                + "la asignacion en incident-service"
                );
    }

    @Test
    void noDebeCompensarCuandoFallaLaBusqueda() {
        UUID incidenteId = UUID.randomUUID();

        Incidente incidente =
                crearIncidenteClasificado(
                        incidenteId
                );

        when(
                incidenteGateway.buscarPorId(
                        incidenteId
                )
        ).thenReturn(
                Optional.of(incidente)
        );

        RuntimeException esperada =
                new RuntimeException(
                        "No hay recursos"
                );

        when(
                recursosGateway.buscarMasCercano(
                        any(),
                        any(),
                        any(Double.class),
                        any(Double.class),
                        any(Double.class)
                )
        ).thenThrow(
                esperada
        );

        assertThrows(
                RuntimeException.class,
                () -> casoUso.ejecutar(
                        crearComando(incidenteId)
                )
        );

        verify(
                recursosGateway,
                never()
        ).reservar(
                any(),
                any(),
                any(Integer.class),
                anyString()
        );

        verify(
                recursosGateway,
                never()
        ).liberar(
                any(),
                any(),
                anyString()
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
                "El comando de asignacion es obligatorio",
                excepcion.getMessage()
        );

        verifyNoInteractions(
                incidenteGateway,
                historialGateway,
                recursosGateway
        );
    }

    @Test
    void debeRechazarGatewayIncidentesNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new AsignarRecursoUseCase(
                                null,
                                historialGateway,
                                recursosGateway
                        )
                );

        assertEquals(
                "El gateway de incidentes es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarGatewayHistorialNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new AsignarRecursoUseCase(
                                incidenteGateway,
                                null,
                                recursosGateway
                        )
                );

        assertEquals(
                "El gateway de historial es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarGatewayRecursosNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new AsignarRecursoUseCase(
                                incidenteGateway,
                                historialGateway,
                                null
                        )
                );

        assertEquals(
                "El gateway de recursos es obligatorio",
                excepcion.getMessage()
        );
    }

    private AsignarRecursoCommand crearComando(
            UUID incidenteId
    ) {
        return new AsignarRecursoCommand(
                incidenteId,
                TipoRecurso.AMBULANCIA,
                30.0,
                10
        );
    }

    private Incidente crearIncidenteClasificado(
            UUID incidenteId
    ) {
        Incidente incidente =
                mock(Incidente.class);

        Ubicacion ubicacion =
                mock(Ubicacion.class);

        when(
                incidente.getId()
        ).thenReturn(
                incidenteId
        );

        /*
         * La primera consulta ocurre antes de cambiar el estado.
         * Las siguientes ocurren después de cambiarlo.
         */
        when(
                incidente.getEstado()
        ).thenReturn(
                EstadoIncidente.CLASIFICADO,
                EstadoIncidente.CLASIFICADO,
                EstadoIncidente.RECURSO_ASIGNADO,
                EstadoIncidente.RECURSO_ASIGNADO
        );

        when(
                incidente.getUbicacion()
        ).thenReturn(
                ubicacion
        );

        when(
                ubicacion.getLatitud()
        ).thenReturn(
                4.6579
        );

        when(
                ubicacion.getLongitud()
        ).thenReturn(
                -74.0596
        );

        return incidente;
    }

    private RecursoDisponible crearRecurso(
            UUID recursoId
    ) {
        return new RecursoDisponible(
                recursoId,
                "AMB-001",
                TipoRecurso.AMBULANCIA,
                4.8,
                12,
                EstadoRecurso.DISPONIBLE
        );
    }

    private ReservaRecurso crearReserva(
            UUID incidenteId,
            UUID recursoId
    ) {
        LocalDateTime fechaReserva =
                LocalDateTime.of(
                        2026,
                        11,
                        18,
                        16,
                        0
                );

        return new ReservaRecurso(
                UUID.randomUUID(),
                recursoId,
                incidenteId,
                EstadoRecurso.RESERVADO,
                fechaReserva,
                fechaReserva.plusMinutes(10)
        );
    }
}