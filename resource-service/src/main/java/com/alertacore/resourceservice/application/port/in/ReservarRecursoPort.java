package com.alertacore.resourceservice.application.port.in;

import com.alertacore.resourceservice.application.command.ReservarRecursoCommand;
import com.alertacore.resourceservice.application.dto.ReservaRecursoDto;

public interface ReservarRecursoPort {

    ReservaRecursoDto reservar(
            ReservarRecursoCommand command
    );
}