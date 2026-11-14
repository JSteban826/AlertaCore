package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.application.dto.EstadisticasIncidentesResultado;

public interface ConsultarEstadisticasIncidentesPort {

    EstadisticasIncidentesResultado ejecutar();
}