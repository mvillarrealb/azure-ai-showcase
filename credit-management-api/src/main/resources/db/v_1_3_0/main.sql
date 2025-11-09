-- =============================================
-- Script de migración v_1_3_0
-- Gestión de Rankings/Clasificaciones - DDL
-- Author: Marco Villarreal
-- =============================================

-- Tabla: ranks
-- Gestión de rangos y clasificaciones de clientes para el sistema
CREATE TABLE ranks (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices de rendimiento
CREATE INDEX idx_ranks_active ON ranks(active) WHERE active = true;
CREATE INDEX idx_ranks_name ON ranks(name) WHERE active = true;
CREATE UNIQUE INDEX idx_ranks_name_unique ON ranks(name) WHERE active = true;

-- Comentarios de tabla
COMMENT ON TABLE ranks IS 'Gestión de rangos y clasificaciones de clientes para evaluación crediticia y análisis semántico';

-- Comentarios de columnas principales
COMMENT ON COLUMN ranks.id IS 'Identificador único del rango (ej: ORO, PLATA, BRONCE)';
COMMENT ON COLUMN ranks.name IS 'Nombre descriptivo del rango';
COMMENT ON COLUMN ranks.description IS 'Descripción detallada del perfil del cliente para este rango';
COMMENT ON COLUMN ranks.active IS 'Indica si el rango está activo y disponible para uso';

$EXECUTE$