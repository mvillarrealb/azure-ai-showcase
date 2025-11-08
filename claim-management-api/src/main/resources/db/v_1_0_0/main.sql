CREATE TABLE IF NOT EXISTS claims (
    id VARCHAR(50) NOT NULL,
    date TIMESTAMP WITH TIME ZONE NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    identity_document VARCHAR(12) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    sub_reason VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'open',
    comments VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_claims PRIMARY KEY (id),
    CONSTRAINT chk_claims_status CHECK (status IN ('open', 'inProgress', 'resolved')),
    CONSTRAINT chk_claims_amount CHECK (amount > 0),
    CONSTRAINT chk_claims_identity_document CHECK (LENGTH(identity_document) >= 8)
);

COMMENT ON TABLE claims IS 'Tabla para almacenar reclamos de clientes bancarios con toda su información y seguimiento';
COMMENT ON COLUMN claims.id IS 'ID único del reclamo con formato CLM-YYYY-NNNNNN generado automáticamente';
COMMENT ON COLUMN claims.date IS 'Fecha y hora del reclamo reportado por el cliente';
COMMENT ON COLUMN claims.amount IS 'Monto del reclamo con precisión de 2 decimales para valores monetarios';
COMMENT ON COLUMN claims.identity_document IS 'Documento de identidad del cliente que presenta el reclamo';
COMMENT ON COLUMN claims.description IS 'Descripción detallada del reclamo proporcionada por el cliente';
COMMENT ON COLUMN claims.reason IS 'Motivo principal categorizado del reclamo';
COMMENT ON COLUMN claims.sub_reason IS 'Submotivo específico que detalla el tipo de reclamo';
COMMENT ON COLUMN claims.status IS 'Estado actual del reclamo: open, inProgress, resolved';
COMMENT ON COLUMN claims.comments IS 'Comentarios adicionales del proceso, especialmente para resolución';
COMMENT ON COLUMN claims.created_at IS 'Fecha de creación del registro con zona horaria';
COMMENT ON COLUMN claims.updated_at IS 'Fecha de última actualización del registro con zona horaria';

CREATE INDEX IF NOT EXISTS idx_claims_identity_document ON claims(identity_document);
CREATE INDEX IF NOT EXISTS idx_claims_status ON claims(status);
CREATE INDEX IF NOT EXISTS idx_claims_created_at ON claims(created_at);
CREATE INDEX IF NOT EXISTS idx_claims_identity_status ON claims(identity_document, status);
CREATE INDEX IF NOT EXISTS idx_claims_date_range ON claims(date);

$EXECUTE$