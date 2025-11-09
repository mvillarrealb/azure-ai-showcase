-- =============================================
-- Script de migración v_1_1_0 - Historial de Empleo
-- Sistema de gestión de créditos - DDL
-- Author: Marco Villarreal
-- =============================================

-- Crear tabla employment_history
CREATE TABLE employment_history (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    position VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    income DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Agregar constraint de FK a customers
ALTER TABLE employment_history 
ADD CONSTRAINT fk_employment_history_customer 
FOREIGN KEY (customer_id) REFERENCES customers(id) 
ON DELETE CASCADE;

-- Índices para optimizar consultas
CREATE INDEX idx_employment_history_customer_id ON employment_history(customer_id);
CREATE INDEX idx_employment_history_start_date ON employment_history(start_date);
CREATE INDEX idx_employment_history_end_date ON employment_history(end_date);
CREATE INDEX idx_employment_history_customer_dates ON employment_history(customer_id, start_date DESC, end_date DESC);

-- Constraint para validar fechas
ALTER TABLE employment_history 
ADD CONSTRAINT chk_employment_dates 
CHECK (end_date IS NULL OR end_date >= start_date);

-- Constraint para validar ingresos positivos
ALTER TABLE employment_history 
ADD CONSTRAINT chk_employment_income_positive 
CHECK (income > 0);

-- Comentarios en la tabla y columnas
COMMENT ON TABLE employment_history IS 'Historial de empleos de los clientes para evaluación crediticia';
COMMENT ON COLUMN employment_history.id IS 'Identificador único del registro de empleo';
COMMENT ON COLUMN employment_history.customer_id IS 'FK al cliente propietario del historial';
COMMENT ON COLUMN employment_history.company_name IS 'Nombre de la empresa empleadora';
COMMENT ON COLUMN employment_history.position IS 'Cargo o posición en la empresa';
COMMENT ON COLUMN employment_history.start_date IS 'Fecha de inicio del empleo';
COMMENT ON COLUMN employment_history.end_date IS 'Fecha de fin del empleo, NULL si es empleo actual';
COMMENT ON COLUMN employment_history.income IS 'Ingreso mensual en el empleo';
COMMENT ON COLUMN employment_history.created_at IS 'Fecha de creación del registro';
COMMENT ON COLUMN employment_history.updated_at IS 'Fecha de última actualización del registro';

$EXECUTE$