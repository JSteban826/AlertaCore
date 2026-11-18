package com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.adapter;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.entity.HistorialEstadoIncidenteEntity;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.entity.IncidenteEntity;
import com.alertacore.incidentservice.infrastructure.mapper.HistorialEstadoPersistenceMapper;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.repository.JpaHistorialEstadoRepository;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.repository.JpaIncidenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class HistorialEstadoPersistenceAdapterTest {

    private JpaHistorialEstadoRepository
            repositorioHistorialJpa;

    private JpaIncidenteRepository
            repositorioIncidenteJpa;

    private HistorialEstadoPersistenceMapper mapper;

    private HistorialEstadoPersistenceAdapter adapter;

    @BeforeEach
    void configurar() {
        repositorioHistorialJpa = mock(
                JpaHistorialEstadoRepository.class
        );

        repositorioIncidenteJpa = mock(
                JpaIncidenteRepository.class
        );

        mapper = mock(
                HistorialEstadoPersistenceMapper.class
        );

        adapter = new HistorialEstadoPersistenceAdapter(
                repositorioHistorialJpa,
                repositorioIncidenteJpa,
                mapper
        );
    }

    @Test
    void debeGuardarRegistroDeHistorial() {
        UUID incidenteId = UUID.randomUUID();

        RegistroHistorialEstado registro =
                crearRegistro(incidenteId);

        IncidenteEntity incidenteEntity =
                mock(IncidenteEntity.class);

        HistorialEstadoIncidenteEntity entidad =
                mock(
                        HistorialEstadoIncidenteEntity.class
                );

        HistorialEstadoIncidenteEntity entidadGuardada =
                mock(
                        HistorialEstadoIncidenteEntity.class
                );

        RegistroHistorialEstado resultadoEsperado =
                crearRegistro(incidenteId);

        when(
                repositorioIncidenteJpa.getReferenceById(
                        incidenteId
                )
        ).thenReturn(incidenteEntity);

        when(
                mapper.aEntidad(
                        registro,
                        incidenteEntity
                )
        ).thenReturn(entidad);

        when(
                repositorioHistorialJpa.save(entidad)
        ).thenReturn(entidadGuardada);

        when(
                mapper.aDominio(entidadGuardada)
        ).thenReturn(resultadoEsperado);

        RegistroHistorialEstado resultado =
                adapter.guardar(registro);

        assertSame(
                resultadoEsperado,
                resultado
        );

        verify(repositorioIncidenteJpa)
                .getReferenceById(incidenteId);

        verify(mapper)
                .aEntidad(
                        registro,
                        incidenteEntity
                );

        verify(repositorioHistorialJpa)
                .save(entidad);

        verify(mapper)
                .aDominio(entidadGuardada);
    }

    @Test
    void debeConsultarHistorialOrdenadoPorFecha() {
        UUID incidenteId = UUID.randomUUID();

        HistorialEstadoIncidenteEntity primeraEntidad =
                mock(
                        HistorialEstadoIncidenteEntity.class
                );

        HistorialEstadoIncidenteEntity segundaEntidad =
                mock(
                        HistorialEstadoIncidenteEntity.class
                );

        RegistroHistorialEstado primerRegistro =
                crearRegistro(
                        incidenteId,
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        "Informacion verificada",
                        LocalDateTime.of(
                                2026,
                                11,
                                16,
                                10,
                                0
                        )
                );

        RegistroHistorialEstado segundoRegistro =
                crearRegistro(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        EstadoIncidente.CLASIFICADO,
                        "Clasificacion automatica",
                        LocalDateTime.of(
                                2026,
                                11,
                                16,
                                10,
                                5
                        )
                );

        when(
                repositorioHistorialJpa
                        .findByIncidenteIdOrderByFechaCambioAsc(
                                incidenteId
                        )
        ).thenReturn(
                List.of(
                        primeraEntidad,
                        segundaEntidad
                )
        );

        when(mapper.aDominio(primeraEntidad))
                .thenReturn(primerRegistro);

        when(mapper.aDominio(segundaEntidad))
                .thenReturn(segundoRegistro);

        List<RegistroHistorialEstado> resultado =
                adapter.buscarPorIncidenteIdOrdenado(
                        incidenteId
                );

        assertEquals(
                2,
                resultado.size()
        );

        assertSame(
                primerRegistro,
                resultado.get(0)
        );

        assertSame(
                segundoRegistro,
                resultado.get(1)
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                resultado.get(0).nuevoEstado()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                resultado.get(1).nuevoEstado()
        );

        verify(repositorioHistorialJpa)
                .findByIncidenteIdOrderByFechaCambioAsc(
                        incidenteId
                );

        verify(mapper)
                .aDominio(primeraEntidad);

        verify(mapper)
                .aDominio(segundaEntidad);
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayHistorial() {
        UUID incidenteId = UUID.randomUUID();

        when(
                repositorioHistorialJpa
                        .findByIncidenteIdOrderByFechaCambioAsc(
                                incidenteId
                        )
        ).thenReturn(List.of());

        List<RegistroHistorialEstado> resultado =
                adapter.buscarPorIncidenteIdOrdenado(
                        incidenteId
                );

        assertEquals(
                0,
                resultado.size()
        );

        verify(repositorioHistorialJpa)
                .findByIncidenteIdOrderByFechaCambioAsc(
                        incidenteId
                );

        verifyNoInteractions(mapper);
    }

    @Test
    void debeRechazarRegistroNulo() {
        assertThrows(
                NullPointerException.class,
                () -> adapter.guardar(null)
        );

        verifyNoInteractions(
                repositorioHistorialJpa,
                repositorioIncidenteJpa,
                mapper
        );
    }

    @Test
    void debeRechazarIdentificadorNuloAlConsultar() {
        assertThrows(
                NullPointerException.class,
                () -> adapter
                        .buscarPorIncidenteIdOrdenado(
                                null
                        )
        );

        verifyNoInteractions(
                repositorioHistorialJpa,
                repositorioIncidenteJpa,
                mapper
        );
    }

    @Test
    void debeRechazarRepositorioHistorialNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new HistorialEstadoPersistenceAdapter(
                        null,
                        repositorioIncidenteJpa,
                        mapper
                )
        );
    }

    @Test
    void debeRechazarRepositorioIncidenteNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new HistorialEstadoPersistenceAdapter(
                        repositorioHistorialJpa,
                        null,
                        mapper
                )
        );
    }

    @Test
    void debeRechazarMapperNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new HistorialEstadoPersistenceAdapter(
                        repositorioHistorialJpa,
                        repositorioIncidenteJpa,
                        null
                )
        );
    }

    private RegistroHistorialEstado crearRegistro(
            UUID incidenteId
    ) {
        return crearRegistro(
                incidenteId,
                EstadoIncidente.REPORTADO,
                EstadoIncidente.VALIDADO,
                "Informacion verificada",
                LocalDateTime.now()
        );
    }

    private RegistroHistorialEstado crearRegistro(
            UUID incidenteId,
            EstadoIncidente estadoAnterior,
            EstadoIncidente nuevoEstado,
            String motivo,
            LocalDateTime fechaCambio
    ) {
        return new RegistroHistorialEstado(
                UUID.randomUUID(),
                incidenteId,
                estadoAnterior,
                nuevoEstado,
                motivo,
                "OPERADOR_API",
                fechaCambio
        );
    }
}