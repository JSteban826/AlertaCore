package com.alertacore.resourceservice.application.port.in;

import com.alertacore.resourceservice.application.command.RegistrarRecursoCommand;
import com.alertacore.resourceservice.application.dto.RecursoDto;

public interface RegistrarRecursoPort {

    RecursoDto registrar(RegistrarRecursoCommand command);
}