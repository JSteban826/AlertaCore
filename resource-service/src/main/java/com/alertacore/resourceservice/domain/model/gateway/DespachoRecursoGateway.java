package com.alertacore.resourceservice.domain.model.gateway;

import com.alertacore.resourceservice.domain.model.DespachoRecurso;

public interface DespachoRecursoGateway {

    DespachoRecurso guardar(
            DespachoRecurso despacho
    );
}