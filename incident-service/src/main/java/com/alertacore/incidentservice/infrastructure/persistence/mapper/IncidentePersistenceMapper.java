package com.alertacore.incidentservice.infrastructure.persistence.mapper;

import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.PuntajeRiesgo;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.infrastructure.persistence.entity.IncidenteEntity;

public final class IncidentePersistenceMapper {

    public IncidenteEntity aEntidad(
            Incidente incidente
    ) {
        Integer puntaje = incidente.getPuntajeRiesgo() == null
                ? null
                : incidente.getPuntajeRiesgo().getValor();

        return new IncidenteEntity(
                incidente.getId(),
                incidente.getTipo(),
                incidente.getDescripcion(),
                incidente.getEstado(),
                puntaje,
                incidente.getCantidadPersonasAfectadas(),
                incidente.hayPersonasHeridas(),
                incidente.getUbicacion().getLatitud(),
                incidente.getUbicacion().getLongitud(),
                incidente.getUbicacion().getDireccion(),
                incidente.getInformacionContacto()
                        .getNombreReportante(),
                incidente.getInformacionContacto()
                        .getCorreo(),
                incidente.getInformacionContacto()
                        .getTelefono(),
                incidente.getFechaReporte(),
                incidente.getFechaUltimaActualizacion()
        );
    }

    public Incidente aDominio(
            IncidenteEntity entidad
    ) {
        Ubicacion ubicacion = new Ubicacion(
                entidad.getLatitud(),
                entidad.getLongitud(),
                entidad.getDireccion()
        );

        InformacionContacto contacto =
                new InformacionContacto(
                        entidad.getNombreReportante(),
                        entidad.getCorreoReportante(),
                        entidad.getTelefonoReportante()
                );

        PuntajeRiesgo puntaje =
                entidad.getPuntajeRiesgo() == null
                        ? null
                        : new PuntajeRiesgo(
                        entidad.getPuntajeRiesgo()
                );

        return Incidente.reconstruir(
                entidad.getId(),
                entidad.getTipo(),
                entidad.getDescripcion(),
                ubicacion,
                contacto,
                entidad.getCantidadPersonasAfectadas(),
                entidad.isHayPersonasHeridas(),
                entidad.getEstado(),
                puntaje,
                entidad.getFechaReporte(),
                entidad.getFechaUltimaActualizacion()
        );
    }
}