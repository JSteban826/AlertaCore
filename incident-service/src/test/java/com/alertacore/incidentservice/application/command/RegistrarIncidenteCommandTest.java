package com.alertacore.incidentservice.application.command;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class RegistrarIncidenteCommandTest {

    @Test
    void debeCrearComandoConTodosLosDatos() {
        RegistrarIncidenteCommand comando =
                new RegistrarIncidenteCommand(
                        TipoIncidente.INUNDACION,
                        "Inundacion con varias personas afectadas",
                        4.6579,
                        -74.0596,
                        "Carrera 7 con Calle 72, Bogota",
                        "Operador de prueba",
                        "operador@alertacore.com",
                        "3001234567",
                        15,
                        true
                );

        assertEquals(
                TipoIncidente.INUNDACION,
                comando.tipo()
        );

        assertEquals(
                "Inundacion con varias personas afectadas",
                comando.descripcion()
        );

        assertEquals(
                4.6579,
                comando.latitud()
        );

        assertEquals(
                -74.0596,
                comando.longitud()
        );

        assertEquals(
                "Carrera 7 con Calle 72, Bogota",
                comando.direccion()
        );

        assertEquals(
                "Operador de prueba",
                comando.nombreReportante()
        );

        assertEquals(
                "operador@alertacore.com",
                comando.correoReportante()
        );

        assertEquals(
                "3001234567",
                comando.telefonoReportante()
        );

        assertEquals(
                15,
                comando.cantidadPersonasAfectadas()
        );

        assertEquals(
                true,
                comando.hayPersonasHeridas()
        );
    }

    @Test
    void debePermitirCeroPersonasAfectadas() {
        RegistrarIncidenteCommand comando =
                new RegistrarIncidenteCommand(
                        TipoIncidente.FALLA_ELECTRICA,
                        "Falla electrica sin personas afectadas",
                        4.6579,
                        -74.0596,
                        "Chapinero, Bogota",
                        "Operador de prueba",
                        "operador@alertacore.com",
                        "3001234567",
                        0,
                        false
                );

        assertEquals(
                0,
                comando.cantidadPersonasAfectadas()
        );

        assertFalse(
                comando.hayPersonasHeridas()
        );
    }

    @Test
    void debeConservarCoordenadasLimiteValidas() {
        RegistrarIncidenteCommand comando =
                new RegistrarIncidenteCommand(
                        TipoIncidente.INCENDIO,
                        "Incendio registrado en coordenadas limite",
                        -90.0,
                        180.0,
                        "Ubicacion geografica limite",
                        "Operador de prueba",
                        "operador@alertacore.com",
                        "3001234567",
                        1,
                        false
                );

        assertEquals(
                -90.0,
                comando.latitud()
        );

        assertEquals(
                180.0,
                comando.longitud()
        );
    }

    @Test
    void debeCompararComandosConLosMismosValores() {
        RegistrarIncidenteCommand primerComando =
                crearComandoValido();

        RegistrarIncidenteCommand segundoComando =
                crearComandoValido();

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
    void debeDiferenciarComandosConValoresDistintos() {
        RegistrarIncidenteCommand primerComando =
                crearComandoValido();

        RegistrarIncidenteCommand segundoComando =
                new RegistrarIncidenteCommand(
                        TipoIncidente.INCENDIO,
                        "Incendio estructural con personas afectadas",
                        4.6579,
                        -74.0596,
                        "Carrera 7 con Calle 72, Bogota",
                        "Operador de prueba",
                        "operador@alertacore.com",
                        "3001234567",
                        15,
                        true
                );

        assertFalse(
                primerComando.equals(segundoComando)
        );
    }

    @Test
    void debeAceptarCamposNulosPorqueElComandoNoValida() {
        RegistrarIncidenteCommand comando =
                new RegistrarIncidenteCommand(
                        null,
                        null,
                        0.0,
                        0.0,
                        null,
                        null,
                        null,
                        null,
                        0,
                        false
                );

        assertNull(
                comando.tipo()
        );

        assertNull(
                comando.descripcion()
        );

        assertNull(
                comando.direccion()
        );

        assertNull(
                comando.nombreReportante()
        );

        assertNull(
                comando.correoReportante()
        );

        assertNull(
                comando.telefonoReportante()
        );
    }

    private RegistrarIncidenteCommand crearComandoValido() {
        return new RegistrarIncidenteCommand(
                TipoIncidente.INUNDACION,
                "Inundacion con varias personas afectadas",
                4.6579,
                -74.0596,
                "Carrera 7 con Calle 72, Bogota",
                "Operador de prueba",
                "operador@alertacore.com",
                "3001234567",
                15,
                true
        );
    }
}