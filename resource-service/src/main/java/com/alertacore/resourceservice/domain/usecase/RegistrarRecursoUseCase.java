package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.command.RegistrarRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.RecursoDto;

import com.alertacore.resourceservice.domain.exception.CodigoRecursoDuplicadoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;

import java.util.Locale;
import java.util.Objects;

public class RegistrarRecursoUseCase {

    private final RecursoGateway recursoGateway;

    public RegistrarRecursoUseCase(
            RecursoGateway recursoGateway
    ) {

        this.recursoGateway =
                Objects.requireNonNull(
                        recursoGateway,
                        "El gateway de recursos es obligatorio"
                );
    }

    public RecursoDto registrar(
            RegistrarRecursoCommand command
    ) {

        Objects.requireNonNull(
                command,
                "El comando de registro es obligatorio"
        );

        String codigoNormalizado =
                normalizarCodigo(
                        command.codigo()
                );

        validarCodigoDisponible(
                codigoNormalizado
        );

        UbicacionRecurso ubicacion =
                UbicacionRecurso.crear(
                        command.latitud(),
                        command.longitud()
                );

        Recurso recurso =
                Recurso.crear(
                        codigoNormalizado,
                        command.nombre(),
                        command.tipo(),
                        ubicacion
                );

        Recurso recursoGuardado =
                recursoGateway.guardar(
                        recurso
                );

        return convertirADto(
                recursoGuardado
        );
    }

    private void validarCodigoDisponible(
            String codigo
    ) {

        if (
                recursoGateway.existePorCodigo(
                        codigo
                )
        ) {

            throw new CodigoRecursoDuplicadoException(
                    codigo
            );
        }
    }

    private String normalizarCodigo(
            String codigo
    ) {

        return codigo
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private RecursoDto convertirADto(
            Recurso recurso
    ) {

        return new RecursoDto(
                recurso.getId(),
                recurso.getCodigo(),
                recurso.getNombre(),
                recurso.getTipo(),
                recurso.getEstado(),
                recurso.getUbicacion()
                        .getLatitud(),
                recurso.getUbicacion()
                        .getLongitud(),
                recurso.getFechaDisponibleDesde(),
                recurso.getFechaUltimaActualizacion()
        );
    }
}