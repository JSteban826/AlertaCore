package com.alertacore.resourceservice.application.port.in;

import com.alertacore.resourceservice.application.dto.RecursoDto;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;

import java.util.List;

public interface BuscarRecursosDisponiblesPort {

    List<RecursoDto> buscar(TipoRecurso tipo);
}