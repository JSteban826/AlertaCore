package com.alertacore.resourceservice.domain.service;

import com.alertacore.resourceservice.domain.exception.UbicacionInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadorDistanciaHaversineTest {

    private CalculadorDistanciaHaversine calculador;

    @BeforeEach
    void setUp() {
        calculador = new CalculadorDistanciaHaversine();
    }

    @Test
    void deberiaRetornarCeroParaLaMismaUbicacion() {
        double distancia = calculador.calcularKilometros(
                4.6579,
                -74.0596,
                4.6579,
                -74.0596
        );

        assertEquals(0.0, distancia, 0.001);
    }

    @Test
    void deberiaCalcularLaDistanciaEntreDosUbicaciones() {
        double distancia = calculador.calcularKilometros(
                4.6579,
                -74.0596,
                4.6612,
                -74.0634
        );

        assertTrue(distancia > 0);
        assertTrue(distancia < 1);
    }

    @Test
    void deberiaCalcularUnaDistanciaConocidaEntreBogotaYMedellin() {
        double distancia = calculador.calcularKilometros(
                4.7110,
                -74.0721,
                6.2442,
                -75.5812
        );

        /*
         * La distancia geodésica aproximada es cercana a 240 km.
         * Se utiliza tolerancia porque las coordenadas y el radio
         * terrestre son aproximados.
         */
        assertEquals(240.0, distancia, 10.0);
    }

    @Test
    void deberiaObtenerLaMismaDistanciaEnAmbasDirecciones() {
        double distanciaOrigenDestino =
                calculador.calcularKilometros(
                        4.7110,
                        -74.0721,
                        6.2442,
                        -75.5812
                );

        double distanciaDestinoOrigen =
                calculador.calcularKilometros(
                        6.2442,
                        -75.5812,
                        4.7110,
                        -74.0721
                );

        assertEquals(
                distanciaOrigenDestino,
                distanciaDestinoOrigen,
                0.001
        );
    }

    @Test
    void deberiaAceptarCoordenadasEnLosLimites() {
        assertDoesNotThrow(
                () -> calculador.calcularKilometros(
                        -90,
                        -180,
                        90,
                        180
                )
        );
    }

    @Test
    void deberiaRechazarLatitudDeOrigenInvalida() {
        UbicacionInvalidaException excepcion = assertThrows(
                UbicacionInvalidaException.class,
                () -> calculador.calcularKilometros(
                        90.1,
                        -74.0596,
                        4.6612,
                        -74.0634
                )
        );

        assertEquals(
                "UBICACION_INVALIDA",
                excepcion.getCodigo()
        );
    }

    @Test
    void deberiaRechazarLongitudDeOrigenInvalida() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> calculador.calcularKilometros(
                        4.6579,
                        -180.1,
                        4.6612,
                        -74.0634
                )
        );
    }

    @Test
    void deberiaRechazarLatitudDeDestinoInvalida() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> calculador.calcularKilometros(
                        4.6579,
                        -74.0596,
                        91,
                        -74.0634
                )
        );
    }

    @Test
    void deberiaRechazarLongitudDeDestinoInvalida() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> calculador.calcularKilometros(
                        4.6579,
                        -74.0596,
                        4.6612,
                        181
                )
        );
    }

    @Test
    void deberiaRechazarValoresNoFinitos() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> calculador.calcularKilometros(
                        Double.NaN,
                        -74.0596,
                        4.6612,
                        -74.0634
                )
        );

        assertThrows(
                UbicacionInvalidaException.class,
                () -> calculador.calcularKilometros(
                        4.6579,
                        -74.0596,
                        4.6612,
                        Double.POSITIVE_INFINITY
                )
        );
    }
}