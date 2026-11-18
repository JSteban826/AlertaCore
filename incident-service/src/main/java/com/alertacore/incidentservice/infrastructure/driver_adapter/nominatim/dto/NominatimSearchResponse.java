package com.alertacore.incidentservice.infrastructure.driver_adapter.nominatim.dto;

public record NominatimSearchResponse(
        String lat,
        String lon,
        String display_name
) {
}