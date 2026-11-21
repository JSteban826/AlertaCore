package com.alertacore.notificationservice.domain.model.gateway;

import java.util.UUID;

public interface ConsultaDespachoGateway {

    IncidenteDetalle consultarIncidente(UUID incidenteId);

    RecursoDetalle consultarRecurso(UUID recursoId);

    record IncidenteDetalle(
            UUID id,
            String tipo,
            String descripcion,
            String estado,
            String nivelRiesgo,
            int cantidadPersonasAfectadas,
            boolean hayPersonasHeridas,
            double latitud,
            double longitud,
            String direccion,
            String fechaReporte,
            String fechaUltimaActualizacion
    ) {}

    record RecursoDetalle(
            UUID id,
            String codigo,
            String nombre,
            String tipo,
            String estado,
            double latitud,
            double longitud
    ) {}
}