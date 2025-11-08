-- DML Script for Credit Management API
-- Insert initial credit products data from productos_financieros_creditos.json

-- Insert credit products data
INSERT INTO credit_products (id, name, description, category, subcategory, minimum_amount, maximum_amount, currency, term, minimum_rate, maximum_rate, requirements, features, benefits) VALUES

-- Crédito Personal
('CP-PEN-001', 'Crédito Personal Express', 'Crédito personal de rápida aprobación para gastos inmediatos sin garantía específica.', 'Crédito Personal', 'Crédito Personal a Corto Plazo', 1000, 15000, 'S/', '6 a 12 meses', 12.00, 16.00, 
    '["DNI vigente", "Recibos de ingresos de los últimos 3 meses", "Constancia laboral", "Historial crediticio regular", "Ingresos mínimos S/ 1,500"]',
    '["Aprobación rápida", "Sin garantía específica", "Pagos fijos mensuales"]',
    '["Liquidez inmediata", "Proceso 100% digital", "Tasas competitivas"]'),

('CP-PEN-002', 'Crédito Personal Premium', 'Crédito personal con mejores condiciones para clientes con excelente historial crediticio.', 'Crédito Personal', 'Crédito Personal a Mediano Plazo', 15000, 50000, 'S/', '13 a 24 meses', 14.00, 20.00,
    '["DNI vigente", "Recibos de ingresos de los últimos 6 meses", "Constancia laboral con mínimo 1 año de antigüedad", "Historial crediticio bueno", "Ingresos mínimos S/ 3,000"]',
    '["Montos altos", "Plazos extendidos", "Condiciones preferenciales"]',
    '["Mayor capacidad de financiamiento", "Mejores tasas", "Flexibilidad de pago"]'),

('CP-USD-001', 'Personal Credit Express USD', 'Crédito personal en dólares para gastos en moneda extranjera con aprobación rápida.', 'Crédito Personal', 'Crédito Personal a Corto Plazo', 300, 5000, 'USD', '6 a 12 meses', 12.50, 17.00,
    '["DNI vigente", "Recibos de ingresos de los últimos 3 meses", "Constancia laboral", "Historial crediticio regular", "Ingresos mínimos USD 450"]',
    '["Financiamiento en dólares", "Aprobación rápida", "Sin garantía específica"]',
    '["Protección cambiaria", "Gastos en moneda extranjera", "Proceso ágil"]'),

('CP-USD-002', 'Personal Credit Premium USD', 'Crédito personal en dólares con condiciones preferenciales para clientes VIP.', 'Crédito Personal', 'Crédito Personal a Mediano Plazo', 5000, 15000, 'USD', '13 a 24 meses', 15.00, 20.00,
    '["DNI vigente", "Recibos de ingresos de los últimos 6 meses", "Constancia laboral con mínimo 1 año de antigüedad", "Historial crediticio bueno", "Ingresos mínimos USD 900"]',
    '["Montos altos en USD", "Condiciones VIP", "Plazos extendidos"]',
    '["Protección cambiaria", "Tasas preferenciales", "Servicio premium"]'),

-- Crédito Hipotecario
('CH-PEN-001', 'Crédito Hipotecario Mi Primera Casa', 'Crédito hipotecario especial para la compra de primera vivienda con beneficios del estado.', 'Crédito Hipotecario', 'Crédito Hipotecario Primera Vivienda', 50000, 200000, 'S/', '10 a 20 años', 6.00, 8.50,
    '["DNI vigente", "Recibos de ingresos de los últimos 6 meses", "Constancia laboral con mínimo 2 años de antigüedad", "Historial crediticio bueno", "Ingresos familiares mínimos S/ 5,000", "Tasación de la propiedad", "Certificado de no poseer otra vivienda"]',
    '["Primera vivienda", "Beneficios estatales", "Tasas preferenciales"]',
    '["Realización del sueño de casa propia", "Apoyo del estado", "Cuotas accesibles"]'),

('CH-PEN-002', 'Crédito Hipotecario Traditional', 'Crédito hipotecario convencional para compra o construcción de vivienda.', 'Crédito Hipotecario', 'Crédito Hipotecario Convencional', 80000, 500000, 'S/', '15 a 30 años', 7.00, 10.00,
    '["DNI vigente", "Recibos de ingresos de los últimos 6 meses", "Constancia laboral con mínimo 2 años de antigüedad", "Historial crediticio excelente", "Ingresos familiares mínimos S/ 8,000", "Tasación de la propiedad", "Cuota inicial del 20% mínimo"]',
    '["Montos altos", "Plazos largos", "Flexibilidad de uso"]',
    '["Inversión patrimonial", "Estabilidad habitacional", "Construcción de patrimonio"]'),

('CH-USD-001', 'Mortgage First Home USD', 'Crédito hipotecario en dólares para primera vivienda con condiciones especiales.', 'Crédito Hipotecario', 'Crédito Hipotecario Primera Vivienda', 15000, 60000, 'USD', '10 a 20 años', 6.50, 9.00,
    '["DNI vigente", "Recibos de ingresos de los últimos 6 meses", "Constancia laboral con mínimo 2 años de antigüedad", "Historial crediticio bueno", "Ingresos familiares mínimos USD 1,500", "Tasación de la propiedad", "Certificado de no poseer otra vivienda"]',
    '["Primera vivienda USD", "Protección cambiaria", "Condiciones especiales"]',
    '["Casa propia en dólares", "Estabilidad cambiaria", "Inversión segura"]'),

('CH-USD-002', 'Mortgage Premium USD', 'Crédito hipotecario en dólares para clientes premium con propiedades de alto valor.', 'Crédito Hipotecario', 'Crédito Hipotecario Convencional', 60000, 150000, 'USD', '15 a 30 años', 7.50, 10.00,
    '["DNI vigente", "Recibos de ingresos de los últimos 6 meses", "Constancia laboral con mínimo 2 años de antigüedad", "Historial crediticio excelente", "Ingresos familiares mínimos USD 2,400", "Tasación de la propiedad", "Cuota inicial del 25% mínimo"]',
    '["Alto valor en USD", "Servicio premium", "Condiciones exclusivas"]',
    '["Propiedades de lujo", "Inversión premium", "Portafolio inmobiliario"]'),

-- Crédito Automotriz
('CA-PEN-001', 'Crédito Automotriz Nuevo', 'Financiamiento para la compra de vehículos nuevos con tasas preferenciales.', 'Crédito Automotriz', 'Crédito Automotriz Vehículo Nuevo', 15000, 80000, 'S/', '2 a 5 años', 8.00, 12.00,
    '["DNI vigente", "Licencia de conducir vigente", "Recibos de ingresos de los últimos 3 meses", "Constancia laboral con mínimo 1 año de antigüedad", "Historial crediticio bueno", "Ingresos mínimos S/ 2,500", "Cuota inicial del 20% mínimo"]',
    '["Vehículos nuevos", "Tasas preferenciales", "Garantía de fábrica"]',
    '["Movilidad garantizada", "Vehículo 0 km", "Garantía completa"]'),

('CA-PEN-002', 'Crédito Automotriz Usado', 'Financiamiento para la compra de vehículos usados con hasta 8 años de antigüedad.', 'Crédito Automotriz', 'Crédito Automotriz Vehículo Usado', 5000, 60000, 'S/', '1 a 4 años', 10.00, 15.00,
    '["DNI vigente", "Licencia de conducir vigente", "Recibos de ingresos de los últimos 3 meses", "Constancia laboral con mínimo 1 año de antigüedad", "Historial crediticio regular", "Ingresos mínimos S/ 2,000", "Cuota inicial del 30% mínimo", "Tasación del vehículo"]',
    '["Vehículos usados", "Hasta 8 años", "Montos accesibles"]',
    '["Movilidad económica", "Inversión accesible", "Opciones variadas"]'),

('CA-USD-001', 'Auto Credit New Vehicle USD', 'Financiamiento en dólares para compra de vehículos nuevos importados.', 'Crédito Automotriz', 'Crédito Automotriz Vehículo Nuevo', 5000, 25000, 'USD', '2 a 5 años', 8.50, 13.00,
    '["DNI vigente", "Licencia de conducir vigente", "Recibos de ingresos de los últimos 3 meses", "Constancia laboral con mínimo 1 año de antigüedad", "Historial crediticio bueno", "Ingresos mínimos USD 750", "Cuota inicial del 25% mínimo"]',
    '["Vehículos importados", "Financiamiento USD", "Modelos exclusivos"]',
    '["Vehículos de lujo", "Tecnología avanzada", "Estatus premium"]'),

('CA-USD-002', 'Auto Credit Used Vehicle USD', 'Financiamiento en dólares para vehículos usados de procedencia extranjera.', 'Crédito Automotriz', 'Crédito Automotriz Vehículo Usado', 1500, 18000, 'USD', '1 a 4 años', 11.00, 15.00,
    '["DNI vigente", "Licencia de conducir vigente", "Recibos de ingresos de los últimos 3 meses", "Constancia laboral con mínimo 1 año de antigüedad", "Historial crediticio regular", "Ingresos mínimos USD 600", "Cuota inicial del 35% mínimo", "Tasación del vehículo"]',
    '["Vehículos importados usados", "Financiamiento USD", "Modelos variados"]',
    '["Acceso a vehículos extranjeros", "Calidad internacional", "Opciones diversas"]'),

-- Crédito Empresarial
('CE-PEN-001', 'Crédito Empresarial PYME', 'Financiamiento para pequeñas y medianas empresas para capital de trabajo e inversión.', 'Crédito Empresarial', 'Crédito Empresarial PYME', 10000, 200000, 'S/', '1 a 5 años', 7.00, 10.00,
    '["RUC vigente", "Estados financieros de los últimos 2 años", "Flujo de caja proyectado", "Constitución de la empresa", "Historial crediticio empresarial bueno", "Ventas anuales mínimas S/ 120,000", "Garantías específicas según monto"]',
    '["Capital de trabajo", "Inversión empresarial", "Plazos flexibles"]',
    '["Crecimiento empresarial", "Liquidez operativa", "Expansión de negocio"]'),

('CE-PEN-002', 'Crédito Empresarial Corporativo', 'Financiamiento para grandes empresas con necesidades de capital significativas.', 'Crédito Empresarial', 'Crédito Empresarial Corporativo', 200000, 1000000, 'S/', '3 a 10 años', 8.00, 12.00,
    '["RUC vigente", "Estados financieros auditados de los últimos 3 años", "Plan de negocio detallado", "Constitución de la empresa", "Historial crediticio empresarial excelente", "Ventas anuales mínimas S/ 2,000,000", "Garantías reales", "Rating crediticio mínimo"]',
    '["Montos corporativos", "Plazos largos", "Condiciones corporativas"]',
    '["Expansión corporativa", "Proyectos grandes", "Consolidación empresarial"]'),

('CE-USD-001', 'Business Credit SME USD', 'Financiamiento en dólares para pequeñas empresas con operaciones de comercio exterior.', 'Crédito Empresarial', 'Crédito Empresarial PYME', 3000, 60000, 'USD', '1 a 5 años', 7.50, 11.00,
    '["RUC vigente", "Estados financieros de los últimos 2 años", "Flujo de caja proyectado", "Constitución de la empresa", "Historial crediticio empresarial bueno", "Ventas anuales mínimas USD 36,000", "Experiencia en comercio exterior", "Garantías específicas según monto"]',
    '["Comercio exterior", "Financiamiento USD", "PYME especializada"]',
    '["Operaciones internacionales", "Protección cambiaria", "Expansión global"]');