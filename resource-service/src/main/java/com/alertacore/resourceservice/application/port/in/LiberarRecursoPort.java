package com.alertacore.resourceservice.application.port.in;

import com.alertacore.resourceservice.application.command.LiberarRecursoCommand;
import com.alertacore.resourceservice.application.dto.LiberarRecursoDto;

public interface LiberarRecursoPort {

    LiberarRecursoDto liberar(
            LiberarRecursoCommand command
    );
}