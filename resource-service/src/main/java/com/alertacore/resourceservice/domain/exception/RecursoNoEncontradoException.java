package com.alertacore.resourceservice.domain.exception;

public class RecursoNoEncontradoException
        extends DominioException {

  public RecursoNoEncontradoException(
          String identificador
  ) {

    super(
            "RECURSO_NO_ENCONTRADO",
            "No existe un recurso con identificador "
                    + identificador
    );
  }
}