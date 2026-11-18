package com.alertacore.incidentservice.infrastructure.driver_adapter.openmeteo;

import com.alertacore.incidentservice.domain.enums.NivelRiesgoMeteorologico;
import com.alertacore.incidentservice.domain.exception.ServicioMeteorologicoException;
import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OpenMeteoAdapterTest {

    private MockRestServiceServer servidor;
    private OpenMeteoAdapter adapter;

    @BeforeEach
    void configurar() {
        RestClient.Builder builder =
                RestClient.builder()
                        .baseUrl(
                                "https://api.open-meteo.com"
                        );

        servidor = MockRestServiceServer
                .bindTo(builder)
                .build();

        adapter = new OpenMeteoAdapter(
                builder.build()
        );
    }

    @Test
    void debeConsultarCondicionMeteorologicaActual() {
        String respuesta = """
                {
                  "latitude": 4.6579,
                  "longitude": -74.0596,
                  "current": {
                    "time": "2026-11-16T13:30",
                    "temperature_2m": 16.4,
                    "precipitation": 8.2,
                    "rain": 7.8,
                    "showers": 0.4,
                    "weather_code": 63,
                    "wind_speed_10m": 12.5
                  }
                }
                """;

        servidor.expect(
                        once(),
                        requestTo(
                                containsString(
                                        "/v1/forecast"
                                )
                        )
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        CondicionMeteorologica resultado =
                adapter.consultarCondicionActual(
                        4.6579,
                        -74.0596
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

        assertEquals(
                LocalDateTime.of(
                        2026,
                        11,
                        16,
                        13,
                        30
                ),
                resultado.fechaConsulta()
        );

        assertEquals(
                "Open-Meteo",
                resultado.fuente()
        );

        assertTrue(
                resultado.hayLluviaIntensa()
        );

        assertEquals(
                NivelRiesgoMeteorologico.ALTO,
                resultado.obtenerNivelRiesgo()
        );

        servidor.verify();
    }

    @Test
    void debeConvertirValoresMeteorologicosNulosEnCero() {
        String respuesta = """
                {
                  "latitude": 4.6579,
                  "longitude": -74.0596,
                  "current": {
                    "time": "2026-11-16T13:30",
                    "temperature_2m": null,
                    "precipitation": null,
                    "rain": null,
                    "showers": null,
                    "weather_code": null,
                    "wind_speed_10m": null
                  }
                }
                """;

        servidor.expect(
                        requestTo(
                                containsString(
                                        "/v1/forecast"
                                )
                        )
                )
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        CondicionMeteorologica resultado =
                adapter.consultarCondicionActual(
                        4.6579,
                        -74.0596
                );

        assertEquals(
                0.0,
                resultado.temperaturaCelsius()
        );

        assertEquals(
                0.0,
                resultado.precipitacionMilimetros()
        );

        assertEquals(
                0.0,
                resultado.lluviaMilimetros()
        );

        assertEquals(
                0.0,
                resultado.chubascosMilimetros()
        );

        assertEquals(
                0.0,
                resultado.velocidadVientoKmH()
        );

        assertEquals(
                0,
                resultado.codigoMeteorologico()
        );

        assertFalse(
                resultado.hayLluviaIntensa()
        );

        assertEquals(
                NivelRiesgoMeteorologico.BAJO,
                resultado.obtenerNivelRiesgo()
        );

        servidor.verify();
    }

    @Test
    void debeUsarFechaActualCuandoOpenMeteoNoEnviaFecha() {
        String respuesta = """
                {
                  "latitude": 4.6579,
                  "longitude": -74.0596,
                  "current": {
                    "time": "",
                    "temperature_2m": 16.4,
                    "precipitation": 0,
                    "rain": 0,
                    "showers": 0,
                    "weather_code": 3,
                    "wind_speed_10m": 8.5
                  }
                }
                """;

        servidor.expect(
                        requestTo(
                                containsString(
                                        "/v1/forecast"
                                )
                        )
                )
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        LocalDateTime antes =
                LocalDateTime.now();

        CondicionMeteorologica resultado =
                adapter.consultarCondicionActual(
                        4.6579,
                        -74.0596
                );

        LocalDateTime despues =
                LocalDateTime.now();

        assertNotNull(
                resultado.fechaConsulta()
        );

        assertFalse(
                resultado.fechaConsulta()
                        .isBefore(antes)
        );

        assertFalse(
                resultado.fechaConsulta()
                        .isAfter(despues)
        );

        servidor.verify();
    }

    @Test
    void debeRechazarRespuestaSinCondicionActual() {
        String respuesta = """
                {
                  "latitude": 4.6579,
                  "longitude": -74.0596,
                  "current": null
                }
                """;

        servidor.expect(
                        requestTo(
                                containsString(
                                        "/v1/forecast"
                                )
                        )
                )
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        ServicioMeteorologicoException excepcion =
                assertThrows(
                        ServicioMeteorologicoException.class,
                        () -> adapter
                                .consultarCondicionActual(
                                        4.6579,
                                        -74.0596
                                )
                );

        assertEquals(
                "Open-Meteo devolvio una respuesta vacia",
                excepcion.getMessage()
        );

        servidor.verify();
    }

    @Test
    void debeConvertirErrorHttpEnExcepcionMeteorologica() {
        servidor.expect(
                        requestTo(
                                containsString(
                                        "/v1/forecast"
                                )
                        )
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withStatus(
                                HttpStatus.SERVICE_UNAVAILABLE
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .body(
                                        """
                                        {
                                          "error": true,
                                          "reason": "Service unavailable"
                                        }
                                        """
                                )
                );

        ServicioMeteorologicoException excepcion =
                assertThrows(
                        ServicioMeteorologicoException.class,
                        () -> adapter
                                .consultarCondicionActual(
                                        4.6579,
                                        -74.0596
                                )
                );

        assertEquals(
                "No fue posible consultar Open-Meteo",
                excepcion.getMessage()
        );

        assertNotNull(
                excepcion.getCause()
        );

        servidor.verify();
    }

    @Test
    void debeConvertirJsonInvalidoEnExcepcionMeteorologica() {
        servidor.expect(
                        requestTo(
                                containsString(
                                        "/v1/forecast"
                                )
                        )
                )
                .andRespond(
                        withSuccess(
                                "{ json-invalido }",
                                MediaType.APPLICATION_JSON
                        )
                );

        ServicioMeteorologicoException excepcion =
                assertThrows(
                        ServicioMeteorologicoException.class,
                        () -> adapter
                                .consultarCondicionActual(
                                        4.6579,
                                        -74.0596
                                )
                );

        assertEquals(
                "No fue posible consultar Open-Meteo",
                excepcion.getMessage()
        );

        assertNotNull(
                excepcion.getCause()
        );

        servidor.verify();
    }

    @Test
    void debeRechazarRestClientNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new OpenMeteoAdapter(null)
        );
    }
}