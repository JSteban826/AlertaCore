package com.alertacore.incidentservice.domain.gateway;

import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;

import java.util.List;
import java.util.UUID;

public interface RepositorioHistorialEstadoPort {

    RegistroHistorialEstado guardar(
            RegistroHistorialEstado registro
    );

    List<RegistroHistorialEstado>
    buscarPorIncidenteIdOrdenado(
            UUID incidenteId
    );
}