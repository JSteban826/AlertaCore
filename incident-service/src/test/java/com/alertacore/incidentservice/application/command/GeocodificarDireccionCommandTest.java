package com.alertacore.incidentservice.application.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeocodificarDireccionCommandTest {

    @Test
    void debeCrearComandoConDireccionValida() {
        GeocodificarDireccionCommand comando =
                new GeocodificarDireccionCommand(
                        "Carrera 7 # 72-41, Chapinero, Bogota"
                );

        assertEquals(
                "Carrera 7 # 72-41, Chapinero, Bogota",
                comando.direccion()
        );
    }

    @Test
    void debeEliminarEspaciosAlInicioYAlFinal() {
        GeocodificarDireccionCommand comando =
                new GeocodificarDireccionCommand(
                        "   Carrera 7 # 72-41, Bogota   "
                );

        assertEquals(
                "Carrera 7 # 72-41, Bogota",
                comando.direccion()
        );
    }

    @Test
    void debeRechazarDireccionNula() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new GeocodificarDireccionCommand(
                                null
                        )
                );

        assertEquals(
                "La direccion es obligatoria",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarDireccionVacia() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new GeocodificarDireccionCommand(
                                ""
                        )
                );

        assertEquals(
                "La direccion es obligatoria",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarDireccionConSoloEspacios() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new GeocodificarDireccionCommand(
                                "     "
                        )
                );

        assertEquals(
                "La direccion es obligatoria",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarDireccionConMenosDeCincoCaracteres() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new GeocodificarDireccionCommand(
                                "Cll"
                        )
                );

        assertEquals(
                "La direccion debe tener al menos 5 caracteres",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarDireccionQueQuedaCortaDespuesDelTrim() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new GeocodificarDireccionCommand(
                                "   Cll   "
                        )
                );

        assertEquals(
                "La direccion debe tener al menos 5 caracteres",
                excepcion.getMessage()
        );
    }

    @Test
    void debePermitirDireccionConExactamenteCincoCaracteres() {
        GeocodificarDireccionCommand comando =
                new GeocodificarDireccionCommand(
                        "Calle"
                );

        assertEquals(
                5,
                comando.direccion().length()
        );

        assertEquals(
                "Calle",
                comando.direccion()
        );
    }

    @Test
    void debePermitirDireccionConExactamenteDoscientosCincuentaCaracteres() {
        String direccion =
                "A".repeat(250);

        GeocodificarDireccionCommand comando =
                new GeocodificarDireccionCommand(
                        direccion
                );

        assertEquals(
                250,
                comando.direccion().length()
        );

        assertEquals(
                direccion,
                comando.direccion()
        );
    }

    @Test
    void debeRechazarDireccionConMasDeDoscientosCincuentaCaracteres() {
        String direccion =
                "A".repeat(251);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new GeocodificarDireccionCommand(
                                direccion
                        )
                );

        assertEquals(
                "La direccion no puede superar los 250 caracteres",
                excepcion.getMessage()
        );
    }

    @Test
    void debeValidarLongitudDespuesDeEliminarEspacios() {
        String direccion =
                "   " + "A".repeat(250) + "   ";

        GeocodificarDireccionCommand comando =
                new GeocodificarDireccionCommand(
                        direccion
                );

        assertEquals(
                250,
                comando.direccion().length()
        );

        assertEquals(
                "A".repeat(250),
                comando.direccion()
        );
    }

    @Test
    void debeCompararComandosConLaMismaDireccionNormalizada() {
        GeocodificarDireccionCommand primerComando =
                new GeocodificarDireccionCommand(
                        "Carrera 7, Bogota"
                );

        GeocodificarDireccionCommand segundoComando =
                new GeocodificarDireccionCommand(
                        "   Carrera 7, Bogota   "
                );

        assertEquals(
                primerComando,
                segundoComando
        );

        assertEquals(
                primerComando.hashCode(),
                segundoComando.hashCode()
        );
    }

    @Test
    void debeDiferenciarComandosConDireccionesDistintas() {
        GeocodificarDireccionCommand primerComando =
                new GeocodificarDireccionCommand(
                        "Carrera 7, Bogota"
                );

        GeocodificarDireccionCommand segundoComando =
                new GeocodificarDireccionCommand(
                        "Calle 72, Bogota"
                );

        assertNotEquals(
                primerComando,
                segundoComando
        );
    }
}