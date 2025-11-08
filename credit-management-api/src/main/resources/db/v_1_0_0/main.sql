-- =============================================
-- Script de migración v_1_0_0
-- Sistema de gestión de créditos - DDL
-- Author: Marco Villarreal
-- =============================================

-- Tabla: customers
-- Gestión de información de clientes del sistema bancario
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    identity_document VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    monthly_income DECIMAL(15, 2),
    current_debt DECIMAL(15, 2) DEFAULT 0.00,
    credit_score INTEGER,
    employment_type VARCHAR(20) CHECK (employment_type IN ('DEPENDIENTE', 'INDEPENDIENTE', 'EMPRESARIO')),
    risk_level VARCHAR(20) CHECK (risk_level IN ('BAJO', 'MEDIO', 'ALTO')),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla: credit_products
-- Catálogo de productos financieros de crédito
CREATE TABLE credit_products (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    subcategory VARCHAR(100) NOT NULL,
    minimum_amount DECIMAL(15, 2) NOT NULL,
    maximum_amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(5) NOT NULL,
    term VARCHAR(50) NOT NULL,
    minimum_rate DECIMAL(5, 2) NOT NULL,
    maximum_rate DECIMAL(5, 2) NOT NULL,
    requirements JSONB,
    features JSONB,
    benefits JSONB,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices de rendimiento
CREATE INDEX idx_customers_identity_document ON customers(identity_document);
CREATE INDEX idx_customers_active ON customers(active) WHERE active = true;
CREATE INDEX idx_customers_risk_level ON customers(risk_level) WHERE active = true;
CREATE INDEX idx_credit_products_category ON credit_products(category, subcategory);
CREATE INDEX idx_credit_products_active ON credit_products(active) WHERE active = true;
CREATE INDEX idx_credit_products_amount_range ON credit_products(minimum_amount, maximum_amount) WHERE active = true;

-- Comentarios de tabla (vendor específico - PostgreSQL)
COMMENT ON TABLE customers IS 'Gestión de información de clientes para evaluación crediticia';
COMMENT ON TABLE credit_products IS 'Catálogo de productos financieros de crédito disponibles';

-- Comentarios de columnas principales
COMMENT ON COLUMN customers.identity_document IS 'Documento de identidad único del cliente';
COMMENT ON COLUMN customers.employment_type IS 'Tipo de empleo: DEPENDIENTE, INDEPENDIENTE, EMPRESARIO';
COMMENT ON COLUMN customers.risk_level IS 'Nivel de riesgo crediticio: BAJO, MEDIO, ALTO';
COMMENT ON COLUMN credit_products.requirements IS 'Lista de requisitos en formato JSON';
COMMENT ON COLUMN credit_products.features IS 'Características del producto en formato JSON';
COMMENT ON COLUMN credit_products.benefits IS 'Beneficios del producto en formato JSON';

$EXECUTE$