package com.alertacore.incidentservice.infrastructure.transaction;

import com.alertacore.incidentservice.domain.model.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.domain.model.ResultadoIncidente;
import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CambiarEstadoTransaccionalTest {

    @Test
    void debeDelegarCambioDeEstado() {
        CambiarEstadoIncidentePort delegado =
                mock(CambiarEstadoIncidentePort.class);

        CambiarEstadoTransaccional transaccional =
                new CambiarEstadoTransaccional(delegado);

        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        UUID.randomUUID(),
                        EstadoIncidente.VALIDADO,
                        "Informacion verificada"
                );

        ResultadoIncidente resultadoEsperado =
                mock(ResultadoIncidente.class);

        when(delegado.ejecutar(comando))
                .thenReturn(resultadoEsperado);

        ResultadoIncidente resultado =
                transaccional.ejecutar(comando);

        assertSame(resultadoEsperado, resultado);
        verify(delegado).ejecutar(comando);
    }

    @Test
    void debeRechazarDelegadoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new CambiarEstadoTransaccional(null)
        );
    }
}