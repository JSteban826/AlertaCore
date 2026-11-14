package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.application.dto.PaginaIncidentesResultado;
import com.alertacore.incidentservice.application.port.in.ListarIncidentesPort;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.Pagina;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;

import java.util.Objects;

public final class ListarIncidentesUseCase
        implements ListarIncidentesPort {

    private static final int TAMANIO_MAXIMO = 100;

    private final RepositorioIncidentePort repositorio;

    public ListarIncidentesUseCase(
            RepositorioIncidentePort repositorio
    ) {
        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio de incidentes es obligatorio"
        );
    }

    @Override
    public PaginaIncidentesResultado ejecutar(
            EstadoIncidente estado,
            TipoIncidente tipo,
            int pagina,
            int tamanio
    ) {
        validarPaginacion(pagina, tamanio);

        Pagina<Incidente> resultado =
                repositorio.buscarPaginado(
                        estado,
                        tipo,
                        pagina,
                        tamanio
                );

        return PaginaIncidentesResultado.desde(resultado);
    }

    private void validarPaginacion(
            int pagina,
            int tamanio
    ) {
        if (pagina < 0) {
            throw new IllegalArgumentException(
                    "El numero de pagina no puede ser negativo"
            );
        }

        if (tamanio < 1) {
            throw new IllegalArgumentException(
                    "El tamanio de pagina debe ser mayor que cero"
            );
        }

        if (tamanio > TAMANIO_MAXIMO) {
            throw new IllegalArgumentException(
                    "El tamanio de pagina no puede superar 100"
            );
        }
    }
}