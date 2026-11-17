package com.alertacore.incidentservice.domain.gateway;

import com.alertacore.incidentservice.domain.model.EstadisticasIncidentes;

public interface RepositorioEstadisticasIncidentePort {

    EstadisticasIncidentes obtenerEstadisticas();
}