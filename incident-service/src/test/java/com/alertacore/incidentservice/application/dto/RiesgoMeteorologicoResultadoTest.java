package com.alertacore.incidentservice.application.dto;

import com.alertacore.incidentservice.domain.enums.NivelRiesgoMeteorologico;
import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;
import com.alertacore.incidentservice.domain.model.RiesgoMeteorologicoResultado;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RiesgoMeteorologicoResultadoTest {

    @Test
    void debeConvertirCondicionMeteorologicaAlDto() {
        UUID incidenteId = UUID.randomUUID();

        LocalDateTime fechaConsulta =
                LocalDateTime.of(
                        2026,
                        11,
                        16,
                        14,
                        30
                );

        CondicionMeteorologica condicion =
                new CondicionMeteorologica(
                        4.6579,
                        -74.0596,
                        16.4,
                        8.2,
                        7.8,
                        0.4,
                        12.5,
                        63,
                        fechaConsulta,
                        "Open-Meteo"
                );

        RiesgoMeteorologicoResultado resultado =
                RiesgoMeteorologicoResultado.desde(
                        incidenteId,
                        condicion
                );

        assertEquals(
                incidenteId,
                resultado.incidenteId()
        );

        assertEquals(
                4.6579,
                resultado.latitud()
        );

        assertEquals(
                -74.0596,
                resultado.longitud()
        );

        assertEquals(
                16.4,
                resultado.temperaturaCelsius()
        );

        assertEquals(
                8.2,
                resultado.precipitacionMilimetros()
        );

        assertEquals(
                7.8,
                resultado.lluviaMilimetros()
        );

        assertEquals(
                0.4,
                resultado.chubascosMilimetros()
        );

        assertEquals(
                12.5,
                resultado.velocidadVientoKmH()
        );

        assertEquals(
                63,
                resultado.codigoMeteorologico()
        );

        assertTrue(
                resultado.lluviaIntensa()
        );

        assertEquals(
                NivelRiesgoMeteorologico.ALTO,
                resultado.nivelRiesgoMeteorologico()
        );

        assertEquals(
                fechaConsulta,
                resultado.fechaConsulta()
        );

        assertEquals(
                "Open-Meteo",
                resultado.fuente()
        );
    }

    @Test
    void debeCrearResultadoConRiesgoBajo() {
        UUID incidenteId = UUID.randomUUID();

        CondicionMeteorologica condicion =
                new CondicionMeteorologica(
                        4.6579,
                        -74.0596,
                        18.0,
                        0.0,
                        0.0,
                        0.0,
                        5.0,
                        1,
                        LocalDateTime.now(),
                        "Open-Meteo"
                );

        RiesgoMeteorologicoResultado resultado =
                RiesgoMeteorologicoResultado.desde(
                        incidenteId,
                        condicion
                );

        assertFalse(
                resultado.lluviaIntensa()
        );

        assertEquals(
                NivelRiesgoMeteorologico.BAJO,
                resultado.nivelRiesgoMeteorologico()
        );

        assertEquals(
                0.0,
                resultado.precipitacionMilimetros()
        );
    }

    @Test
    void debeCrearResultadoConRiesgoMedio() {
        UUID incidenteId = UUID.randomUUID();

        CondicionMeteorologica condicion =
                new CondicionMeteorologica(
                        4.6579,
                        -74.0596,
                        17.5,
                        5.0,
                        5.0,
                        0.0,
                        7.5,
                        51,
                        LocalDateTime.now(),
                        "Open-Meteo"
                );

        RiesgoMeteorologicoResultado resultado =
                RiesgoMeteorologicoResultado.desde(
                        incidenteId,
                        condicion
                );

        assertFalse(
                resultado.lluviaIntensa()
        );

        assertEquals(
                NivelRiesgoMeteorologico.MEDIO,
                resultado.nivelRiesgoMeteorologico()
        );
    }

    @Test
    void debeCrearResultadoConRiesgoCritico() {
        UUID incidenteId = UUID.randomUUID();

        CondicionMeteorologica condicion =
                new CondicionMeteorologica(
                        4.6579,
                        -74.0596,
                        14.0,
                        15.0,
                        12.0,
                        3.0,
                        20.0,
                        65,
                        LocalDateTime.now(),
                        "Open-Meteo"
                );

        RiesgoMeteorologicoResultado resultado =
                RiesgoMeteorologicoResultado.desde(
                        incidenteId,
                        condicion
                );

        assertTrue(
                resultado.lluviaIntensa()
        );

        assertEquals(
                NivelRiesgoMeteorologico.CRITICO,
                resultado.nivelRiesgoMeteorologico()
        );
    }

    @Test
    void debeConservarCoordenadasLimiteValidas() {
        UUID incidenteId = UUID.randomUUID();

        CondicionMeteorologica condicion =
                new CondicionMeteorologica(
                        -90.0,
                        180.0,
                        10.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0,
                        LocalDateTime.now(),
                        "Open-Meteo"
                );

        RiesgoMeteorologicoResultado resultado =
                RiesgoMeteorologicoResultado.desde(
                        incidenteId,
                        condicion
                );

        assertEquals(
                -90.0,
                resultado.latitud()
        );

        assertEquals(
                180.0,
                resultado.longitud()
        );
    }

    @Test
    void debeRechazarCondicionMeteorologicaNula() {
        assertThrows(
                NullPointerException.class,
                () -> RiesgoMeteorologicoResultado.desde(
                        UUID.randomUUID(),
                        null
                )
        );
    }
}