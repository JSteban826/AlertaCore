package com.alertacore.incidentservice.domain.model;

import java.util.List;

public record PaginaIncidentesResultado(
        List<ResultadoIncidente> contenido,
        int numeroPagina,
        int tamanioPagina,
        long totalElementos,
        int totalPaginas,
        boolean primeraPagina,
        boolean ultimaPagina,
        boolean vacia
) {

    public static PaginaIncidentesResultado desde(
            Pagina<Incidente> pagina
    ) {
        List<ResultadoIncidente> resultados =
                pagina.contenido()
                        .stream()
                        .map(ResultadoIncidente::desde)
                        .toList();

        return new PaginaIncidentesResultado(
                resultados,
                pagina.numeroPagina(),
                pagina.tamanioPagina(),
                pagina.totalElementos(),
                pagina.totalPaginas(),
                pagina.esPrimeraPagina(),
                pagina.esUltimaPagina(),
                pagina.estaVacia()
        );
    }
}