CREATE TABLE IF NOT EXISTS resources.despachos_recursos (

    id UUID PRIMARY KEY,

    recurso_id UUID NOT NULL,

    incidente_id UUID NOT NULL,

    responsable VARCHAR(100) NOT NULL,

    fecha_despacho TIMESTAMP NOT NULL,

    fecha_llegada TIMESTAMP,

    fecha_finalizacion TIMESTAMP
    );

CREATE INDEX idx_despacho_incidente
    ON resources.despachos_recursos(
                                    incidente_id
        );