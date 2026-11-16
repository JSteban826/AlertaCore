package com.alertacore.incidentservice.application.dto;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.model.EstadisticasIncidentes;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadisticasIncidentesResultadoTest {

    @Test
    void debeConvertirEstadisticasCompletasAResultado() {
        Map<EstadoIncidente, Long> cantidadPorEstado =
                new EnumMap<>(EstadoIncidente.class);

        cantidadPorEstado.put(
                EstadoIncidente.REPORTADO,
                2L
        );

        cantidadPorEstado.put(
                EstadoIncidente.VALIDADO,
                1L
        );

        cantidadPorEstado.put(
                EstadoIncidente.CLASIFICADO,
                3L
        );

        Map<NivelRiesgo, Long> cantidadPorNivel =
                new EnumMap<>(NivelRiesgo.class);

        cantidadPorNivel.put(
                NivelRiesgo.BAJO,
                1L
        );

        cantidadPorNivel.put(
                NivelRiesgo.ALTO,
                2L
        );

        cantidadPorNivel.put(
                NivelRiesgo.CRITICO,
                1L
        );

        EstadisticasIncidentes estadisticas =
                new EstadisticasIncidentes(
                        8L,
                        6L,
                        2L,
                        8.46,
                        2.31,
                        14.60,
                        cantidadPorEstado,
                        cantidadPorNivel,
                        2L
                );

        EstadisticasIncidentesResultado resultado =
                EstadisticasIncidentesResultado.desde(
                        estadisticas
                );

        assertEquals(
                8L,
                resultado.totalIncidentes()
        );

        assertEquals(
                6L,
                resultado.incidentesValidados()
        );

        assertEquals(
                2L,
                resultado.incidentesSinValidar()
        );

        assertEquals(
                8.46,
                resultado.tiempoPromedioRespuestaMinutos()
        );

        assertEquals(
                2.31,
                resultado.tiempoMinimoRespuestaMinutos()
        );

        assertEquals(
                14.60,
                resultado.tiempoMaximoRespuestaMinutos()
        );

        assertEquals(
                2L,
                resultado.cantidadPorEstado()
                        .get("REPORTADO")
        );

        assertEquals(
                1L,
                resultado.cantidadPorEstado()
                        .get("VALIDADO")
        );

        assertEquals(
                3L,
                resultado.cantidadPorEstado()
                        .get("CLASIFICADO")
        );

        assertEquals(
                1L,
                resultado.cantidadPorNivelRiesgo()
                        .get("BAJO")
        );

        assertEquals(
                2L,
                resultado.cantidadPorNivelRiesgo()
                        .get("ALTO")
        );

        assertEquals(
                1L,
                resultado.cantidadPorNivelRiesgo()
                        .get("CRITICO")
        );

        assertEquals(
                2L,
                resultado.cantidadPorNivelRiesgo()
                        .get("SIN_CLASIFICAR")
        );
    }

    @Test
    void debeAgregarSinClasificarCuandoLaCantidadEsMayorQueCero() {
        EstadisticasIncidentes estadisticas =
                new EstadisticasIncidentes(
                        4L,
                        2L,
                        2L,
                        5.0,
                        2.0,
                        8.0,
                        Map.of(
                                EstadoIncidente.REPORTADO,
                                2L,
                                EstadoIncidente.CLASIFICADO,
                                2L
                        ),
                        Map.of(
                                NivelRiesgo.CRITICO,
                                2L
                        ),
                        2L
                );

        EstadisticasIncidentesResultado resultado =
                EstadisticasIncidentesResultado.desde(
                        estadisticas
                );

        assertTrue(
                resultado.cantidadPorNivelRiesgo()
                        .containsKey("SIN_CLASIFICAR")
        );

        assertEquals(
                2L,
                resultado.cantidadPorNivelRiesgo()
                        .get("SIN_CLASIFICAR")
        );
    }

    @Test
    void noDebeAgregarSinClasificarCuandoLaCantidadEsCero() {
        EstadisticasIncidentes estadisticas =
                new EstadisticasIncidentes(
                        3L,
                        3L,
                        0L,
                        4.5,
                        2.0,
                        7.0,
                        Map.of(
                                EstadoIncidente.CLASIFICADO,
                                3L
                        ),
                        Map.of(
                                NivelRiesgo.MEDIO,
                                1L,
                                NivelRiesgo.ALTO,
                                2L
                        ),
                        0L
                );

        EstadisticasIncidentesResultado resultado =
                EstadisticasIncidentesResultado.desde(
                        estadisticas
                );

        assertFalse(
                resultado.cantidadPorNivelRiesgo()
                        .containsKey("SIN_CLASIFICAR")
        );

        assertEquals(
                2,
                resultado.cantidadPorNivelRiesgo()
                        .size()
        );
    }

    @Test
    void debeConvertirEstadisticasVacias() {
        EstadisticasIncidentes estadisticas =
                EstadisticasIncidentes.vacias();

        EstadisticasIncidentesResultado resultado =
                EstadisticasIncidentesResultado.desde(
                        estadisticas
                );

        assertEquals(
                0L,
                resultado.totalIncidentes()
        );

        assertEquals(
                0L,
                resultado.incidentesValidados()
        );

        assertEquals(
                0L,
                resultado.incidentesSinValidar()
        );

        assertEquals(
                0.0,
                resultado.tiempoPromedioRespuestaMinutos()
        );

        assertEquals(
                0.0,
                resultado.tiempoMinimoRespuestaMinutos()
        );

        assertEquals(
                0.0,
                resultado.tiempoMaximoRespuestaMinutos()
        );

        assertTrue(
                resultado.cantidadPorEstado().isEmpty()
        );

        assertTrue(
                resultado.cantidadPorNivelRiesgo()
                        .isEmpty()
        );
    }

    @Test
    void debeConvertirLosEnumsEnNombresDeTexto() {
        EstadisticasIncidentes estadisticas =
                new EstadisticasIncidentes(
                        4L,
                        4L,
                        0L,
                        6.0,
                        1.0,
                        10.0,
                        Map.of(
                                EstadoIncidente.REPORTADO,
                                1L,
                                EstadoIncidente.VALIDADO,
                                1L,
                                EstadoIncidente.CLASIFICADO,
                                2L
                        ),
                        Map.of(
                                NivelRiesgo.BAJO,
                                1L,
                                NivelRiesgo.MEDIO,
                                1L,
                                NivelRiesgo.ALTO,
                                1L,
                                NivelRiesgo.CRITICO,
                                1L
                        ),
                        0L
                );

        EstadisticasIncidentesResultado resultado =
                EstadisticasIncidentesResultado.desde(
                        estadisticas
                );

        assertTrue(
                resultado.cantidadPorEstado()
                        .containsKey("REPORTADO")
        );

        assertTrue(
                resultado.cantidadPorEstado()
                        .containsKey("VALIDADO")
        );

        assertTrue(
                resultado.cantidadPorEstado()
                        .containsKey("CLASIFICADO")
        );

        assertTrue(
                resultado.cantidadPorNivelRiesgo()
                        .containsKey("BAJO")
        );

        assertTrue(
                resultado.cantidadPorNivelRiesgo()
                        .containsKey("MEDIO")
        );

        assertTrue(
                resultado.cantidadPorNivelRiesgo()
                        .containsKey("ALTO")
        );

        assertTrue(
                resultado.cantidadPorNivelRiesgo()
                        .containsKey("CRITICO")
        );
    }

    @Test
    void debeCrearMapasInmutables() {
        EstadisticasIncidentes estadisticas =
                new EstadisticasIncidentes(
                        1L,
                        1L,
                        0L,
                        3.5,
                        3.5,
                        3.5,
                        Map.of(
                                EstadoIncidente.VALIDADO,
                                1L
                        ),
                        Map.of(
                                NivelRiesgo.BAJO,
                                1L
                        ),
                        0L
                );

        EstadisticasIncidentesResultado resultado =
                EstadisticasIncidentesResultado.desde(
                        estadisticas
                );

        assertThrows(
                UnsupportedOperationException.class,
                () -> resultado.cantidadPorEstado()
                        .put(
                                "CERRADO",
                                1L
                        )
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> resultado.cantidadPorNivelRiesgo()
                        .put(
                                "CRITICO",
                                2L
                        )
        );
    }

    @Test
    void debeRechazarEstadisticasNulas() {
        assertThrows(
                NullPointerException.class,
                () -> EstadisticasIncidentesResultado.desde(
                        null
                )
        );
    }
}