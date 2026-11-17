package com.alertacore.resourceservice.application.port.in;

import com.alertacore.resourceservice.application.command.DespacharRecursoCommand;
import com.alertacore.resourceservice.application.dto.DespachoRecursoDto;

public interface DespacharRecursoPort {

    DespachoRecursoDto despachar(
            DespacharRecursoCommand command
    );
}