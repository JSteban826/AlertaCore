package com.alertacore.incidentservice.domain.model.gateway;

import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;

import java.util.List;
import java.util.UUID;

public interface HistorialEstadoGateway {

    RegistroHistorialEstado guardar(
            RegistroHistorialEstado registro
    );

    List<RegistroHistorialEstado>
    buscarPorIncidenteIdOrdenado(
            UUID incidenteId
    );
}