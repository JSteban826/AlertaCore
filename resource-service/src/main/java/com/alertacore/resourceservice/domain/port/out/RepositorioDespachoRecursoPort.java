package com.alertacore.resourceservice.domain.port.out;

import com.alertacore.resourceservice.domain.model.DespachoRecurso;

public interface RepositorioDespachoRecursoPort {

    DespachoRecurso guardar(
            DespachoRecurso despacho
    );
}