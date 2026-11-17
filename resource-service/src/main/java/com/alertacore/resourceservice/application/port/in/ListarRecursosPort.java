package com.alertacore.resourceservice.application.port.in;

import com.alertacore.resourceservice.application.dto.RecursoDto;

import java.util.List;

public interface ListarRecursosPort {

    List<RecursoDto> listar();
}