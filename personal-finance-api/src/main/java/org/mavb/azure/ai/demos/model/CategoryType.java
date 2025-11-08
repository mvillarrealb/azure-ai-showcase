package org.mavb.azure.ai.demos.model;

/**
 * Tipos de categorías financieras según especificación OpenAPI
 */
public enum CategoryType {
    /**
     * Categoría de ingreso
     */
    INCOME("income"),
    
    /**
     * Categoría de gasto
     */
    EXPENSE("expense");
    
    private final String value;
    
    CategoryType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Convierte un string del OpenAPI al enum
     */
    public static CategoryType fromValue(String value) {
        for (CategoryType type : CategoryType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de categoría inválido: " + value);
    }
}