package com.alertacore.resourceservice.infrastructure.entrypoint.rest.request;

public record ActualizarUbicacionRequest(

        double latitud,

        double longitud

) {
}