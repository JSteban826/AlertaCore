package com.alertacore.resourceservice.application.command;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.UbicacionInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrarRecursoCommandTest {

    @Test
    void deberiaCrearUnComandoValido() {
        RegistrarRecursoCommand command =
                new RegistrarRecursoCommand(
                        "AMB-001",
                        "Ambulancia principal",
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596
                );

        assertEquals("AMB-001", command.codigo());
        assertEquals(
                "Ambulancia principal",
                command.nombre()
        );
        assertEquals(
                TipoRecurso.AMBULANCIA,
                command.tipo()
        );
        assertEquals(4.6579, command.latitud());
        assertEquals(-74.0596, command.longitud());
    }

    @Test
    void deberiaEliminarEspaciosExternos() {
        RegistrarRecursoCommand command =
                new RegistrarRecursoCommand(
                        "  AMB-001  ",
                        "  Ambulancia principal  ",
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596
                );

        assertEquals("AMB-001", command.codigo());
        assertEquals(
                "Ambulancia principal",
                command.nombre()
        );
    }

    @Test
    void deberiaRechazarCodigoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegistrarRecursoCommand(
                        null,
                        "Ambulancia principal",
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596
                )
        );
    }

    @Test
    void deberiaRechazarCodigoVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegistrarRecursoCommand(
                        "   ",
                        "Ambulancia principal",
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596
                )
        );
    }

    @Test
    void deberiaRechazarNombreVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegistrarRecursoCommand(
                        "AMB-001",
                        " ",
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596
                )
        );
    }

    @Test
    void deberiaRechazarTipoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new RegistrarRecursoCommand(
                        "AMB-001",
                        "Ambulancia principal",
                        null,
                        4.6579,
                        -74.0596
                )
        );
    }

    @Test
    void deberiaRechazarLatitudInvalida() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> new RegistrarRecursoCommand(
                        "AMB-001",
                        "Ambulancia principal",
                        TipoRecurso.AMBULANCIA,
                        91,
                        -74.0596
                )
        );
    }

    @Test
    void deberiaRechazarLongitudInvalida() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> new RegistrarRecursoCommand(
                        "AMB-001",
                        "Ambulancia principal",
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -181
                )
        );
    }

    @Test
    void deberiaRechazarCoordenadasNoFinitas() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> new RegistrarRecursoCommand(
                        "AMB-001",
                        "Ambulancia principal",
                        TipoRecurso.AMBULANCIA,
                        Double.NaN,
                        -74.0596
                )
        );
    }
}