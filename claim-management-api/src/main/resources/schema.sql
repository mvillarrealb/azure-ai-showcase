-- Script de creación de tabla para Claims en PostgreSQL

-- Crear tabla claims
CREATE TABLE IF NOT EXISTS claims (
    id VARCHAR(50) PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    identity_document VARCHAR(12) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    sub_reason VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'open',
    comments VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_claims_identity_document ON claims(identity_document);
CREATE INDEX IF NOT EXISTS idx_claims_status ON claims(status);
CREATE INDEX IF NOT EXISTS idx_claims_created_at ON claims(created_at);
CREATE INDEX IF NOT EXISTS idx_claims_identity_status ON claims(identity_document, status);

-- Agregar comentarios para documentación
COMMENT ON TABLE claims IS 'Tabla para almacenar reclamos de clientes';
COMMENT ON COLUMN claims.id IS 'ID único del reclamo con formato CLM-YYYY-NNNNNN';
COMMENT ON COLUMN claims.date IS 'Fecha del reclamo';
COMMENT ON COLUMN claims.amount IS 'Monto del reclamo con precisión de 2 decimales';
COMMENT ON COLUMN claims.identity_document IS 'Documento de identidad del cliente';
COMMENT ON COLUMN claims.description IS 'Descripción detallada del reclamo';
COMMENT ON COLUMN claims.reason IS 'Motivo principal del reclamo';
COMMENT ON COLUMN claims.sub_reason IS 'Submotivo específico del reclamo';
COMMENT ON COLUMN claims.status IS 'Estado del reclamo: open, inProgress, resolved';
COMMENT ON COLUMN claims.comments IS 'Comentarios adicionales, especialmente para resolución';
COMMENT ON COLUMN claims.created_at IS 'Fecha de creación del registro';
COMMENT ON COLUMN claims.updated_at IS 'Fecha de última actualización del registro';

-- Insertar datos de ejemplo para testing
INSERT INTO claims (id, date, amount, identity_document, description, reason, sub_reason, status, comments, created_at, updated_at) 
VALUES 
    ('CLM-2024-001234', '2024-11-08 10:30:00', 1500.75, '12345678', 'Cargo no autorizado en mi tarjeta de crédito por compra que no realicé', 'Cargo indebido', 'Transacción no autorizada', 'open', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CLM-2024-001235', '2024-11-07 14:15:00', 2500.00, '87654321', 'Error en el cálculo de intereses de mi préstamo personal', 'Error en cálculo', 'Intereses incorrectos', 'inProgress', 'Se está revisando con el área de créditos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CLM-2024-001236', '2024-11-06 09:45:00', 850.25, '11223344', 'No se aplicó el descuento prometido en mi tarjeta de crédito', 'Descuento no aplicado', 'Promoción no reflejada', 'resolved', 'Reclamo resuelto: Se aplicó el descuento correspondiente y se reembolsó la diferencia', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;