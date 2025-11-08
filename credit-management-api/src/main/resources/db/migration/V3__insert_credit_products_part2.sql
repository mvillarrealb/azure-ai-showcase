-- DML Script Part 2 - Continuation of credit products insertion

INSERT INTO credit_products (id, name, description, category, subcategory, minimum_amount, maximum_amount, currency, term, minimum_rate, maximum_rate, requirements, features, benefits) VALUES

-- More Crédito Empresarial
('CE-USD-002', 'Corporate Credit USD', 'Crédito corporativo en dólares para empresas con operaciones internacionales.', 'Crédito Empresarial', 'Crédito Empresarial Corporativo', 60000, 300000, 'USD', '3 a 10 años', 8.50, 12.00,
    '["RUC vigente", "Estados financieros auditados de los últimos 3 años", "Plan de negocio detallado", "Constitución de la empresa", "Historial crediticio empresarial excelente", "Ventas anuales mínimas USD 600,000", "Garantías reales", "Rating crediticio internacional"]',
    '["Operaciones internacionales", "Montos corporativos USD", "Rating internacional"]',
    '["Expansión internacional", "Operaciones globales", "Diversificación geográfica"]'),

-- Crédito Estudiantil
('CES-PEN-001', 'Crédito Estudiantil Pregrado', 'Financiamiento para estudios universitarios de pregrado en instituciones nacionales.', 'Crédito Estudiantil', 'Crédito Estudiantil Pregrado', 2000, 30000, 'S/', '1 a 4 años', 5.00, 7.00,
    '["DNI vigente del estudiante", "DNI vigente del garante (padre/madre/tutor)", "Constancia de matrícula", "Carta de aceptación de la institución educativa", "Recibos de ingresos del garante (últimos 3 meses)", "Historial crediticio del garante regular o bueno", "Ingresos mínimos del garante S/ 2,000"]',
    '["Educación superior", "Tasas preferenciales", "Apoyo educativo"]',
    '["Inversión en educación", "Futuro profesional", "Desarrollo personal"]'),

('CES-PEN-002', 'Crédito Estudiantil Postgrado', 'Financiamiento para estudios de especialización, maestría o doctorado.', 'Crédito Estudiantil', 'Crédito Estudiantil Postgrado', 5000, 50000, 'S/', '2 a 5 años', 6.00, 8.00,
    '["DNI vigente del estudiante", "Título universitario o técnico", "Carta de aceptación de la institución educativa", "Recibos de ingresos del solicitante o garante (últimos 6 meses)", "Historial crediticio bueno", "Ingresos mínimos S/ 3,000", "Plan de estudios detallado"]',
    '["Postgrado", "Especialización", "Alta formación"]',
    '["Especialización profesional", "Mejores oportunidades", "Desarrollo académico"]'),

('CES-USD-001', 'Student Credit Undergraduate USD', 'Financiamiento en dólares para estudios universitarios en el extranjero.', 'Crédito Estudiantil', 'Crédito Estudiantil Pregrado', 600, 9000, 'USD', '1 a 4 años', 5.50, 7.50,
    '["DNI vigente del estudiante", "DNI vigente del garante (padre/madre/tutor)", "Visa de estudiante", "Carta de aceptación de la institución educativa extranjera", "Recibos de ingresos del garante (últimos 6 meses)", "Historial crediticio del garante bueno", "Ingresos mínimos del garante USD 600"]',
    '["Estudios en el extranjero", "Financiamiento USD", "Experiencia internacional"]',
    '["Educación internacional", "Experiencia global", "Competitividad mundial"]'),

('CES-USD-002', 'Student Credit Graduate USD', 'Crédito estudiantil en dólares para programas de postgrado en universidades extranjeras.', 'Crédito Estudiantil', 'Crédito Estudiantil Postgrado', 1500, 15000, 'USD', '2 a 5 años', 6.50, 8.00,
    '["DNI vigente del estudiante", "Título universitario apostillado", "Visa de estudiante", "Carta de aceptación de la institución educativa extranjera", "Recibos de ingresos del solicitante o garante (últimos 6 meses)", "Historial crediticio excelente", "Ingresos mínimos USD 900", "Certificado de idiomas (según destino)"]',
    '["Postgrado internacional", "Universidades extranjeras", "Alta especialización"]',
    '["Postgrado de clase mundial", "Red internacional", "Excelencia académica"]'),

-- Microcrédito
('CMC-PEN-001', 'Crédito MiCrédito', 'Microcrédito para emprendedores y pequeños negocios con montos accesibles y requisitos flexibles.', 'Microcrédito', 'Microcrédito Emprendimiento', 500, 8000, 'S/', '3 a 18 meses', 15.00, 25.00,
    '["DNI vigente", "Recibo de servicios del domicilio", "Constancia de ingresos del negocio", "Referencias comerciales", "Historial crediticio básico", "Ingresos mínimos S/ 800"]',
    '["Requisitos flexibles", "Montos pequeños", "Apoyo al emprendimiento"]',
    '["Inicio de negocio", "Microempresa", "Independencia económica"]'),

-- Créditos Específicos
('CTJ-PEN-001', 'Crédito Tarjeta Soat', 'Crédito específico para pago de SOAT vehicular con descuentos por pronto pago.', 'Crédito Específico', 'Crédito SOAT Vehicular', 150, 800, 'S/', '3 a 12 meses', 8.00, 12.00,
    '["DNI vigente", "Licencia de conducir vigente", "Tarjeta de propiedad del vehículo", "Constancia de ingresos", "Historial crediticio regular"]',
    '["SOAT vehicular", "Descuentos por pronto pago", "Proceso rápido"]',
    '["Cumplimiento legal", "Protección vehicular", "Tranquilidad vial"]'),

-- Crédito Revolvente
('CRV-PEN-001', 'Crédito Revolvente Empresarial', 'Línea de crédito revolvente para empresas que necesitan capital de trabajo flexible.', 'Crédito Revolvente', 'Crédito Revolvente Capital Trabajo', 20000, 300000, 'S/', '12 meses renovables', 9.00, 14.00,
    '["RUC vigente", "Estados financieros de los últimos 2 años", "Flujo de caja mensual", "Constitución de la empresa", "Historial crediticio empresarial bueno", "Ventas anuales mínimas S/ 300,000", "Garantías líquidas"]',
    '["Disposiciones parciales", "Pagos flexibles", "Sin comisión por prepago", "Renovación automática", "Banca digital incluida"]',
    '["Capital de trabajo inmediato", "Flexibilidad en el uso de fondos", "Tasas competitivas", "Gestión 100% digital"]'),

-- Crédito Sectorial
('CAG-PEN-001', 'Crédito Agropecuario', 'Financiamiento especializado para actividades agrícolas, ganaderas y de pesca.', 'Crédito Sectorial', 'Crédito Agropecuario', 5000, 150000, 'S/', '6 meses a 3 años', 10.00, 16.00,
    '["DNI vigente", "RUC (si aplica)", "Título de propiedad o contrato de arrendamiento de tierras", "Plan productivo", "Experiencia en actividad agropecuaria mínima 2 años", "Referencias de proveedores del sector", "Garantía sobre cosecha o ganado"]',
    '["Sector agropecuario", "Financiamiento especializado", "Garantías sectoriales"]',
    '["Desarrollo rural", "Producción agrícola", "Sostenibilidad alimentaria"]'),

-- Crédito Construcción
('CCO-USD-001', 'Construction Credit USD', 'Crédito en dólares para construcción y mejoras de vivienda con desembolsos programados.', 'Crédito Construcción', 'Crédito Construcción Vivienda', 8000, 80000, 'USD', '1 a 3 años', 8.00, 12.00,
    '["DNI vigente", "Título de propiedad del terreno", "Proyecto arquitectónico aprobado", "Presupuesto de construcción detallado", "Licencia de construcción", "Historial crediticio bueno", "Ingresos mínimos USD 1,200", "Cronograma de obra"]',
    '["Desembolsos programados", "Seguimiento de obra", "Asesoría técnica"]',
    '["Construcción planificada", "Control de calidad", "Cumplimiento de cronograma"]'),

-- Crédito Tecnológico
('CTC-PEN-001', 'Crédito Tecnológico', 'Financiamiento para compra de equipos tecnológicos, software y hardware especializado.', 'Crédito Tecnológico', 'Crédito Equipos TI', 3000, 50000, 'S/', '6 meses a 3 años', 11.00, 16.00,
    '["DNI vigente", "RUC (para empresas)", "Cotización de equipos a adquirir", "Constancia laboral o de actividad empresarial", "Recibos de ingresos de los últimos 3 meses", "Historial crediticio bueno", "Ingresos mínimos S/ 2,500"]',
    '["Equipos tecnológicos", "Software especializado", "Modernización digital"]',
    '["Transformación digital", "Competitividad tecnológica", "Innovación empresarial"]'),

-- Crédito Médico
('CMS-PEN-001', 'Crédito Médico Especializado', 'Financiamiento para tratamientos médicos, cirugías y equipos médicos especializados.', 'Crédito Médico', 'Crédito Tratamientos Médicos', 2000, 80000, 'S/', '6 meses a 4 años', 9.00, 14.00,
    '["DNI vigente", "Presupuesto médico detallado", "Orden médica o prescripción", "Historial clínico", "Recibos de ingresos de los últimos 3 meses", "Historial crediticio regular", "Ingresos mínimos S/ 1,800", "Aval médico de la institución"]',
    '["Tratamientos especializados", "Equipos médicos", "Financiamiento sanitario"]',
    '["Atención médica especializada", "Salud y bienestar", "Calidad de vida"]'),

-- Crédito Educación Internacional
('CEI-USD-001', 'Education International Credit USD', 'Crédito especializado en dólares para educación internacional, cursos y certificaciones.', 'Crédito Educación Internacional', 'Crédito Cursos Internacionales', 1000, 25000, 'USD', '6 meses a 3 años', 7.00, 11.00,
    '["DNI vigente", "Carta de aceptación del programa educativo", "Brochure o información detallada del curso", "Constancia laboral", "Recibos de ingresos de los últimos 6 meses", "Historial crediticio bueno", "Ingresos mínimos USD 800", "Certificado de idiomas (si aplica)"]',
    '["Educación internacional", "Cursos especializados", "Certificaciones globales"]',
    '["Competencias internacionales", "Desarrollo profesional", "Oportunidades globales"]'),

-- Crédito Turismo
('CTU-PEN-001', 'Crédito Turismo Nacional', 'Financiamiento para viajes turísticos nacionales, paquetes vacacionales y hospedajes.', 'Crédito Turismo', 'Crédito Turismo Nacional', 800, 12000, 'S/', '3 a 18 meses', 13.00, 18.00,
    '["DNI vigente", "Reserva o cotización turística", "Constancia laboral", "Recibos de ingresos de los últimos 2 meses", "Historial crediticio regular", "Ingresos mínimos S/ 1,500"]',
    '["Turismo nacional", "Paquetes turísticos", "Experiencias únicas"]',
    '["Descanso y recreación", "Conocimiento del país", "Momentos familiares"]'),

-- Crédito Verde
('CEN-PEN-001', 'Crédito Energía Renovable', 'Financiamiento para instalación de paneles solares, sistemas de energía renovable y eficiencia energética.', 'Crédito Verde', 'Crédito Energía Solar', 8000, 60000, 'S/', '2 a 8 años', 6.00, 10.00,
    '["DNI vigente", "Título de propiedad de la vivienda", "Cotización técnica del sistema", "Estudio de factibilidad técnica", "Constancia laboral", "Recibos de ingresos de los últimos 6 meses", "Historial crediticio bueno", "Ingresos mínimos S/ 3,500", "Certificación de instalador autorizado"]',
    '["Energía renovable", "Sostenibilidad ambiental", "Ahorro energético", "Tecnología verde"]',
    '["Ahorro en energía", "Contribución ambiental", "Tecnología sustentable", "Inversión a largo plazo"]');

-- Insert some sample customers for testing
INSERT INTO customers (identity_document, first_name, last_name, email, phone, monthly_income, current_debt, credit_score, employment_type, risk_level) VALUES
('12345678901', 'Juan', 'Pérez', 'juan.perez@email.com', '+51987654321', 8000.00, 2000.00, 750, 'DEPENDIENTE', 'BAJO'),
('10456789012', 'María', 'González', 'maria.gonzalez@email.com', '+51987654322', 15000.00, 5000.00, 720, 'EMPRESARIO', 'BAJO'),
('11223344556', 'Carlos', 'Rodriguez', 'carlos.rodriguez@email.com', '+51987654323', 5000.00, 3000.00, 650, 'INDEPENDIENTE', 'MEDIO'),
('98765432109', 'Ana', 'López', 'ana.lopez@email.com', '+51987654324', 12000.00, 8000.00, 580, 'DEPENDIENTE', 'MEDIO'),
('55443322110', 'Pedro', 'Martínez', 'pedro.martinez@email.com', '+51987654325', 3000.00, 4000.00, 520, 'INDEPENDIENTE', 'ALTO');