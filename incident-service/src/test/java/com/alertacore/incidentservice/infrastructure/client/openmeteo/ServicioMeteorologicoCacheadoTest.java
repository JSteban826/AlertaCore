package com.alertacore.incidentservice.infrastructure.client.openmeteo;

import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;
import com.alertacore.incidentservice.domain.gateway.ServicioMeteorologicoPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServicioMeteorologicoCacheadoTest {

    @Test
    void debeDelegarConsultaMeteorologica() {
        ServicioMeteorologicoPort delegado =
                mock(ServicioMeteorologicoPort.class);

        ServicioMeteorologicoCacheado servicio =
                new ServicioMeteorologicoCacheado(
                        delegado
                );

        CondicionMeteorologica condicion =
                new CondicionMeteorologica(
                        4.6579,
                        -74.0596,
                        15.4,
                        0,
                        0,
                        0,
                        8.5,
                        3,
                        LocalDateTime.now(),
                        "Open-Meteo"
                );

        when(
                delegado.consultarCondicionActual(
                        4.6579,
                        -74.0596
                )
        ).thenReturn(condicion);

        CondicionMeteorologica resultado =
                servicio.consultarCondicionActual(
                        4.6579,
                        -74.0596
                );

        assertSame(condicion, resultado);

        verify(delegado)
                .consultarCondicionActual(
                        4.6579,
                        -74.0596
                );
    }

    @Test
    void debeRechazarDelegadoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new ServicioMeteorologicoCacheado(
                        null
                )
        );
    }
}