package com.alertacore.incidentservice.application.command;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;

public record RegistrarIncidenteCommand(
        TipoIncidente tipo,
        String descripcion,
        double latitud,
        double longitud,
        String direccion,
        String nombreReportante,
        String correoReportante,
        String telefonoReportante,
        int cantidadPersonasAfectadas,
        boolean hayPersonasHeridas
) {
}