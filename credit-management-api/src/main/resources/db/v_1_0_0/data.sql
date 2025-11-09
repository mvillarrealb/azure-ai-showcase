-- =============================================
-- Script de migración v_1_0_0 - Datos iniciales
-- Sistema de gestión de créditos - DML
-- Author: Marco Villarreal
-- =============================================

-- Datos de prueba para customers
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, credit_score, employment_type, risk_level, active)
VALUES 
    ('12345678', 'Juan Carlos', 'Pérez López', 'juan.perez@email.com', '+51987654321', 4500.00, 1200.00, 720, 'DEPENDIENTE', 'BAJO', true),
    ('87654321', 'María Elena', 'García Torres', 'maria.garcia@email.com', '+51976543210', 6800.00, 2300.00, 650, 'INDEPENDIENTE', 'MEDIO', true),
    ('11223344', 'Carlos Eduardo', 'Mendoza Silva', 'carlos.mendoza@email.com', '+51965432109', 12000.00, 5600.00, 580, 'EMPRESARIO', 'ALTO', true),
    ('10456789001', 'Ana Sofía', 'Rodríguez Vega', 'ana.rodriguez@email.com', '+51954321098', 3200.00, 800.00, 740, 'DEPENDIENTE', 'BAJO', true),
    ('10456789002', 'Roberto Miguel', 'Flores Castro', 'roberto.flores@email.com', '+51943210987', 8500.00, 3400.00, 680, 'INDEPENDIENTE', 'MEDIO', true);

-- =============================================
-- PRODUCTOS CREDITICIOS
-- =============================================
-- IMPORTANTE: Los productos crediticios deben crearse via API REST para garantizar
-- sincronización automática con Azure AI Search. 
-- 
-- Para crear productos usar el script: create-products.sh
-- Esto asegura que el ProductSyncListener sea activado y los productos 
-- se indexen correctamente en AI Search para búsquedas semánticas.
--
-- Los productos se crean usando POST /products endpoint
-- =============================================