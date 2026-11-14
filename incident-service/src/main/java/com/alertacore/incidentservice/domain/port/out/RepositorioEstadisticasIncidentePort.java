package com.alertacore.incidentservice.domain.port.out;

import com.alertacore.incidentservice.domain.model.EstadisticasIncidentes;

public interface RepositorioEstadisticasIncidentePort {

    EstadisticasIncidentes obtenerEstadisticas();
}