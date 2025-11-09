-- =============================================
-- Script de migración v_1_2_0
-- Sistema de gestión de créditos - Limpieza de campos no utilizados
-- Author: Marco Villarreal
-- =============================================

-- Eliminación de columnas no utilizadas en la tabla customers
-- Se eliminan credit_score, employment_type y risk_level por no ser necesarios

-- Eliminar índice relacionado con risk_level antes de eliminar la columna
DROP INDEX IF EXISTS idx_customers_risk_level;

-- Eliminar las columnas de customers que no se van a utilizar
ALTER TABLE customers DROP COLUMN IF EXISTS credit_score;
ALTER TABLE customers DROP COLUMN IF EXISTS employment_type;
ALTER TABLE customers DROP COLUMN IF EXISTS risk_level;

-- Eliminar comentarios de columnas que ya no existen
COMMENT ON COLUMN customers.employment_type IS NULL;
COMMENT ON COLUMN customers.risk_level IS NULL;

$EXECUTE$