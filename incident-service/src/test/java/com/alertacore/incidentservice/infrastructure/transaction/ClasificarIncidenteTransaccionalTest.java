package com.alertacore.incidentservice.infrastructure.transaction;

import com.alertacore.incidentservice.domain.model.command.ClasificarIncidenteCommand;
import com.alertacore.incidentservice.domain.model.ResultadoClasificacion;
import com.alertacore.incidentservice.application.port.in.ClasificarIncidentePort;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClasificarIncidenteTransaccionalTest {

    @Test
    void debeDelegarClasificacion() {
        ClasificarIncidentePort delegado =
                mock(ClasificarIncidentePort.class);

        ClasificarIncidenteTransaccional transaccional =
                new ClasificarIncidenteTransaccional(delegado);

        ClasificarIncidenteCommand comando =
                new ClasificarIncidenteCommand(
                        UUID.randomUUID(),
                        true,
                        true
                );

        ResultadoClasificacion resultadoEsperado =
                mock(ResultadoClasificacion.class);

        when(delegado.ejecutar(comando))
                .thenReturn(resultadoEsperado);

        ResultadoClasificacion resultado =
                transaccional.ejecutar(comando);

        assertSame(resultadoEsperado, resultado);
        verify(delegado).ejecutar(comando);
    }

    @Test
    void debeRechazarDelegadoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new ClasificarIncidenteTransaccional(
                        null
                )
        );
    }
}