-- =============================================
-- Script de migración v_1_1_0 - Datos de historial de empleo
-- Sistema de gestión de créditos - DML
-- Author: Marco Villarreal
-- =============================================

-- Primero, insertar clientes adicionales para las pruebas de historial de empleo
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, credit_score, employment_type, risk_level, active)
VALUES 
    -- Clientes con empleo estable
    ('10456789101', 'Luis Fernando', 'Gómez Ruiz', 'luis.gomez@email.com', '+51987654001', 5500.00, 1000.00, 750, 'DEPENDIENTE', 'BAJO', true),
    ('10456789102', 'Sandra Patricia', 'López Martín', 'sandra.lopez@email.com', '+51987654002', 7200.00, 1500.00, 720, 'INDEPENDIENTE', 'BAJO', true),
    
    -- Clientes con desempleo de 1 mes
    ('10456789103', 'Miguel Angel', 'Herrera Cruz', 'miguel.herrera@email.com', '+51987654003', 4800.00, 800.00, 680, 'DEPENDIENTE', 'MEDIO', true),
    ('10456789104', 'Diana Carolina', 'Morales Vega', 'diana.morales@email.com', '+51987654004', 6100.00, 1200.00, 700, 'INDEPENDIENTE', 'MEDIO', true),
    
    -- Clientes con desempleo de 3 meses
    ('10456789105', 'Carlos Alberto', 'Jiménez Soto', 'carlos.jimenez@email.com', '+51987654005', 5000.00, 900.00, 650, 'DEPENDIENTE', 'MEDIO', true),
    ('10456789106', 'Isabel María', 'Ramírez Torres', 'isabel.ramirez@email.com', '+51987654006', 5800.00, 1100.00, 680, 'INDEPENDIENTE', 'MEDIO', true),
    
    -- Clientes con desempleo de 6 meses
    ('10456789107', 'José Ricardo', 'Fernández Luna', 'jose.fernandez@email.com', '+51987654007', 4200.00, 600.00, 620, 'DEPENDIENTE', 'ALTO', true),
    ('10456789108', 'Martha Elena', 'Vargas Peña', 'martha.vargas@email.com', '+51987654008', 4900.00, 750.00, 640, 'INDEPENDIENTE', 'ALTO', true),
    
    -- Clientes sin empleo (0 registros)
    ('10456789109', 'Pedro Antonio', 'Castillo Reyes', 'pedro.castillo@email.com', '+51987654009', 3500.00, 400.00, 580, 'DEPENDIENTE', 'ALTO', true),
    ('10456789110', 'Lucia Esperanza', 'Mendez Silva', 'lucia.mendez@email.com', '+51987654010', 3200.00, 350.00, 560, 'INDEPENDIENTE', 'ALTO', true),
    
    -- Clientes con 1 empleo estable
    ('10456789111', 'Roberto Carlos', 'Delgado Montes', 'roberto.delgado@email.com', '+51987654011', 6500.00, 1300.00, 730, 'DEPENDIENTE', 'BAJO', true),
    ('10456789112', 'Ana Gabriela', 'Rojas Campos', 'ana.rojas@email.com', '+51987654012', 7800.00, 1600.00, 760, 'INDEPENDIENTE', 'BAJO', true),
    
    -- Cliente con 8 meses de desempleo previos
    ('10456789113', 'Fernando José', 'Aguilar Moreno', 'fernando.aguilar@email.com', '+51987654013', 5200.00, 950.00, 690, 'DEPENDIENTE', 'MEDIO', true);

-- =============================================
-- HISTORIAL DE EMPLEO - CASOS DE PRUEBA
-- =============================================

-- 1. CLIENTES SUMAMENTE ESTABLES (2 empleos largos, sin desempleo)
-- Cliente 10456789101: Estabilidad total, cambio directo entre empleos
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789101'), 'Banco Continental', 'Analista de Créditos Jr.', '2020-01-15', '2022-12-31', 3800.00),
    ((SELECT id FROM customers WHERE identity_document = '10456789101'), 'BBVA Perú', 'Analista de Créditos Sr.', '2023-01-01', NULL, 5500.00);

-- Cliente 10456789102: Promoción interna sin interrupción
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789102'), 'Interbank', 'Especialista en Finanzas', '2019-06-01', '2022-05-31', 5200.00),
    ((SELECT id FROM customers WHERE identity_document = '10456789102'), 'Interbank', 'Gerente de Finanzas', '2022-06-01', NULL, 7200.00);

-- 2. CLIENTES CON DESEMPLEO DE 1 MES
-- Cliente 10456789103: Gap de exactamente 1 mes
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789103'), 'Scotiabank', 'Asesor Comercial', '2021-03-01', '2023-02-28', 4200.00),
    ((SELECT id FROM customers WHERE identity_document = '10456789103'), 'BCP', 'Ejecutivo de Ventas', '2023-04-01', NULL, 4800.00);

-- Cliente 10456789104: Gap de 1 mes entre empleos
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789104'), 'Rimac Seguros', 'Consultora Independiente', '2020-08-15', '2022-11-30', 5500.00),
    ((SELECT id FROM customers WHERE identity_document = '10456789104'), 'La Positiva Seguros', 'Gerente de Cuenta', '2023-01-01', NULL, 6100.00);

-- 3. CLIENTES CON DESEMPLEO DE 3 MESES
-- Cliente 10456789105: Gap de 3 meses
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789105'), 'Falabella Financiero', 'Analista de Riesgo', '2020-05-01', '2022-08-31', 4500.00),
    ((SELECT id FROM customers WHERE identity_document = '10456789105'), 'Caja Metropolitana', 'Especialista en Microcréditos', '2022-12-01', NULL, 5000.00);

-- Cliente 10456789106: Gap de 3 meses exactos
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789106'), 'Mibanco', 'Evaluadora de Créditos', '2019-10-01', '2022-06-30', 5200.00),
    ((SELECT id FROM customers WHERE identity_document = '10456789106'), 'Financiera CrediScotia', 'Supervisora de Evaluaciones', '2022-10-01', NULL, 5800.00);

-- 4. CLIENTES CON DESEMPLEO DE 6 MESES
-- Cliente 10456789107: Gap de 6 meses
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789107'), 'Compartamos Financiera', 'Asesor de Microcréditos', '2020-02-01', '2022-03-31', 3800.00),
    ((SELECT id FROM customers WHERE identity_document = '10456789107'), 'Credinka', 'Analista de Cobranzas', '2022-10-01', NULL, 4200.00);

-- Cliente 10456789108: Gap de 6 meses exactos
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789108'), 'Caja Piura', 'Funcionaria de Negocios', '2019-12-01', '2021-12-31', 4400.00),
    ((SELECT id FROM customers WHERE identity_document = '10456789108'), 'Financiera Confianza', 'Gerente de Agencia', '2022-07-01', NULL, 4900.00);

-- 5. CLIENTES SIN EMPLEO (10456789109 y 10456789110) - NO SE INSERTAN REGISTROS

-- 6. CLIENTES CON 1 EMPLEO SOLAMENTE, ESTABLE
-- Cliente 10456789111: Solo empleo actual, muy estable (3+ años)
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789111'), 'BCP', 'Gerente de Banca Empresarial', '2021-01-15', NULL, 6500.00);

-- Cliente 10456789112: Solo empleo actual, estable (2+ años)
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789112'), 'BBVA', 'Directora de Inversiones', '2022-03-01', NULL, 7800.00);

-- 7. CLIENTE CON 8 MESES DE DESEMPLEO PREVIOS
-- Cliente 10456789113: Gap de 8 meses antes del empleo actual
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789113'), 'Saga Falabella', 'Supervisor de Créditos', '2021-01-01', '2022-01-31', 4800.00),
    ((SELECT id FROM customers WHERE identity_document = '10456789113'), 'Ripley', 'Analista Senior de Riesgo', '2022-10-01', NULL, 5200.00);

-- =============================================
-- CASOS ADICIONALES PARA PRUEBAS MÁS COMPLEJAS
-- =============================================

-- Cliente con múltiples empleos y gaps variados (para testing avanzado)
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, credit_score, employment_type, risk_level, active)
VALUES 
    ('10456789114', 'Carmen Rosa', 'Bustamante Ochoa', 'carmen.bustamante@email.com', '+51987654014', 4600.00, 850.00, 660, 'INDEPENDIENTE', 'MEDIO', true);

-- Historial complejo: múltiples gaps
INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
    ((SELECT id FROM customers WHERE identity_document = '10456789114'), 'Tiendas EFE', 'Asesora de Ventas', '2020-01-15', '2020-06-30', 3200.00),
    -- Gap de 2 meses
    ((SELECT id FROM customers WHERE identity_document = '10456789114'), 'Ripley', 'Ejecutiva de Cuenta', '2020-09-01', '2021-03-31', 3800.00),
    -- Gap de 4 meses
    ((SELECT id FROM customers WHERE identity_document = '10456789114'), 'Saga Falabella', 'Coordinadora de Créditos', '2021-08-01', '2022-12-31', 4200.00),
    -- Gap de 1 mes
    ((SELECT id FROM customers WHERE identity_document = '10456789114'), 'Oechsle', 'Jefa de Cobranzas', '2023-02-01', NULL, 4600.00);