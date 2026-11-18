package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.exception.DireccionNoEncontradaException;
import com.alertacore.incidentservice.domain.exception.ServicioGeocodificacionException;
import com.alertacore.incidentservice.domain.model.GeocodificacionResultado;
import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;
import com.alertacore.incidentservice.domain.model.command.GeocodificarDireccionCommand;
import com.alertacore.incidentservice.domain.model.gateway.ServicioGeocodificacionGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GeocodificarDireccionUseCaseTest {

    private ServicioGeocodificacionGateway
            servicioGeocodificacion;

    private GeocodificarDireccionUseCase casoUso;

    @BeforeEach
    void configurar() {
        servicioGeocodificacion =
                mock(
                        ServicioGeocodificacionGateway.class
                );

        casoUso =
                new GeocodificarDireccionUseCase(
                        servicioGeocodificacion
                );
    }

    @Test
    void debeGeocodificarDireccionCorrectamente() {
        String direccion =
                "Carrera 7 con Calle 72, Bogota";

        GeocodificarDireccionCommand comando =
                new GeocodificarDireccionCommand(
                        direccion
                );

        ResultadoGeocodificacion resultadoGateway =
                mock(
                        ResultadoGeocodificacion.class
                );

        when(
                servicioGeocodificacion.geocodificar(
                        direccion
                )
        ).thenReturn(
                resultadoGateway
        );

        GeocodificacionResultado resultado =
                casoUso.ejecutar(
                        comando
                );

        assertNotNull(
                resultado
        );

        verify(servicioGeocodificacion)
                .geocodificar(
                        direccion
                );
    }

    @Test
    void debeRechazarComandoNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> casoUso.ejecutar(
                                null
                        )
                );

        assertEquals(
                "El comando de geocodificacion es obligatorio",
                excepcion.getMessage()
        );

        verifyNoInteractions(
                servicioGeocodificacion
        );
    }

    @Test
    void debeRechazarGatewayNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeocodificarDireccionUseCase(
                                        null
                                )
                );

        assertEquals(
                "El servicio de geocodificacion es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debePropagarDireccionNoEncontrada() {
        String direccion =
                "Avenida Inventada XYZ 999999";

        GeocodificarDireccionCommand comando =
                new GeocodificarDireccionCommand(
                        direccion
                );

        DireccionNoEncontradaException esperada =
                mock(
                        DireccionNoEncontradaException.class
                );

        when(
                servicioGeocodificacion.geocodificar(
                        direccion
                )
        ).thenThrow(
                esperada
        );

        DireccionNoEncontradaException resultado =
                assertThrows(
                        DireccionNoEncontradaException.class,
                        () -> casoUso.ejecutar(
                                comando
                        )
                );

        assertSame(
                esperada,
                resultado
        );

        verify(servicioGeocodificacion)
                .geocodificar(
                        direccion
                );
    }

    @Test
    void debePropagarErrorDelServicioDeGeocodificacion() {
        String direccion =
                "Carrera 7 con Calle 72, Bogota";

        GeocodificarDireccionCommand comando =
                new GeocodificarDireccionCommand(
                        direccion
                );

        ServicioGeocodificacionException esperada =
                mock(
                        ServicioGeocodificacionException.class
                );

        when(
                servicioGeocodificacion.geocodificar(
                        direccion
                )
        ).thenThrow(
                esperada
        );

        ServicioGeocodificacionException resultado =
                assertThrows(
                        ServicioGeocodificacionException.class,
                        () -> casoUso.ejecutar(
                                comando
                        )
                );

        assertSame(
                esperada,
                resultado
        );

        verify(servicioGeocodificacion)
                .geocodificar(
                        direccion
                );
    }
}