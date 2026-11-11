package com.alertacore.incidentservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UbicacionTest {

    @Test
    void debeCrearUbicacionValida() {
        Ubicacion ubicacion = new Ubicacion(
                4.6579,
                -74.0596,
                "Carrera 7 con Calle 72, Bogota"
        );

        assertEquals(4.6579, ubicacion.getLatitud());
        assertEquals(-74.0596, ubicacion.getLongitud());
        assertEquals(
                "Carrera 7 con Calle 72, Bogota",
                ubicacion.getDireccion()
        );
    }

    @Test
    void debeRechazarLatitudMayorANoventa() {
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> new Ubicacion(
                        91,
                        -74.0596,
                        "Bogota"
                )
        );

        assertEquals(
                "La latitud debe estar entre -90 y 90",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarLongitudInvalida() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ubicacion(
                        4.6579,
                        -181,
                        "Bogota"
                )
        );
    }

    @Test
    void debeRechazarDireccionVacia() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ubicacion(
                        4.6579,
                        -74.0596,
                        " "
                )
        );
    }
}