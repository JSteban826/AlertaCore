CREATE TABLE incidents.historial_estados_incidente (
                                                       id UUID PRIMARY KEY,

                                                       incidente_id UUID NOT NULL,

                                                       estado_anterior VARCHAR(40),

                                                       nuevo_estado VARCHAR(40) NOT NULL,

                                                       motivo VARCHAR(250) NOT NULL,

                                                       responsable VARCHAR(150),

                                                       fecha_cambio TIMESTAMP NOT NULL,

                                                       CONSTRAINT fk_historial_incidente
                                                           FOREIGN KEY (incidente_id)
                                                               REFERENCES incidents.incidentes (id)
                                                               ON DELETE CASCADE
);

CREATE INDEX idx_historial_incidente
    ON incidents.historial_estados_incidente (
                                              incidente_id
        );

CREATE INDEX idx_historial_fecha
    ON incidents.historial_estados_incidente (
                                              fecha_cambio
        );

CREATE INDEX idx_historial_incidente_fecha
    ON incidents.historial_estados_incidente (
                                              incidente_id,
                                              fecha_cambio
        );