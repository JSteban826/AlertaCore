package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.NivelRiesgoMeteorologico;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CondicionMeteorologicaTest {

    @Test
    void debeClasificarRiesgoBajo() {
        CondicionMeteorologica condicion =
                crearCondicion(1.4);

        assertEquals(
                NivelRiesgoMeteorologico.BAJO,
                condicion.obtenerNivelRiesgo()
        );

        assertFalse(
                condicion.hayLluviaIntensa()
        );
    }

    @Test
    void debeClasificarRiesgoMedio() {
        CondicionMeteorologica condicion =
                crearCondicion(4.5);

        assertEquals(
                NivelRiesgoMeteorologico.MEDIO,
                condicion.obtenerNivelRiesgo()
        );

        assertFalse(
                condicion.hayLluviaIntensa()
        );
    }

    @Test
    void debeClasificarRiesgoAlto() {
        CondicionMeteorologica condicion =
                crearCondicion(7.5);

        assertEquals(
                NivelRiesgoMeteorologico.ALTO,
                condicion.obtenerNivelRiesgo()
        );

        assertTrue(
                condicion.hayLluviaIntensa()
        );
    }

    @Test
    void debeClasificarRiesgoCritico() {
        CondicionMeteorologica condicion =
                crearCondicion(15.0);

        assertEquals(
                NivelRiesgoMeteorologico.CRITICO,
                condicion.obtenerNivelRiesgo()
        );

        assertTrue(
                condicion.hayLluviaIntensa()
        );
    }

    @Test
    void debeRechazarPrecipitacionNegativa() {
        assertThrows(
                IllegalArgumentException.class,
                () -> crearCondicion(-1)
        );
    }

    @Test
    void debeRechazarLatitudInvalida() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CondicionMeteorologica(
                        100,
                        -74.0596,
                        15,
                        0,
                        0,
                        0,
                        12,
                        0,
                        LocalDateTime.now(),
                        "Open-Meteo"
                )
        );
    }

    private CondicionMeteorologica crearCondicion(
            double precipitacion
    ) {
        return new CondicionMeteorologica(
                4.6579,
                -74.0596,
                15.4,
                precipitacion,
                precipitacion,
                0,
                12.5,
                63,
                LocalDateTime.now(),
                "Open-Meteo"
        );
    }
}