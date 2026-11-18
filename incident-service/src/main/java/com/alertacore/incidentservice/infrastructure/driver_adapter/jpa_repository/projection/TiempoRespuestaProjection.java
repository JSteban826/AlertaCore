package com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.projection;

public interface TiempoRespuestaProjection {

    Double getPromedioMinutos();

    Double getMinimoMinutos();

    Double getMaximoMinutos();

    Long getIncidentesValidados();
}
