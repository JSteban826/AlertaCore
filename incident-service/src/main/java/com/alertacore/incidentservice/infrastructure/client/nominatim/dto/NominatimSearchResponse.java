package com.alertacore.incidentservice.infrastructure.client.nominatim.dto;

public record NominatimSearchResponse(
        String lat,
        String lon,
        String display_name
) {
}