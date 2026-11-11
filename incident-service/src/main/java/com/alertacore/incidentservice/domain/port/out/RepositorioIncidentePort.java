package com.alertacore.incidentservice.domain.port.out;

import com.alertacore.incidentservice.domain.model.Incidente;

import java.util.Optional;
import java.util.UUID;

public interface RepositorioIncidentePort {

    Incidente guardar(Incidente incidente);

    Optional<Incidente> buscarPorId(UUID id);

    boolean existePorId(UUID id);
}