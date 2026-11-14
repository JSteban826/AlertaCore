package com.alertacore.incidentservice.infrastructure.client.openmeteo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenMeteoCurrentResponse(
        double latitude,
        double longitude,
        Current current
) {

    public record Current(
            String time,

            @JsonProperty("temperature_2m")
            Double temperature2m,

            Double precipitation,

            Double rain,

            Double showers,

            @JsonProperty("weather_code")
            Integer weatherCode,

            @JsonProperty("wind_speed_10m")
            Double windSpeed10m
    ) {
    }
}