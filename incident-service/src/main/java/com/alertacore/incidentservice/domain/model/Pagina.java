package com.alertacore.incidentservice.domain.model;

import java.util.List;
import java.util.Objects;

public record Pagina<T>(
        List<T> contenido,
        int numeroPagina,
        int tamanioPagina,
        long totalElementos,
        int totalPaginas
) {

    public Pagina {
        Objects.requireNonNull(
                contenido,
                "El contenido de la pagina es obligatorio"
        );

        if (numeroPagina < 0) {
            throw new IllegalArgumentException(
                    "El numero de pagina no puede ser negativo"
            );
        }

        if (tamanioPagina < 1) {
            throw new IllegalArgumentException(
                    "El tamanio de pagina debe ser mayor que cero"
            );
        }

        if (totalElementos < 0) {
            throw new IllegalArgumentException(
                    "El total de elementos no puede ser negativo"
            );
        }

        if (totalPaginas < 0) {
            throw new IllegalArgumentException(
                    "El total de paginas no puede ser negativo"
            );
        }

        contenido = List.copyOf(contenido);
    }

    public boolean esPrimeraPagina() {
        return numeroPagina == 0;
    }

    public boolean esUltimaPagina() {
        return totalPaginas == 0
                || numeroPagina >= totalPaginas - 1;
    }

    public boolean estaVacia() {
        return contenido.isEmpty();
    }
}