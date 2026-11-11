package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PuntajeRiesgoTest {

    @Test
    void debeRetornarRiesgoBajo() {
        PuntajeRiesgo puntaje = new PuntajeRiesgo(24);

        assertEquals(NivelRiesgo.BAJO, puntaje.obtenerNivel());
        assertEquals(
                120,
                puntaje.obtenerTiempoMaximoRespuestaMinutos()
        );
    }

    @Test
    void debeRetornarRiesgoMedio() {
        PuntajeRiesgo puntaje = new PuntajeRiesgo(25);

        assertEquals(NivelRiesgo.MEDIO, puntaje.obtenerNivel());
        assertEquals(
                60,
                puntaje.obtenerTiempoMaximoRespuestaMinutos()
        );
    }

    @Test
    void debeRetornarRiesgoAlto() {
        PuntajeRiesgo puntaje = new PuntajeRiesgo(50);

        assertEquals(NivelRiesgo.ALTO, puntaje.obtenerNivel());
        assertEquals(
                30,
                puntaje.obtenerTiempoMaximoRespuestaMinutos()
        );
    }

    @Test
    void debeRetornarRiesgoCritico() {
        PuntajeRiesgo puntaje = new PuntajeRiesgo(75);

        assertEquals(NivelRiesgo.CRITICO, puntaje.obtenerNivel());
        assertEquals(
                10,
                puntaje.obtenerTiempoMaximoRespuestaMinutos()
        );
    }

    @Test
    void debeRechazarPuntajeMayorACien() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PuntajeRiesgo(101)
        );
    }

    @Test
    void debeRechazarPuntajeNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PuntajeRiesgo(-1)
        );
    }
}