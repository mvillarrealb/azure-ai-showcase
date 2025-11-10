-- =============================================
-- Script de migración v_1_4_0 - Datos para AI Search
-- Clientes tipo con historiales laborales completos para cada rango
-- Author: Marco Villarreal
-- =============================================

-- =============================================
-- CLIENTES RANGO BRONCE
-- Ingresos S/2,500 - S/4,500, Endeudamiento <40%, Historial 6+ meses
-- =============================================

-- BRONCE #1: Joven profesional iniciando carrera
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025001001', 'Diego Alejandro', 'Santos Villanueva', 'diego.santos@email.com', '+51987650100', 3200.00, 1100.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025001001'), 'Tiendas EFE', 'Vendedor Junior', '2024-04-01', NULL, 3200.00);

-- BRONCE #2: Con gap de empleo reciente pero estabilizándose
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025001002', 'Karla Stefany', 'Moreno Delgado', 'karla.moreno@email.com', '+51987650101', 3800.00, 1400.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025001002'), 'Ripley', 'Cajera', '2023-06-01', '2024-01-31', 3200.00),
-- Gap de 2 meses
((SELECT id FROM customers WHERE identity_document = '10025001002'), 'Saga Falabella', 'Asesora de Ventas', '2024-04-01', NULL, 3800.00);

-- BRONCE #3: Empleado con historial más largo pero ingresos limitados
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025001003', 'Javier Enrique', 'Quispe Mamani', 'javier.quispe@email.com', '+51987650102', 4200.00, 1500.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025001003'), 'Caja Municipal Arequipa', 'Asesor de Créditos', '2023-01-15', '2024-03-31', 3800.00),
-- Gap de 1 mes
((SELECT id FROM customers WHERE identity_document = '10025001003'), 'Financiera Credinka', 'Analista Junior', '2024-05-01', NULL, 4200.00);

-- =============================================
-- CLIENTES RANGO PLATA
-- Ingresos S/4,500 - S/6,500, Endeudamiento <35%, Historial 2+ años
-- =============================================

-- PLATA #1: Profesional técnico con promoción reciente
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025002001', 'Marina Elizabeth', 'Flores Castillo', 'marina.flores@email.com', '+51987650200', 5200.00, 1650.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025002001'), 'Interbank', 'Asistente de Operaciones', '2022-03-01', '2024-02-29', 4200.00),
((SELECT id FROM customers WHERE identity_document = '10025002001'), 'Interbank', 'Coordinadora de Procesos', '2024-03-01', NULL, 5200.00);

-- PLATA #2: Consultor independiente con clientes fijos
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025002002', 'Carlos Roberto', 'Mendoza Silva', 'carlos.mendoza@email.com', '+51987650201', 5800.00, 1900.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025002002'), 'KPMG Perú', 'Consultor Junior', '2021-08-01', '2023-12-31', 4800.00),
-- Gap pequeño de 15 días
((SELECT id FROM customers WHERE identity_document = '10025002002'), 'Consultoría Independiente', 'Consultor Senior Freelance', '2024-01-15', NULL, 5800.00);

-- PLATA #3: Empleado bancario estable con experiencia
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025002003', 'Rosa Mercedes', 'Vargas Huamán', 'rosa.vargas@email.com', '+51987650202', 6100.00, 2000.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025002003'), 'Scotiabank Perú', 'Analista de Riesgo', '2022-01-10', NULL, 6100.00);

-- =============================================
-- CLIENTES RANGO ORO  
-- Ingresos S/6,500 - S/10,000, Endeudamiento <30%, Historial 3+ años
-- =============================================

-- ORO #1: Ejecutivo bancario con trayectoria sólida
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025003001', 'Fernando Andrés', 'Salazar Ponce', 'fernando.salazar@email.com', '+51987650300', 7500.00, 2100.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025003001'), 'BCP', 'Analista de Banca Corporativa', '2020-06-01', '2023-05-31', 6200.00),
((SELECT id FROM customers WHERE identity_document = '10025003001'), 'BCP', 'Ejecutivo Senior Corporativo', '2023-06-01', NULL, 7500.00);

-- ORO #2: Ingeniera de sistemas con progresión técnica
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025003002', 'Patricia Alejandra', 'Romero Gonzales', 'patricia.romero@email.com', '+51987650301', 8200.00, 2300.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025003002'), 'IBM Perú', 'Desarrolladora Senior', '2020-09-01', '2022-12-31', 6800.00),
-- Cambio directo, sin gap
((SELECT id FROM customers WHERE identity_document = '10025003002'), 'Microsoft Perú', 'Tech Lead', '2023-01-01', NULL, 8200.00);

-- ORO #3: Contador público senior en multinacional
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025003003', 'Ricardo Augusto', 'Herrera Morales', 'ricardo.herrera@email.com', '+51987650302', 9200.00, 2600.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025003003'), 'Deloitte Perú', 'Auditor Senior', '2019-03-01', '2022-02-28', 7200.00),
((SELECT id FROM customers WHERE identity_document = '10025003003'), 'PwC Perú', 'Manager de Auditoría', '2022-03-01', NULL, 9200.00);

-- =============================================
-- CLIENTES RANGO PLATINO
-- Ingresos S/10,000 - S/20,000, Endeudamiento <25%, Historial 5+ años
-- =============================================

-- PLATINO #1: Gerente de banca corporativa
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025004001', 'Eduardo Manuel', 'Vega Bustamante', 'eduardo.vega@email.com', '+51987650400', 14500.00, 3200.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025004001'), 'BBVA Continental', 'Ejecutivo de Banca Empresarial', '2018-01-15', '2021-12-31', 10800.00),
((SELECT id FROM customers WHERE identity_document = '10025004001'), 'BBVA Continental', 'Gerente de Grandes Empresas', '2022-01-01', NULL, 14500.00);

-- PLATINO #2: Directora de operaciones en retail
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025004002', 'Claudia Esperanza', 'Torres Maldonado', 'claudia.torres@email.com', '+51987650401', 16800.00, 3800.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025004002'), 'Saga Falabella', 'Gerente de Operaciones Regional', '2017-08-01', '2020-07-31', 12000.00),
((SELECT id FROM customers WHERE identity_document = '10025004002'), 'Ripley Perú', 'Directora de Operaciones', '2020-08-01', NULL, 16800.00);

-- PLATINO #3: Consultor senior independiente especializado
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025004003', 'Gonzalo Patricio', 'Alvarado Chávez', 'gonzalo.alvarado@email.com', '+51987650402', 18200.00, 4100.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025004003'), 'McKinsey & Company', 'Senior Consultant', '2016-06-01', '2021-05-31', 14500.00),
((SELECT id FROM customers WHERE identity_document = '10025004003'), 'Consultoría Estratégica Propia', 'Director y Consultor Principal', '2021-06-01', NULL, 18200.00);

-- =============================================
-- CLIENTES RANGO PREMIUM
-- Ingresos S/20,000+, Endeudamiento <20%, Historial ejecutivo 5+ años
-- =============================================

-- PREMIUM #1: CEO de empresa mediana
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025005001', 'Alejandro José', 'Mendoza Vargas', 'alejandro.mendoza@email.com', '+51987650500', 35000.00, 6000.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025005001'), 'Grupo Intercorp', 'Gerente General División Retail', '2017-01-01', '2021-12-31', 24000.00),
((SELECT id FROM customers WHERE identity_document = '10025005001'), 'Inversiones Propias S.A.C.', 'CEO y Fundador', '2022-01-01', NULL, 35000.00);

-- PREMIUM #2: CFO de multinacional
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025005002', 'Isabella Carolina', 'Castañeda Pérez', 'isabella.castaneda@email.com', '+51987650501', 28500.00, 5200.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025005002'), 'Nestlé Perú', 'Directora Financiera Regional Andina', '2018-03-01', '2022-02-28', 22000.00),
((SELECT id FROM customers WHERE identity_document = '10025005002'), 'Unilever Andina', 'Chief Financial Officer', '2022-03-01', NULL, 28500.00);

-- PREMIUM #3: Inversionista y empresario serial
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, active)
VALUES ('10025005003', 'Rodrigo Sebastián', 'Espinoza Sánchez', 'rodrigo.espinoza@email.com', '+51987650502', 42000.00, 7500.00, true);

INSERT INTO employment_history (customer_id, company_name, position, start_date, end_date, income)
VALUES 
((SELECT id FROM customers WHERE identity_document = '10025005003'), 'Credicorp Capital', 'Managing Director Private Equity', '2015-09-01', '2020-08-31', 25000.00),
((SELECT id FROM customers WHERE identity_document = '10025005003'), 'Espinoza Ventures', 'Founding Partner y Angel Investor', '2020-09-01', NULL, 42000.00);