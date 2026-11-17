package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.domain.model.EstadisticasIncidentesResultado;

public interface ConsultarEstadisticasIncidentesPort {

    EstadisticasIncidentesResultado ejecutar();
}