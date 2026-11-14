package com.alertacore.incidentservice.domain.port.out;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.Pagina;

import java.util.Optional;
import java.util.UUID;

public interface RepositorioIncidentePort {

    Incidente guardar(Incidente incidente);

    Optional<Incidente> buscarPorId(UUID id);

    boolean existePorId(UUID id);

    Pagina<Incidente> buscarPaginado(
            EstadoIncidente estado,
            TipoIncidente tipo,
            int numeroPagina,
            int tamanioPagina
    );
}