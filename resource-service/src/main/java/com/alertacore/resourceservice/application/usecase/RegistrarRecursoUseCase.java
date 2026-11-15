package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.command.RegistrarRecursoCommand;
import com.alertacore.resourceservice.application.dto.RecursoDto;
import com.alertacore.resourceservice.application.port.in.RegistrarRecursoPort;
import com.alertacore.resourceservice.domain.exception.CodigoRecursoDuplicadoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;

import java.util.Locale;
import java.util.Objects;

public class RegistrarRecursoUseCase
        implements RegistrarRecursoPort {

    private final RepositorioRecursoPort repositorioRecursoPort;

    public RegistrarRecursoUseCase(
            RepositorioRecursoPort repositorioRecursoPort
    ) {
        this.repositorioRecursoPort = Objects.requireNonNull(
                repositorioRecursoPort,
                "El repositorio de recursos es obligatorio"
        );
    }

    @Override
    public RecursoDto registrar(
            RegistrarRecursoCommand command
    ) {
        Objects.requireNonNull(
                command,
                "El comando de registro es obligatorio"
        );

        String codigoNormalizado = normalizarCodigo(
                command.codigo()
        );

        validarCodigoDisponible(codigoNormalizado);

        UbicacionRecurso ubicacion =
                UbicacionRecurso.crear(
                        command.latitud(),
                        command.longitud()
                );

        Recurso recurso = Recurso.crear(
                codigoNormalizado,
                command.nombre(),
                command.tipo(),
                ubicacion
        );

        Recurso recursoGuardado =
                repositorioRecursoPort.guardar(recurso);

        return convertirADto(recursoGuardado);
    }

    private void validarCodigoDisponible(String codigo) {
        if (repositorioRecursoPort.existePorCodigo(codigo)) {
            throw new CodigoRecursoDuplicadoException(codigo);
        }
    }

    private String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase(Locale.ROOT);
    }

    private RecursoDto convertirADto(Recurso recurso) {
        return new RecursoDto(
                recurso.getId(),
                recurso.getCodigo(),
                recurso.getNombre(),
                recurso.getTipo(),
                recurso.getEstado(),
                recurso.getUbicacion().getLatitud(),
                recurso.getUbicacion().getLongitud(),
                recurso.getFechaDisponibleDesde(),
                recurso.getFechaUltimaActualizacion()
        );
    }
}