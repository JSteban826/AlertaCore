package com.alertacore.resourceservice.domain.port.out;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioRecursoPort {

    Recurso guardar(Recurso recurso);

    Optional<Recurso> buscarPorId(UUID id);

    Optional<Recurso> buscarPorCodigo(String codigo);

    List<Recurso> buscarDisponiblesPorTipo(TipoRecurso tipo);

    boolean existePorCodigo(String codigo);

    List<Recurso> buscarTodos();
}
