package com.alertacore.resourceservice.domain.exception;

public class RecursoYaReservadoException extends DominioException {

  public RecursoYaReservadoException() {
    super(
            "RECURSO_YA_RESERVADO",
            "Ya existe una reserva activa"
    );
  }
}