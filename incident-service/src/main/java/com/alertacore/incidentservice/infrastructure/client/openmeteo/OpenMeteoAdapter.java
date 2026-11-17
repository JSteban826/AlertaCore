package com.alertacore.incidentservice.infrastructure.client.openmeteo;

import com.alertacore.incidentservice.domain.exception.ServicioMeteorologicoException;
import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;
import com.alertacore.incidentservice.domain.gateway.ServicioMeteorologicoPort;
import com.alertacore.incidentservice.infrastructure.client.openmeteo.dto.OpenMeteoCurrentResponse;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Objects;

public final class OpenMeteoAdapter
        implements ServicioMeteorologicoPort {

    private static final String FUENTE = "Open-Meteo";

    private final RestClient restClient;

    public OpenMeteoAdapter(
            RestClient restClient
    ) {
        this.restClient = Objects.requireNonNull(
                restClient,
                "El cliente HTTP es obligatorio"
        );
    }

    @Override
    public CondicionMeteorologica consultarCondicionActual(
            double latitud,
            double longitud
    ) {
        try {
            OpenMeteoCurrentResponse respuesta =
                    restClient.get()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path("/v1/forecast")
                                            .queryParam(
                                                    "latitude",
                                                    latitud
                                            )
                                            .queryParam(
                                                    "longitude",
                                                    longitud
                                            )
                                            .queryParam(
                                                    "current",
                                                    "temperature_2m,"
                                                            + "precipitation,"
                                                            + "rain,"
                                                            + "showers,"
                                                            + "weather_code,"
                                                            + "wind_speed_10m"
                                            )
                                            .queryParam(
                                                    "timezone",
                                                    "America/Bogota"
                                            )
                                            .build()
                            )
                            .retrieve()
                            .body(
                                    OpenMeteoCurrentResponse.class
                            );

            if (respuesta == null
                    || respuesta.current() == null) {

                throw new ServicioMeteorologicoException(
                        "Open-Meteo devolvio una respuesta vacia"
                );
            }

            OpenMeteoCurrentResponse.Current actual =
                    respuesta.current();

            return new CondicionMeteorologica(
                    respuesta.latitude(),
                    respuesta.longitude(),
                    valor(actual.temperature2m()),
                    valor(actual.precipitation()),
                    valor(actual.rain()),
                    valor(actual.showers()),
                    valor(actual.windSpeed10m()),
                    actual.weatherCode() == null
                            ? 0
                            : actual.weatherCode(),
                    convertirFecha(actual.time()),
                    FUENTE
            );
        } catch (ServicioMeteorologicoException excepcion) {
            throw excepcion;
        } catch (Exception excepcion) {
            throw new ServicioMeteorologicoException(
                    "No fue posible consultar Open-Meteo",
                    excepcion
            );
        }
    }

    private double valor(Double valor) {
        return valor == null ? 0 : valor;
    }

    private LocalDateTime convertirFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return LocalDateTime.now();
        }

        return LocalDateTime.parse(fecha);
    }
}