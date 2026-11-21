package com.alertacore.resourceservice.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ReservaVencidaExceptionTest {

    @Test
    void deberiaCrearExcepcionConCodigoYMensajeCorrectos() {
        ReservaVencidaException excepcion =
                new ReservaVencidaException();

        assertAll(
                () -> assertEquals(
                        "RESERVA_VENCIDA",
                        excepcion.getCodigo()
                ),
                () -> assertEquals(
                        "La reserva ha expirado",
                        excepcion.getMessage()
                ),
                () -> assertInstanceOf(
                        DominioException.class,
                        excepcion
                )
        );
    }
}