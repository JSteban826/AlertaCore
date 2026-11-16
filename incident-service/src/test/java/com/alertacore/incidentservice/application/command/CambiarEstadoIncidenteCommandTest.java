package com.alertacore.incidentservice.application.command;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CambiarEstadoIncidenteCommandTest {

    @Test
    void debeCrearComandoValido() {
        UUID incidenteId = UUID.randomUUID();

        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        "La informacion fue verificada"
                );

        assertEquals(
                incidenteId,
                comando.incidenteId()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                comando.nuevoEstado()
        );

        assertEquals(
                "La informacion fue verificada",
                comando.motivo()
        );
    }

    @Test
    void debeEliminarEspaciosDelMotivo() {
        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        UUID.randomUUID(),
                        EstadoIncidente.VALIDADO,
                        "   La informacion fue verificada   "
                );

        assertEquals(
                "La informacion fue verificada",
                comando.motivo()
        );
    }

    @Test
    void debeRechazarIdentificadorNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new CambiarEstadoIncidenteCommand(
                                null,
                                EstadoIncidente.VALIDADO,
                                "Informacion verificada"
                        )
                );

        assertEquals(
                "El identificador del incidente es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarNuevoEstadoNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new CambiarEstadoIncidenteCommand(
                                UUID.randomUUID(),
                                null,
                                "Informacion verificada"
                        )
                );

        assertEquals(
                "El nuevo estado del incidente es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarMotivoNulo() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new CambiarEstadoIncidenteCommand(
                                UUID.randomUUID(),
                                EstadoIncidente.VALIDADO,
                                null
                        )
                );

        assertEquals(
                "El motivo del cambio de estado es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarMotivoVacio() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new CambiarEstadoIncidenteCommand(
                                UUID.randomUUID(),
                                EstadoIncidente.VALIDADO,
                                ""
                        )
                );

        assertEquals(
                "El motivo del cambio de estado es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarMotivoConSoloEspacios() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new CambiarEstadoIncidenteCommand(
                                UUID.randomUUID(),
                                EstadoIncidente.VALIDADO,
                                "     "
                        )
                );

        assertEquals(
                "El motivo del cambio de estado es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debePermitirMotivoConUnCaracter() {
        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        UUID.randomUUID(),
                        EstadoIncidente.VALIDADO,
                        "A"
                );

        assertEquals(
                "A",
                comando.motivo()
        );
    }

    @Test
    void debePermitirMotivoConExactamenteDoscientosCincuentaCaracteres() {
        String motivo = "A".repeat(250);

        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        UUID.randomUUID(),
                        EstadoIncidente.VALIDADO,
                        motivo
                );

        assertEquals(
                250,
                comando.motivo().length()
        );

        assertEquals(
                motivo,
                comando.motivo()
        );
    }

    @Test
    void debeRechazarMotivoConMasDeDoscientosCincuentaCaracteres() {
        String motivo = "A".repeat(251);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new CambiarEstadoIncidenteCommand(
                                UUID.randomUUID(),
                                EstadoIncidente.VALIDADO,
                                motivo
                        )
                );

        assertEquals(
                "El motivo no puede superar los 250 caracteres",
                excepcion.getMessage()
        );
    }

    @Test
    void debeValidarLongitudDespuesDeEliminarEspacios() {
        String motivo =
                "   " + "A".repeat(250) + "   ";

        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        UUID.randomUUID(),
                        EstadoIncidente.VALIDADO,
                        motivo
                );

        assertEquals(
                250,
                comando.motivo().length()
        );

        assertEquals(
                "A".repeat(250),
                comando.motivo()
        );
    }

    @Test
    void debeRechazarMotivoQueSigueSuperandoElLimiteDespuesDelTrim() {
        String motivo =
                "   " + "A".repeat(251) + "   ";

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new CambiarEstadoIncidenteCommand(
                                UUID.randomUUID(),
                                EstadoIncidente.VALIDADO,
                                motivo
                        )
                );

        assertEquals(
                "El motivo no puede superar los 250 caracteres",
                excepcion.getMessage()
        );
    }

    @Test
    void debeCompararComandosConLosMismosValoresNormalizados() {
        UUID incidenteId = UUID.randomUUID();

        CambiarEstadoIncidenteCommand primerComando =
                new CambiarEstadoIncidenteCommand(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        "Informacion verificada"
                );

        CambiarEstadoIncidenteCommand segundoComando =
                new CambiarEstadoIncidenteCommand(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        "   Informacion verificada   "
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
    void debeDiferenciarComandosConEstadosDistintos() {
        UUID incidenteId = UUID.randomUUID();

        CambiarEstadoIncidenteCommand primerComando =
                new CambiarEstadoIncidenteCommand(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        "Cambio permitido"
                );

        CambiarEstadoIncidenteCommand segundoComando =
                new CambiarEstadoIncidenteCommand(
                        incidenteId,
                        EstadoIncidente.CANCELADO,
                        "Cambio permitido"
                );

        assertNotEquals(
                primerComando,
                segundoComando
        );
    }

    @Test
    void debeDiferenciarComandosConMotivosDistintos() {
        UUID incidenteId = UUID.randomUUID();

        CambiarEstadoIncidenteCommand primerComando =
                new CambiarEstadoIncidenteCommand(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        "Informacion verificada"
                );

        CambiarEstadoIncidenteCommand segundoComando =
                new CambiarEstadoIncidenteCommand(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        "Solicitud cancelada"
                );

        assertNotEquals(
                primerComando,
                segundoComando
        );
    }
}