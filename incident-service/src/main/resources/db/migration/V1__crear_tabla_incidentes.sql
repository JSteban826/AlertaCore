CREATE SCHEMA IF NOT EXISTS incidents;

CREATE TABLE incidents.incidentes (
                                      id UUID PRIMARY KEY,

                                      tipo VARCHAR(40) NOT NULL,

                                      descripcion VARCHAR(500) NOT NULL,

                                      estado VARCHAR(40) NOT NULL,

                                      puntaje_riesgo INTEGER,

                                      cantidad_personas_afectadas INTEGER NOT NULL,

                                      hay_personas_heridas BOOLEAN NOT NULL,

                                      latitud DOUBLE PRECISION NOT NULL,

                                      longitud DOUBLE PRECISION NOT NULL,

                                      direccion VARCHAR(250) NOT NULL,

                                      nombre_reportante VARCHAR(150) NOT NULL,

                                      correo_reportante VARCHAR(200) NOT NULL,

                                      telefono_reportante VARCHAR(30) NOT NULL,

                                      fecha_reporte TIMESTAMP NOT NULL,

                                      fecha_ultima_actualizacion TIMESTAMP NOT NULL,

                                      version BIGINT NOT NULL DEFAULT 0,

                                      CONSTRAINT chk_incidente_puntaje
                                          CHECK (
                                              puntaje_riesgo IS NULL
                                                  OR puntaje_riesgo BETWEEN 0 AND 100
                                              ),

                                      CONSTRAINT chk_incidente_personas_afectadas
                                          CHECK (
                                              cantidad_personas_afectadas >= 0
                                              ),

                                      CONSTRAINT chk_incidente_latitud
                                          CHECK (
                                              latitud BETWEEN -90 AND 90
                                              ),

                                      CONSTRAINT chk_incidente_longitud
                                          CHECK (
                                              longitud BETWEEN -180 AND 180
                                              ),

                                      CONSTRAINT chk_incidente_descripcion
                                          CHECK (
                                              LENGTH(TRIM(descripcion))
                                                  BETWEEN 10 AND 500
                                              )
);

CREATE INDEX idx_incidente_estado
    ON incidents.incidentes (estado);

CREATE INDEX idx_incidente_tipo
    ON incidents.incidentes (tipo);

CREATE INDEX idx_incidente_fecha_reporte
    ON incidents.incidentes (fecha_reporte);

CREATE INDEX idx_incidente_estado_fecha
    ON incidents.incidentes (
                             estado,
                             fecha_reporte
        );