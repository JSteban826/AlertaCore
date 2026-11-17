package com.alertacore.resourceservice.domain.exception;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;

public class RecursoDisponibleNoEncontradoException
        extends DominioException {

  private static final String CODIGO =
          "RECURSO_DISPONIBLE_NO_ENCONTRADO";

  public RecursoDisponibleNoEncontradoException(
          TipoRecurso tipo,
          double distanciaMaximaKm
  ) {
    super(
            CODIGO,
            "No se encontró un recurso disponible de tipo %s dentro de %.2f km"
                    .formatted(tipo, distanciaMaximaKm)
    );
  }
}