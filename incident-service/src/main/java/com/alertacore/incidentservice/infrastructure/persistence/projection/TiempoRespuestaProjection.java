package com.alertacore.incidentservice.infrastructure.persistence.projection;

public interface TiempoRespuestaProjection {

    Double getPromedioMinutos();

    Double getMinimoMinutos();

    Double getMaximoMinutos();

    Long getIncidentesValidados();
}
