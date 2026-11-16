package com.alertacore.incidentservice.application.dto;

import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeocodificacionResultadoTest {

    @Test
    void debeConvertirResultadoDeDominioADto() {
        ResultadoGeocodificacion resultadoDominio =
                new ResultadoGeocodificacion(
                        "Carrera 7 # 72-41, Chapinero, Bogota, Colombia",
                        "Carrera 7, La Gran Via, Localidad Chapinero, "
                                + "Bogota, Colombia",
                        4.6757081,
                        -74.041004,
                        "OpenStreetMap Nominatim",
                        "© OpenStreetMap contributors"
                );

        GeocodificacionResultado resultado =
                GeocodificacionResultado.desde(
                        resultadoDominio
                );

        assertEquals(
                "Carrera 7 # 72-41, Chapinero, Bogota, Colombia",
                resultado.direccionConsultada()
        );

        assertEquals(
                "Carrera 7, La Gran Via, Localidad Chapinero, "
                        + "Bogota, Colombia",
                resultado.direccionNormalizada()
        );

        assertEquals(
                4.6757081,
                resultado.latitud()
        );

        assertEquals(
                -74.041004,
                resultado.longitud()
        );

        assertEquals(
                "OpenStreetMap Nominatim",
                resultado.fuente()
        );

        assertEquals(
                "© OpenStreetMap contributors",
                resultado.atribucion()
        );
    }

    @Test
    void debeConservarCoordenadasLimiteValidas() {
        ResultadoGeocodificacion resultadoDominio =
                new ResultadoGeocodificacion(
                        "Punto geografico de prueba",
                        "Punto geografico normalizado",
                        -90.0,
                        180.0,
                        "OpenStreetMap Nominatim",
                        "© OpenStreetMap contributors"
                );

        GeocodificacionResultado resultado =
                GeocodificacionResultado.desde(
                        resultadoDominio
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
    void debeRechazarResultadoDeDominioNulo() {
        assertThrows(
                NullPointerException.class,
                () -> GeocodificacionResultado.desde(
                        null
                )
        );
    }
}