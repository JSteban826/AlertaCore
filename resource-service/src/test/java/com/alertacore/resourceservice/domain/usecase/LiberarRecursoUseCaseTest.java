package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.EstadoReserva;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoLiberableException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.ReservaRecurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.model.command.LiberarRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.LiberarRecursoDto;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import com.alertacore.resourceservice.domain.model.gateway.ReservaRecursoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LiberarRecursoUseCaseTest {

    @Mock
    private RecursoGateway recursoGateway;

    @Mock
    private ReservaRecursoGateway reservaRecursoGateway;

    private LiberarRecursoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase =
                new LiberarRecursoUseCase(
                        recursoGateway,
                        reservaRecursoGateway
                );
    }

    @Test
    void deberiaLiberarUnRecursoDespachado() {
        UUID incidenteId = UUID.randomUUID();

        Recurso recurso =
                crearRecurso();

        recurso.reservar();
        recurso.despachar();

        ReservaRecurso reserva =
                crearReservaActiva(
                        recurso.getId(),
                        incidenteId
                );

        when(
                recursoGateway.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(
                Optional.of(recurso)
        );

        when(
                reservaRecursoGateway
                        .buscarReservaActivaPorRecurso(
                                recurso.getId()
                        )
        ).thenReturn(
                Optional.of(reserva)
        );

        LiberarRecursoDto resultado =
                useCase.liberar(
                        crearCommand(
                                recurso.getId(),
                                incidenteId
                        )
                );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                resultado.estado()
        );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                recurso.getEstado()
        );

        assertEquals(
                EstadoReserva.CANCELADA,
                reserva.getEstadoReserva()
        );

        verify(
                reservaRecursoGateway
        ).guardar(
                reserva
        );

        verify(
                recursoGateway
        ).guardar(
                recurso
        );
    }

    @Test
    void deberiaLiberarUnRecursoEnAtencion() {
        UUID incidenteId = UUID.randomUUID();

        Recurso recurso =
                crearRecurso();

        recurso.reservar();
        recurso.despachar();
        recurso.iniciarAtencion();

        ReservaRecurso reserva =
                crearReservaActiva(
                        recurso.getId(),
                        incidenteId
                );

        when(
                recursoGateway.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(
                Optional.of(recurso)
        );

        when(
                reservaRecursoGateway
                        .buscarReservaActivaPorRecurso(
                                recurso.getId()
                        )
        ).thenReturn(
                Optional.of(reserva)
        );

        LiberarRecursoDto resultado =
                useCase.liberar(
                        crearCommand(
                                recurso.getId(),
                                incidenteId
                        )
                );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                resultado.estado()
        );

        assertEquals(
                EstadoReserva.CANCELADA,
                reserva.getEstadoReserva()
        );

        verify(
                reservaRecursoGateway
        ).guardar(
                reserva
        );

        verify(
                recursoGateway
        ).guardar(
                recurso
        );
    }

    @Test
    void noDeberiaLiberarUnRecursoDisponible() {
        UUID incidenteId = UUID.randomUUID();

        Recurso recurso =
                crearRecurso();

        when(
                recursoGateway.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(
                Optional.of(recurso)
        );

        assertThrows(
                RecursoNoLiberableException.class,
                () -> useCase.liberar(
                        crearCommand(
                                recurso.getId(),
                                incidenteId
                        )
                )
        );

        verifyNoInteractions(
                reservaRecursoGateway
        );

        verify(
                recursoGateway,
                never()
        ).guardar(
                any()
        );
    }

    @Test
    void noDeberiaLiberarUnRecursoReservado() {
        UUID incidenteId = UUID.randomUUID();

        Recurso recurso =
                crearRecurso();

        recurso.reservar();

        when(
                recursoGateway.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(
                Optional.of(recurso)
        );

        assertThrows(
                RecursoNoLiberableException.class,
                () -> useCase.liberar(
                        crearCommand(
                                recurso.getId(),
                                incidenteId
                        )
                )
        );

        verifyNoInteractions(
                reservaRecursoGateway
        );

        verify(
                recursoGateway,
                never()
        ).guardar(
                any()
        );
    }

    @Test
    void noDeberiaLiberarUnRecursoInexistente() {
        UUID recursoId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();

        when(
                recursoGateway.buscarPorId(
                        recursoId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                RecursoNoLiberableException.class,
                () -> useCase.liberar(
                        crearCommand(
                                recursoId,
                                incidenteId
                        )
                )
        );

        verifyNoInteractions(
                reservaRecursoGateway
        );

        verify(
                recursoGateway,
                never()
        ).guardar(
                any()
        );
    }

    @Test
    void noDeberiaLiberarSinReservaActiva() {
        UUID incidenteId = UUID.randomUUID();

        Recurso recurso =
                crearRecurso();

        recurso.reservar();
        recurso.despachar();

        when(
                recursoGateway.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(
                Optional.of(recurso)
        );

        when(
                reservaRecursoGateway
                        .buscarReservaActivaPorRecurso(
                                recurso.getId()
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                RecursoNoLiberableException.class,
                () -> useCase.liberar(
                        crearCommand(
                                recurso.getId(),
                                incidenteId
                        )
                )
        );

        verify(
                reservaRecursoGateway,
                never()
        ).guardar(
                any()
        );

        verify(
                recursoGateway,
                never()
        ).guardar(
                any()
        );
    }

    @Test
    void noDeberiaLiberarReservaDeOtroIncidente() {
        UUID incidenteSolicitanteId =
                UUID.randomUUID();

        UUID incidenteReservaId =
                UUID.randomUUID();

        Recurso recurso =
                crearRecurso();

        recurso.reservar();
        recurso.despachar();

        ReservaRecurso reserva =
                crearReservaActiva(
                        recurso.getId(),
                        incidenteReservaId
                );

        when(
                recursoGateway.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(
                Optional.of(recurso)
        );

        when(
                reservaRecursoGateway
                        .buscarReservaActivaPorRecurso(
                                recurso.getId()
                        )
        ).thenReturn(
                Optional.of(reserva)
        );

        assertThrows(
                RecursoNoLiberableException.class,
                () -> useCase.liberar(
                        crearCommand(
                                recurso.getId(),
                                incidenteSolicitanteId
                        )
                )
        );

        assertEquals(
                EstadoReserva.ACTIVA,
                reserva.getEstadoReserva()
        );

        assertEquals(
                EstadoRecurso.DESPACHADO,
                recurso.getEstado()
        );

        verify(
                reservaRecursoGateway,
                never()
        ).guardar(
                any()
        );

        verify(
                recursoGateway,
                never()
        ).guardar(
                any()
        );
    }

    @Test
    void deberiaRechazarComandoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> useCase.liberar(
                        null
                )
        );

        verifyNoInteractions(
                recursoGateway,
                reservaRecursoGateway
        );
    }

    @Test
    void deberiaRechazarGatewayDeRecursosNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberarRecursoUseCase(
                        null,
                        reservaRecursoGateway
                )
        );
    }

    @Test
    void deberiaRechazarGatewayDeReservasNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberarRecursoUseCase(
                        recursoGateway,
                        null
                )
        );
    }

    private Recurso crearRecurso() {
        return Recurso.crear(
                "AMB-001",
                "Ambulancia Principal",
                TipoRecurso.AMBULANCIA,
                UbicacionRecurso.crear(
                        4.6579,
                        -74.0596
                )
        );
    }

    private ReservaRecurso crearReservaActiva(
            UUID recursoId,
            UUID incidenteId
    ) {
        return ReservaRecurso.crear(
                recursoId,
                incidenteId,
                UUID.randomUUID().toString(),
                10
        );
    }

    private LiberarRecursoCommand crearCommand(
            UUID recursoId,
            UUID incidenteId
    ) {
        return new LiberarRecursoCommand(
                recursoId,
                incidenteId,
                "Atencion finalizada"
        );
    }
}