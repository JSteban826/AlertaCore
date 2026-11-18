package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.command.BuscarRecursoMasCercanoCommand;
import com.alertacore.resourceservice.domain.model.dto.RecursoCercanoDto;
import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoDisponibleNoEncontradoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import com.alertacore.resourceservice.domain.service.CalculadorDistanciaHaversine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarRecursoMasCercanoUseCaseTest {

    @Mock
    private RecursoGateway recursoGateway;

    private BuscarRecursoMasCercanoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BuscarRecursoMasCercanoUseCase(
                recursoGateway,
                new CalculadorDistanciaHaversine()
        );
    }

    @Test
    void deberiaEncontrarElRecursoMasCercano() {
        Recurso recursoCercano = crearRecurso(
                "AMB-001",
                4.6585,
                -74.0600
        );

        Recurso recursoLejano = crearRecurso(
                "AMB-002",
                4.7000,
                -74.1000
        );

        when(
                recursoGateway.buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                )
        ).thenReturn(List.of(recursoLejano, recursoCercano));

        BuscarRecursoMasCercanoCommand command =
                crearCommand(30);

        RecursoCercanoDto resultado = useCase.buscar(command);

        assertEquals(
                recursoCercano.getId(),
                resultado.recursoId()
        );
        assertEquals("AMB-001", resultado.codigo());
        assertEquals(
                TipoRecurso.AMBULANCIA,
                resultado.tipo()
        );
        assertEquals(
                EstadoRecurso.DISPONIBLE,
                resultado.estado()
        );
        assertTrue(resultado.distanciaKm() >= 0);
        assertTrue(resultado.tiempoEstimadoLlegadaMinutos() >= 1);

        verify(recursoGateway)
                .buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                );
    }

    @Test
    void deberiaIgnorarUnRecursoQueNoEstaDisponible() {
        Recurso recursoReservado = crearRecurso(
                "AMB-001",
                4.6580,
                -74.0600
        );
        recursoReservado.reservar();

        Recurso recursoDisponible = crearRecurso(
                "AMB-002",
                4.6700,
                -74.0700
        );

        when(
                recursoGateway.buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                )
        ).thenReturn(
                List.of(recursoReservado, recursoDisponible)
        );

        RecursoCercanoDto resultado =
                useCase.buscar(crearCommand(30));

        assertEquals(
                recursoDisponible.getId(),
                resultado.recursoId()
        );
        assertEquals("AMB-002", resultado.codigo());
    }

    @Test
    void deberiaRechazarRecursosFueraDeLaDistanciaMaxima() {
        Recurso recursoLejano = crearRecurso(
                "AMB-001",
                6.2442,
                -75.5812
        );

        when(
                recursoGateway.buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                )
        ).thenReturn(List.of(recursoLejano));

        RecursoDisponibleNoEncontradoException excepcion =
                assertThrows(
                        RecursoDisponibleNoEncontradoException.class,
                        () -> useCase.buscar(crearCommand(10))
                );

        assertEquals(
                "RECURSO_DISPONIBLE_NO_ENCONTRADO",
                excepcion.getCodigo()
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoNoHayRecursos() {
        when(
                recursoGateway.buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                )
        ).thenReturn(List.of());

        assertThrows(
                RecursoDisponibleNoEncontradoException.class,
                () -> useCase.buscar(crearCommand(30))
        );
    }

    @Test
    void deberiaConsultarSolamenteElTipoSolicitado() {
        when(
                recursoGateway.buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                )
        ).thenReturn(List.of());

        assertThrows(
                RecursoDisponibleNoEncontradoException.class,
                () -> useCase.buscar(crearCommand(30))
        );

        verify(recursoGateway, times(1))
                .buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                );

        verifyNoMoreInteractions(recursoGateway);
    }

    private BuscarRecursoMasCercanoCommand crearCommand(
            double distanciaMaximaKm
    ) {
        return new BuscarRecursoMasCercanoCommand(
                UUID.randomUUID(),
                TipoRecurso.AMBULANCIA,
                4.6579,
                -74.0596,
                distanciaMaximaKm
        );
    }

    private Recurso crearRecurso(
            String codigo,
            double latitud,
            double longitud
    ) {
        return Recurso.crear(
                codigo,
                "Recurso " + codigo,
                TipoRecurso.AMBULANCIA,
                UbicacionRecurso.crear(latitud, longitud)
        );
    }
}