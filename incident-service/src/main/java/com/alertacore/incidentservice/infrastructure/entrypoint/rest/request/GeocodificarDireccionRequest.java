package com.alertacore.incidentservice.infrastructure.entrypoint.rest.request;

import com.alertacore.incidentservice.application.command.GeocodificarDireccionCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GeocodificarDireccionRequest(

        @NotBlank(
                message = "La direccion es obligatoria"
        )
        @Size(
                min = 5,
                max = 250,
                message = "La direccion debe tener entre 5 y 250 caracteres"
        )
        String direccion
) {

    public GeocodificarDireccionCommand aComando() {
        return new GeocodificarDireccionCommand(
                direccion
        );
    }
}