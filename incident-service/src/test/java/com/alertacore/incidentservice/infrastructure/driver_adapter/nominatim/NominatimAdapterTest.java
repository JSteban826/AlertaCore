package com.alertacore.incidentservice.infrastructure.driver_adapter.nominatim;

import com.alertacore.incidentservice.domain.exception.DireccionNoEncontradaException;
import com.alertacore.incidentservice.domain.exception.ServicioGeocodificacionException;
import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class NominatimAdapterTest {

    private static final String DIRECCION =
            "Carrera 7 # 72-41, Chapinero, Bogota, Colombia";

    private MockRestServiceServer servidor;
    private NominatimAdapter adapter;

    @BeforeEach
    void configurar() {
        RestClient.Builder builder =
                RestClient.builder()
                        .baseUrl(
                                "https://nominatim.openstreetmap.org"
                        )
                        .defaultHeader(
                                "User-Agent",
                                "AlertaCore-IncidentService/1.0"
                        )
                        .defaultHeader(
                                "Accept-Language",
                                "es-CO,es;q=0.9"
                        );

        servidor = MockRestServiceServer
                .bindTo(builder)
                .build();

        adapter = new NominatimAdapter(
                builder.build()
        );
    }

    @Test
    void debeGeocodificarDireccionCorrectamente() {
        String respuesta = """
                [
                  {
                    "lat": "4.6757081",
                    "lon": "-74.041004",
                    "display_name": "Carrera 7, La Gran Via, Localidad Chapinero, Bogota, Colombia"
                  }
                ]
                """;

        servidor.expect(
                        once(),
                        requestTo(
                                containsString("/search")
                        )
                )
                .andExpect(method(HttpMethod.GET))
                .andExpect(
                        requestTo(
                                containsString("format=jsonv2")
                        )
                )
                .andExpect(
                        requestTo(
                                containsString("limit=1")
                        )
                )
                .andExpect(
                        requestTo(
                                containsString("countrycodes=co")
                        )
                )
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        ResultadoGeocodificacion resultado =
                adapter.geocodificar(DIRECCION);

        assertEquals(
                DIRECCION,
                resultado.direccionConsultada()
        );

        assertEquals(
                "Carrera 7, La Gran Via, Localidad Chapinero, Bogota, Colombia",
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

        servidor.verify();
    }

    @Test
    void debeEnviarSolicitudGetANominatim() {
        String respuesta = """
                [
                  {
                    "lat": "4.6757081",
                    "lon": "-74.041004",
                    "display_name": "Chapinero, Bogota, Colombia"
                  }
                ]
                """;

        servidor.expect(
                        once(),
                        requestTo(
                                containsString("/search")
                        )
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        adapter.geocodificar(DIRECCION);

        servidor.verify();
    }

    @Test
    void debeRechazarDireccionNoEncontrada() {
        servidor.expect(
                        requestTo(
                                containsString("/search")
                        )
                )
                .andRespond(
                        withSuccess(
                                "[]",
                                MediaType.APPLICATION_JSON
                        )
                );

        DireccionNoEncontradaException excepcion =
                assertThrows(
                        DireccionNoEncontradaException.class,
                        () -> adapter.geocodificar(
                                "Avenida Inventada XYZ 999999, Bogota, Colombia"
                        )
                );

        assertEquals(
                """
                No se encontraron coordenadas para la direccion: \
                Avenida Inventada XYZ 999999, Bogota, Colombia\
                """,
                excepcion.getMessage()
        );

        servidor.verify();
    }

    @Test
    void debeConvertirLatitudInvalidaEnExcepcionDelServicio() {
        String respuesta = """
                [
                  {
                    "lat": "latitud-invalida",
                    "lon": "-74.041004",
                    "display_name": "Chapinero, Bogota, Colombia"
                  }
                ]
                """;

        servidor.expect(
                        requestTo(
                                containsString("/search")
                        )
                )
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        ServicioGeocodificacionException excepcion =
                assertThrows(
                        ServicioGeocodificacionException.class,
                        () -> adapter.geocodificar(
                                DIRECCION
                        )
                );

        assertEquals(
                "No fue posible consultar Nominatim",
                excepcion.getMessage()
        );

        assertNotNull(
                excepcion.getCause()
        );

        servidor.verify();
    }

    @Test
    void debeConvertirLongitudInvalidaEnExcepcionDelServicio() {
        String respuesta = """
                [
                  {
                    "lat": "4.6757081",
                    "lon": "longitud-invalida",
                    "display_name": "Chapinero, Bogota, Colombia"
                  }
                ]
                """;

        servidor.expect(
                        requestTo(
                                containsString("/search")
                        )
                )
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        ServicioGeocodificacionException excepcion =
                assertThrows(
                        ServicioGeocodificacionException.class,
                        () -> adapter.geocodificar(
                                DIRECCION
                        )
                );

        assertEquals(
                "No fue posible consultar Nominatim",
                excepcion.getMessage()
        );

        assertNotNull(
                excepcion.getCause()
        );

        servidor.verify();
    }

    @Test
    void debeRechazarCoordenadasFueraDelRango() {
        String respuesta = """
                [
                  {
                    "lat": "100.0",
                    "lon": "-74.041004",
                    "display_name": "Ubicacion invalida"
                  }
                ]
                """;

        servidor.expect(
                        requestTo(
                                containsString("/search")
                        )
                )
                .andRespond(
                        withSuccess(
                                respuesta,
                                MediaType.APPLICATION_JSON
                        )
                );

        ServicioGeocodificacionException excepcion =
                assertThrows(
                        ServicioGeocodificacionException.class,
                        () -> adapter.geocodificar(
                                DIRECCION
                        )
                );

        assertEquals(
                "No fue posible consultar Nominatim",
                excepcion.getMessage()
        );

        assertNotNull(
                excepcion.getCause()
        );

        servidor.verify();
    }

    @Test
    void debeConvertirErrorHttpEnExcepcionDelServicio() {
        servidor.expect(
                        requestTo(
                                containsString("/search")
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
                                          "error": "Service unavailable"
                                        }
                                        """
                                )
                );

        ServicioGeocodificacionException excepcion =
                assertThrows(
                        ServicioGeocodificacionException.class,
                        () -> adapter.geocodificar(
                                DIRECCION
                        )
                );

        assertEquals(
                "No fue posible consultar Nominatim",
                excepcion.getMessage()
        );

        assertNotNull(
                excepcion.getCause()
        );

        servidor.verify();
    }

    @Test
    void debeConvertirJsonInvalidoEnExcepcionDelServicio() {
        servidor.expect(
                        requestTo(
                                containsString("/search")
                        )
                )
                .andRespond(
                        withSuccess(
                                "{ json-invalido }",
                                MediaType.APPLICATION_JSON
                        )
                );

        ServicioGeocodificacionException excepcion =
                assertThrows(
                        ServicioGeocodificacionException.class,
                        () -> adapter.geocodificar(
                                DIRECCION
                        )
                );

        assertEquals(
                "No fue posible consultar Nominatim",
                excepcion.getMessage()
        );

        assertNotNull(
                excepcion.getCause()
        );

        servidor.verify();
    }

    @Test
    void debeRechazarDireccionNula() {
        assertThrows(
                NullPointerException.class,
                () -> adapter.geocodificar(null)
        );
    }

    @Test
    void debeRechazarRestClientNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new NominatimAdapter(null)
        );
    }
}