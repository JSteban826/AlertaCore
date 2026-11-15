package com.alertacore.resourceservice.domain.model;

import com.alertacore.resourceservice.domain.exception.UbicacionInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UbicacionRecursoTest {

    @Test
    void deberiaCrearUnaUbicacionValida() {
        UbicacionRecurso ubicacion =
                UbicacionRecurso.crear(4.6579, -74.0596);

        assertEquals(4.6579, ubicacion.getLatitud());
        assertEquals(-74.0596, ubicacion.getLongitud());
        assertNotNull(ubicacion.getFechaRegistro());
    }

    @Test
    void deberiaAceptarLosLimitesDeLatitud() {
        assertDoesNotThrow(
                () -> UbicacionRecurso.crear(-90, 0)
        );

        assertDoesNotThrow(
                () -> UbicacionRecurso.crear(90, 0)
        );
    }

    @Test
    void deberiaAceptarLosLimitesDeLongitud() {
        assertDoesNotThrow(
                () -> UbicacionRecurso.crear(0, -180)
        );

        assertDoesNotThrow(
                () -> UbicacionRecurso.crear(0, 180)
        );
    }

    @Test
    void deberiaRechazarLatitudMayorA90() {
        UbicacionInvalidaException excepcion = assertThrows(
                UbicacionInvalidaException.class,
                () -> UbicacionRecurso.crear(90.1, -74.0596)
        );

        assertEquals("UBICACION_INVALIDA", excepcion.getCodigo());
    }

    @Test
    void deberiaRechazarLatitudMenorAMenos90() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> UbicacionRecurso.crear(-90.1, -74.0596)
        );
    }

    @Test
    void deberiaRechazarLongitudMayorA180() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> UbicacionRecurso.crear(4.6579, 180.1)
        );
    }

    @Test
    void deberiaRechazarLongitudMenorAMenos180() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> UbicacionRecurso.crear(4.6579, -180.1)
        );
    }

    @Test
    void deberiaRechazarCoordenadasNoNumericas() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> UbicacionRecurso.crear(Double.NaN, -74.0596)
        );

        assertThrows(
                UbicacionInvalidaException.class,
                () -> UbicacionRecurso.crear(4.6579, Double.POSITIVE_INFINITY)
        );
    }
}