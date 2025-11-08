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
    ('TEST001', 'Ana Sofía', 'Rodríguez Vega', 'ana.rodriguez@email.com', '+51954321098', 3200.00, 800.00, 740, 'DEPENDIENTE', 'BAJO', true),
    ('TEST002', 'Roberto Miguel', 'Flores Castro', 'roberto.flores@email.com', '+51943210987', 8500.00, 3400.00, 680, 'INDEPENDIENTE', 'MEDIO', true);

-- Inserción de productos financieros de crédito
INSERT INTO credit_products (id, name, description, category, subcategory, minimum_amount, maximum_amount, currency, term, minimum_rate, maximum_rate, requirements, features, benefits, active)
VALUES 
    ('CP-PEN-001', 'Crédito Personal Express', 'Crédito personal de rápida aprobación para gastos inmediatos sin garantía específica.', 'Crédito Personal', 'Crédito Personal a Corto Plazo', 1000.00, 15000.00, 'S/', '6 a 12 meses', 12.00, 16.00, 
     '["DNI vigente", "Recibos de ingresos de los últimos 3 meses", "Constancia laboral", "Historial crediticio regular", "Ingresos mínimos S/ 1,500"]',
     '["Aprobación rápida", "Sin garantía específica", "Cuotas fijas"]',
     '["Tasa preferencial", "Proceso 100% digital", "Desembolso inmediato"]', true),
    
    ('CP-PEN-002', 'Crédito Personal Premium', 'Crédito personal con mejores condiciones para clientes con excelente historial crediticio.', 'Crédito Personal', 'Crédito Personal a Mediano Plazo', 15000.00, 50000.00, 'S/', '13 a 24 meses', 14.00, 20.00,
     '["DNI vigente", "Recibos de ingresos de los últimos 6 meses", "Constancia laboral con mínimo 1 año de antigüedad", "Historial crediticio bueno", "Ingresos mínimos S/ 3,000"]',
     '["Montos altos", "Plazos flexibles", "Cuotas fijas"]',
     '["Tasa competitiva", "Sin penalidad por prepago", "Asesoría financiera"]', true),
    
    ('CH-PEN-001', 'Crédito Hipotecario Mi Primera Casa', 'Crédito hipotecario especial para la compra de primera vivienda con beneficios del estado.', 'Crédito Hipotecario', 'Crédito Hipotecario Primera Vivienda', 50000.00, 200000.00, 'S/', '10 a 20 años', 6.00, 8.50,
     '["DNI vigente", "Recibos de ingresos de los últimos 6 meses", "Constancia laboral con mínimo 2 años de antigüedad", "Historial crediticio bueno", "Ingresos familiares mínimos S/ 5,000", "Tasación de la propiedad", "Certificado de no poseer otra vivienda"]',
     '["Beneficios estatales", "Tasas preferenciales", "Plazos largos"]',
     '["Subsidio gubernamental", "Deducción fiscal", "Seguro de desgravamen incluido"]', true),
    
    ('CA-PEN-001', 'Crédito Automotriz Nuevo', 'Financiamiento para la compra de vehículos nuevos con tasas preferenciales.', 'Crédito Automotriz', 'Crédito Automotriz Vehículo Nuevo', 15000.00, 80000.00, 'S/', '2 a 5 años', 8.00, 12.00,
     '["DNI vigente", "Licencia de conducir vigente", "Recibos de ingresos de los últimos 3 meses", "Constancia laboral con mínimo 1 año de antigüedad", "Historial crediticio bueno", "Ingresos mínimos S/ 2,500", "Cuota inicial del 20% mínimo"]',
     '["Vehículos nuevos", "Tasas preferenciales", "Seguro vehicular"]',
     '["SOAT incluido", "Seguro de desgravamen", "Mantenimiento gratuito primer año"]', true),
    
    ('CE-PEN-001', 'Crédito Empresarial PYME', 'Financiamiento para pequeñas y medianas empresas para capital de trabajo e inversión.', 'Crédito Empresarial', 'Crédito Empresarial PYME', 10000.00, 200000.00, 'S/', '1 a 5 años', 7.00, 10.00,
     '["RUC vigente", "Estados financieros de los últimos 2 años", "Flujo de caja proyectado", "Constitución de la empresa", "Historial crediticio empresarial bueno", "Ventas anuales mínimas S/ 120,000", "Garantías específicas según monto"]',
     '["Capital de trabajo", "Inversión en activos", "Línea de crédito"]',
     '["Asesoría empresarial", "Tasas competitivas", "Plazos flexibles"]', true),
    
    ('CP-USD-001', 'Personal Credit Express USD', 'Crédito personal en dólares para gastos en moneda extranjera con aprobación rápida.', 'Crédito Personal', 'Crédito Personal a Corto Plazo', 300.00, 5000.00, 'USD', '6 a 12 meses', 12.50, 17.00,
     '["DNI vigente", "Recibos de ingresos de los últimos 3 meses", "Constancia laboral", "Historial crediticio regular", "Ingresos mínimos USD 450"]',
     '["Moneda dólares", "Aprobación rápida", "Proceso digital"]',
     '["Protección cambiaria", "Cuotas en dólares", "Sin comisión por cambio"]', true),
    
    ('CMC-PEN-001', 'Crédito MiCrédito', 'Microcrédito para emprendedores y pequeños negocios con montos accesibles y requisitos flexibles.', 'Microcrédito', 'Microcrédito Emprendimiento', 500.00, 8000.00, 'S/', '3 a 18 meses', 15.00, 25.00,
     '["DNI vigente", "Recibo de servicios del domicilio", "Constancia de ingresos del negocio", "Referencias comerciales", "Historial crediticio básico", "Ingresos mínimos S/ 800"]',
     '["Requisitos flexibles", "Montos accesibles", "Apoyo al emprendimiento"]',
     '["Capacitación empresarial", "Red de proveedores", "Seguimiento personalizado"]', true),
    
    ('CEN-PEN-001', 'Crédito Energía Renovable', 'Financiamiento para instalación de paneles solares, sistemas de energía renovable y eficiencia energética.', 'Crédito Verde', 'Crédito Energía Solar', 8000.00, 60000.00, 'S/', '2 a 8 años', 6.00, 10.00,
     '["DNI vigente", "Título de propiedad de la vivienda", "Cotización técnica del sistema", "Estudio de factibilidad técnica", "Constancia laboral", "Recibos de ingresos de los últimos 6 meses", "Historial crediticio bueno", "Ingresos mínimos S/ 3,500", "Certificación de instalador autorizado"]',
     '["Energía renovable", "Beneficios ambientales", "Ahorro energético"]',
     '["Deducción fiscal", "Ahorro en factura eléctrica", "Contribución ambiental"]', true);

$EXECUTE$