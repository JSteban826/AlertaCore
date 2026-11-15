package com.alertacore.resourceservice.domain.exception;

public class CodigoRecursoDuplicadoException
        extends DominioException {

  private static final String CODIGO =
          "CODIGO_RECURSO_DUPLICADO";

  public CodigoRecursoDuplicadoException(String codigoRecurso) {
    super(
            CODIGO,
            "Ya existe un recurso registrado con el código %s"
                    .formatted(codigoRecurso)
    );
  }
}