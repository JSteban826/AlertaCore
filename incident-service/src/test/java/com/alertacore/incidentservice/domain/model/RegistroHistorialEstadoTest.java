package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistroHistorialEstadoTest {

    @Test
    void debeCrearRegistroValido() {
        UUID incidenteId = UUID.randomUUID();

        RegistroHistorialEstado registro =
                RegistroHistorialEstado.crear(
                        incidenteId,
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        "La informacion fue verificada",
                        "OPERADOR_API"
                );

        assertNotNull(registro.id());
        assertNotNull(registro.fechaCambio());

        assertEquals(
                incidenteId,
                registro.incidenteId()
        );

        assertEquals(
                EstadoIncidente.REPORTADO,
                registro.estadoAnterior()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                registro.nuevoEstado()
        );
    }

    @Test
    void debeRechazarMotivoVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> RegistroHistorialEstado.crear(
                        UUID.randomUUID(),
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        " ",
                        "OPERADOR_API"
                )
        );
    }

    @Test
    void debeRechazarResponsableVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> RegistroHistorialEstado.crear(
                        UUID.randomUUID(),
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        "Informacion verificada",
                        " "
                )
        );
    }
}