package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.model.EstadisticasIncidentes;
import com.alertacore.incidentservice.domain.model.EstadisticasIncidentesResultado;
import com.alertacore.incidentservice.domain.model.gateway.EstadisticasIncidenteGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ConsultarEstadisticasIncidentesUseCaseTest {

    private EstadisticasIncidenteGateway
            estadisticasGateway;

    private ConsultarEstadisticasIncidentesUseCase
            casoUso;

    @BeforeEach
    void configurar() {
        estadisticasGateway =
                mock(
                        EstadisticasIncidenteGateway.class
                );

        casoUso =
                new ConsultarEstadisticasIncidentesUseCase(
                        estadisticasGateway
                );
    }

    @Test
    void debeConsultarEstadisticasCorrectamente() {
        EstadisticasIncidentes estadisticas =
                mock(
                        EstadisticasIncidentes.class
                );

        when(
                estadisticasGateway.obtenerEstadisticas()
        ).thenReturn(
                estadisticas
        );

        EstadisticasIncidentesResultado resultado =
                casoUso.ejecutar();

        assertNotNull(
                resultado
        );

        verify(estadisticasGateway)
                .obtenerEstadisticas();

        verifyNoMoreInteractions(
                estadisticasGateway
        );
    }

    @Test
    void debeRechazarGatewayNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new ConsultarEstadisticasIncidentesUseCase(
                                        null
                                )
                );

        assertEquals(
                "El repositorio de estadisticas es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debePropagarErrorDelGateway() {
        RuntimeException esperada =
                new RuntimeException(
                        "Fallo al consultar estadisticas"
                );

        when(
                estadisticasGateway.obtenerEstadisticas()
        ).thenThrow(
                esperada
        );

        RuntimeException resultado =
                assertThrows(
                        RuntimeException.class,
                        () -> casoUso.ejecutar()
                );

        assertSame(
                esperada,
                resultado
        );

        verify(estadisticasGateway)
                .obtenerEstadisticas();
    }
}