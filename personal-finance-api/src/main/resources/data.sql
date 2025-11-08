-- Datos iniciales para categorías
INSERT INTO categories (id, name, type) VALUES
('cat-001', 'Alimentación', 'EXPENSE'),
('cat-002', 'Transporte', 'EXPENSE'),
('cat-003', 'Salario', 'INCOME'),
('cat-004', 'Vivienda', 'EXPENSE'),
('cat-005', 'Entretenimiento', 'EXPENSE'),
('cat-006', 'Freelance', 'INCOME'),
('cat-007', 'Servicios Públicos', 'EXPENSE'),
('cat-008', 'Inversiones', 'INCOME'),
('cat-009', 'Educación', 'EXPENSE'),
('cat-010', 'Salud', 'EXPENSE')
ON CONFLICT (id) DO NOTHING;