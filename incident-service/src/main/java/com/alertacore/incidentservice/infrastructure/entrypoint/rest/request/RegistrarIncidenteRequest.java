package com.alertacore.incidentservice.infrastructure.entrypoint.rest.request;

import com.alertacore.incidentservice.application.command.RegistrarIncidenteCommand;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RegistrarIncidenteRequest(

        @NotNull(
                message = "El tipo de incidente es obligatorio"
        )
        TipoIncidente tipo,

        @NotBlank(
                message = "La descripcion es obligatoria"
        )
        @Size(
                min = 10,
                max = 500,
                message = "La descripcion debe tener entre 10 y 500 caracteres"
        )
        String descripcion,

        @Min(
                value = -90,
                message = "La latitud debe ser mayor o igual a -90"
        )
        @Max(
                value = 90,
                message = "La latitud debe ser menor o igual a 90"
        )
        double latitud,

        @Min(
                value = -180,
                message = "La longitud debe ser mayor o igual a -180"
        )
        @Max(
                value = 180,
                message = "La longitud debe ser menor o igual a 180"
        )
        double longitud,

        @NotBlank(
                message = "La direccion es obligatoria"
        )
        @Size(
                max = 250,
                message = "La direccion no puede superar los 250 caracteres"
        )
        String direccion,

        @NotBlank(
                message = "El nombre del reportante es obligatorio"
        )
        @Size(
                max = 150,
                message = "El nombre no puede superar los 150 caracteres"
        )
        String nombreReportante,

        @NotBlank(
                message = "El correo del reportante es obligatorio"
        )
        @Email(
                message = "El correo del reportante no es valido"
        )
        @Size(
                max = 200,
                message = "El correo no puede superar los 200 caracteres"
        )
        String correoReportante,

        @NotBlank(
                message = "El telefono del reportante es obligatorio"
        )
        @Size(
                min = 7,
                max = 30,
                message = "El telefono debe tener entre 7 y 30 caracteres"
        )
        String telefonoReportante,

        @PositiveOrZero(
                message = "La cantidad de personas afectadas no puede ser negativa"
        )
        int cantidadPersonasAfectadas,

        boolean hayPersonasHeridas
) {

    public RegistrarIncidenteCommand aComando() {
        return new RegistrarIncidenteCommand(
                tipo,
                descripcion,
                latitud,
                longitud,
                direccion,
                nombreReportante,
                correoReportante,
                telefonoReportante,
                cantidadPersonasAfectadas,
                hayPersonasHeridas
        );
    }
}