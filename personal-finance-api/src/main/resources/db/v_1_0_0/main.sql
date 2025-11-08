CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uk_categories_name_type UNIQUE (name, type)
);

COMMENT ON TABLE categories IS 'Tabla que almacena las categorías de transacciones financieras.';
COMMENT ON COLUMN categories.id IS 'Identificador único de la categoría.';
COMMENT ON COLUMN categories.name IS 'Nombre descriptivo de la categoría.';
COMMENT ON COLUMN categories.type IS 'Tipo de categoría: INCOME o EXPENSE.';

CREATE TABLE IF NOT EXISTS transactions (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    amount DECIMAL(10,2) NOT NULL,
    date TIMESTAMP WITH TIME ZONE NOT NULL,
    description VARCHAR(500) NOT NULL,
    category_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) 
        REFERENCES categories(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT chk_transactions_amount_not_zero CHECK (amount != 0),
    CONSTRAINT chk_transactions_description_not_empty CHECK (trim(description) != '')
);

COMMENT ON TABLE transactions IS 'Tabla que almacena las transacciones financieras del usuario.';
COMMENT ON COLUMN transactions.id IS 'Identificador único de la transacción.';
COMMENT ON COLUMN transactions.amount IS 'Monto de la transacción.';
COMMENT ON COLUMN transactions.date IS 'Fecha y hora de la transacción.';
COMMENT ON COLUMN transactions.description IS 'Descripción de la transacción.';
COMMENT ON COLUMN transactions.category_id IS 'Referencia a la categoría.';

CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(date);
CREATE INDEX IF NOT EXISTS idx_transactions_category ON transactions(category_id);
CREATE INDEX IF NOT EXISTS idx_transactions_amount ON transactions(amount);


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
$EXECUTE$