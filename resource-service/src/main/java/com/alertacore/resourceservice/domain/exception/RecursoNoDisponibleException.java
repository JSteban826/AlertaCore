package com.alertacore.resourceservice.domain.exception;

public class RecursoNoDisponibleException extends DominioException {

  public RecursoNoDisponibleException(String codigo) {
    super(
            "RECURSO_NO_DISPONIBLE",
            "El recurso %s no está disponible"
                    .formatted(codigo)
    );
  }
}