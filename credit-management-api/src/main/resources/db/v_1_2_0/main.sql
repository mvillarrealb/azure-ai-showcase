-- =============================================
-- Script de migración v_1_2_0 - CORREGIDO
-- Sistema de gestión de créditos - Limpieza de campos no utilizados
-- Author: Marco Villarreal
-- =============================================

-- Verificación y eliminación segura de columnas en customers
-- Solo elimina si las columnas existen para evitar errores

-- Eliminar índice relacionado con risk_level antes de eliminar la columna (si existe)
DROP INDEX IF EXISTS idx_customers_risk_level;

-- Verificar y eliminar columnas solo si existen
DO $$ 
BEGIN
    -- Verificar y eliminar credit_score si existe
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name='customers' AND column_name='credit_score') THEN
        ALTER TABLE customers DROP COLUMN credit_score;
    END IF;
    
    -- Verificar y eliminar employment_type si existe
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name='customers' AND column_name='employment_type') THEN
        ALTER TABLE customers DROP COLUMN employment_type;
    END IF;
    
    -- Verificar y eliminar risk_level si existe
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name='customers' AND column_name='risk_level') THEN
        ALTER TABLE customers DROP COLUMN risk_level;
    END IF;
END $$;

$EXECUTE$