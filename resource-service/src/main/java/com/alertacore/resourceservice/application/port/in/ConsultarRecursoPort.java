package com.alertacore.resourceservice.application.port.in;

import com.alertacore.resourceservice.application.dto.RecursoDto;

import java.util.UUID;

public interface ConsultarRecursoPort {

    RecursoDto consultar(UUID recursoId);
}