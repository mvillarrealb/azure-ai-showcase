-- DDL Script for Credit Management API Database
-- PostgreSQL Database Schema

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    identity_document VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    monthly_income DECIMAL(15,2),
    current_debt DECIMAL(15,2) DEFAULT 0,
    credit_score INTEGER,
    employment_type VARCHAR(20) CHECK (employment_type IN ('DEPENDIENTE', 'INDEPENDIENTE', 'EMPRESARIO')),
    risk_level VARCHAR(20) CHECK (risk_level IN ('BAJO', 'MEDIO', 'ALTO')),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create credit_products table
CREATE TABLE IF NOT EXISTS credit_products (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    subcategory VARCHAR(100) NOT NULL,
    minimum_amount DECIMAL(15,2) NOT NULL,
    maximum_amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(5) NOT NULL CHECK (currency IN ('S/', 'USD')),
    term VARCHAR(50) NOT NULL,
    minimum_rate DECIMAL(5,2) NOT NULL,
    maximum_rate DECIMAL(5,2) NOT NULL,
    requirements JSONB,
    features JSONB,
    benefits JSONB,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_customers_identity_document ON customers(identity_document);
CREATE INDEX IF NOT EXISTS idx_customers_active ON customers(active);
CREATE INDEX IF NOT EXISTS idx_customers_risk_level ON customers(risk_level);
CREATE INDEX IF NOT EXISTS idx_customers_employment_type ON customers(employment_type);
CREATE INDEX IF NOT EXISTS idx_customers_credit_score ON customers(credit_score);

CREATE INDEX IF NOT EXISTS idx_credit_products_active ON credit_products(active);
CREATE INDEX IF NOT EXISTS idx_credit_products_category ON credit_products(category);
CREATE INDEX IF NOT EXISTS idx_credit_products_currency ON credit_products(currency);
CREATE INDEX IF NOT EXISTS idx_credit_products_amounts ON credit_products(minimum_amount, maximum_amount);
CREATE INDEX IF NOT EXISTS idx_credit_products_category_currency ON credit_products(category, currency);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers
DROP TRIGGER IF EXISTS update_customers_updated_at ON customers;
CREATE TRIGGER update_customers_updated_at
    BEFORE UPDATE ON customers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_credit_products_updated_at ON credit_products;
CREATE TRIGGER update_credit_products_updated_at
    BEFORE UPDATE ON credit_products
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();