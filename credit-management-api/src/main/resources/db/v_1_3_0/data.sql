-- =============================================
-- Script de migración v_1_3_0 - Datos iniciales
-- Gestión de Rankings/Clasificaciones - DML  
-- Author: Marco Villarreal
-- =============================================

-- Inserción de rangos iniciales del sistema
INSERT INTO ranks (id, name, description, active, created_at, updated_at) VALUES
('BRONCE', 'BRONCE', 'Cliente con ingresos básicos entre S/930 - S/3,000 mensuales. Trabajador dependiente con contrato temporal o independiente con ingresos variables. Historial crediticio limitado o en construcción. Acceso a productos crediticios básicos como préstamos personales pequeños y tarjetas de crédito con límites bajos. Montos hasta S/15,000.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('PLATA', 'PLATA', 'Cliente consolidado con ingresos estables entre S/3,000 - S/6,000 mensuales. Empleado con antigüedad laboral 1-3 años en empresa formal o independiente con negocio registrado. Historial crediticio favorable con operaciones diversificadas. Cuenta con ahorros, posible CTS, algunas inversiones menores. Accede a préstamos personales amplios, créditos vehiculares, tarjetas de crédito gold, financiamiento de vivienda inicial. Montos hasta S/80,000.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('ORO', 'ORO', 'Cliente premium con ingresos sólidos entre S/6,000 - S/12,000 mensuales. Profesional universitario, ejecutivo medio, empresario con negocio establecido 3+ años. Excelente historial crediticio con diversificación en múltiples productos. Portfolio balanceado con ahorros, CTS, inversiones en fondos, acciones. Productos preferenciales: préstamos corporativos, créditos hipotecarios preferenciales, tarjetas platinum, seguros integrales. Montos hasta S/200,000.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('PLATINO', 'PLATINO', 'Cliente VIP con ingresos altos entre S/12,000 - S/25,000 mensuales. Alta gerencia, profesional especializado, empresario exitoso con empresas consolidadas. Historial crediticio impecable con relaciones bancarias múltiples. Portfolio diversificado con inversiones avanzadas, bienes raíces, fondos de inversión. Acceso a banca privada: créditos corporativos grandes, financiamiento inmobiliario premium, productos de inversión exclusivos. Montos hasta S/500,000.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('DIAMANTE', 'DIAMANTE', 'Cliente ultra premium con ingresos superiores a S/25,000 mensuales. Director ejecutivo, empresario multinacional, inversionista profesional. Patrimonio sustancial con múltiples fuentes de ingresos. Relationships bancarias internacionales y productos de banca privada global. Portfolio sofisticado con inversiones alternativas, private equity, bienes raíces comerciales. Productos exclusivos: financiamiento estructurado, créditos sindicados, gestión integral de patrimonio. Montos ilimitados según evaluación.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

$EXECUTE$