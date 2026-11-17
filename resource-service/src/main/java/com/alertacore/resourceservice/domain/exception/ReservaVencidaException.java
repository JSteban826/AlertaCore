package com.alertacore.resourceservice.domain.exception;

public class ReservaVencidaException extends DominioException {

  public ReservaVencidaException() {
    super(
            "RESERVA_VENCIDA",
            "La reserva ha expirado"
    );
  }
}