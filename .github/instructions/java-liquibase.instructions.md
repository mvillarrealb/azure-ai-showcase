---
description: "Estándares y mejores prácticas para Liquibase en aplicaciones Spring Boot"
applyTo: "**/src/main/resources/db/**"
---

# Estándares Liquibase para Spring Boot

## Configuración Base Requerida

### 1. Dependencia en build.gradle
```groovy
dependencies {
    implementation 'org.liquibase:liquibase-core'
    // Otras dependencias...
}
```

### 2. Configuración en application.yaml
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: none  # OBLIGATORIO: Desactivar DDL automático de Hibernate
  liquibase:
    change-log: classpath:/db/changelog.yaml
    enabled: true
    drop-first: false
    default-schema: public
```

## Estructura de Directorios Estandarizada

### Organización de Archivos
```
src/main/resources/db/
├── changelog.yaml                # Archivo principal de changelog
├── v_1_0_0/                     # Directorio por versión
│   ├── main.sql                 # DDL principal (tablas, índices, constraints)
│   └── data.sql                 # DML inicial (datos de referencia)
├── v_1_1_0/                     # Próxima versión
│   ├── migrations.sql           # Cambios estructurales
│   └── data-updates.sql         # Actualizaciones de datos
└── v_2_0_0/                     # Versión mayor
    └── major-changes.sql
```

### Convención de Nomenclatura
- **Directorios**: `v_X_Y_Z` donde X.Y.Z es la versión semántica
- **Archivos DDL**: `main.sql` para estructura inicial, `migrations.sql` para cambios
- **Archivos DML**: `data.sql` para datos iniciales, `data-updates.sql` para actualizaciones
- **Delimitador personalizado**: `$EXECUTE$` para scripts complejos con funciones/triggers

## Plantilla changelog.yaml

### Estructura Base del Changelog
```yaml
databaseChangeLog:
  - changeSet:
      id: v_X_Y_Z
      author: "Nombre del Desarrollador"
      comment: |
        Descripción detallada del changeset incluyendo:
        - Tablas creadas/modificadas
        - Índices agregados
        - Constraints implementados
        - Datos iniciales cargados
      sqlFile:
        encoding: utf8
        stripComments: true
        path: "classpath:/db/v_X_Y_Z/main.sql"
        endDelimiter: "$EXECUTE$"
  
  # Changeset para datos iniciales (opcional)
  - changeSet:
      id: v_X_Y_Z_data
      author: "Nombre del Desarrollador"
      comment: "Carga de datos iniciales y de referencia"
      sqlFile:
        encoding: utf8
        stripComments: true
        path: "classpath:/db/v_X_Y_Z/data.sql"
      rollback:
        # Especificar rollback si es necesario
        sql: "DELETE FROM tabla WHERE condicion;"
```

## Estándares para Archivos SQL

### Estructura de main.sql
```sql
-- =====================================================================================================================
-- Nombre del Proyecto - Database Schema Definition
-- Version: X.Y.Z
-- Description: Descripción del propósito del schema
-- Author: Nombre del Desarrollador
-- Database: PostgreSQL 12+
-- =====================================================================================================================

-- =====================================================================================================================
-- TABLA: nombre_tabla
-- Descripción: Propósito y uso de la tabla
-- =====================================================================================================================

CREATE TABLE IF NOT EXISTS nombre_tabla (
    -- Comentarios detallados para cada columna
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    
    -- Campo con constraint y comentario
    campo VARCHAR(100) NOT NULL,
    
    -- Auditoría obligatoria
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT pk_nombre_tabla PRIMARY KEY (id),
    CONSTRAINT uk_nombre_tabla_campo UNIQUE (campo),
    CONSTRAINT fk_nombre_tabla_referencia FOREIGN KEY (referencia_id) 
        REFERENCES tabla_referencia(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- Comentarios PostgreSQL obligatorios
COMMENT ON TABLE nombre_tabla IS 'Descripción completa del propósito de la tabla';
COMMENT ON COLUMN nombre_tabla.campo IS 'Descripción detallada del campo y su uso';

-- =====================================================================================================================
-- ÍNDICES PARA OPTIMIZACIÓN DE CONSULTAS
-- =====================================================================================================================

-- Índices con comentarios explicativos
CREATE INDEX IF NOT EXISTS idx_nombre_tabla_campo ON nombre_tabla(campo);
COMMENT ON INDEX idx_nombre_tabla_campo IS 'Índice para optimizar consultas por campo específico';

-- =====================================================================================================================
-- TRIGGERS PARA AUDITORÍA AUTOMÁTICA
-- =====================================================================================================================

-- Función reutilizable para updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para la tabla
DROP TRIGGER IF EXISTS trigger_nombre_tabla_updated_at ON nombre_tabla;
CREATE TRIGGER trigger_nombre_tabla_updated_at
    BEFORE UPDATE ON nombre_tabla
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

$EXECUTE$
```

### Estructura de data.sql
```sql
-- =====================================================================================================================
-- DATOS INICIALES Y DE REFERENCIA
-- Version: X.Y.Z
-- =====================================================================================================================

-- Inserción con manejo de conflictos
INSERT INTO tabla_referencia (id, nombre, tipo) VALUES
('ref-001', 'Valor de Referencia 1', 'TIPO_A'),
('ref-002', 'Valor de Referencia 2', 'TIPO_B'),
('ref-003', 'Valor de Referencia 3', 'TIPO_C')
ON CONFLICT (id) DO NOTHING;

-- Datos con relaciones
INSERT INTO tabla_principal (id, campo, referencia_id) VALUES
(gen_random_uuid(), 'Dato Principal 1', 'ref-001'),
(gen_random_uuid(), 'Dato Principal 2', 'ref-002')
ON CONFLICT (campo) DO NOTHING;
```

## Mejores Prácticas Obligatorias

### 1. Convenciones de Nombres
- **Tablas**: `snake_case` en plural (ej: `credit_products`, `user_sessions`)
- **Columnas**: `snake_case` descriptivo (ej: `created_at`, `monthly_income`)
- **Índices**: `idx_tabla_campo` o `idx_tabla_campo1_campo2` para compuestos
- **Constraints**: `pk_`, `uk_`, `fk_`, `chk_` seguido del nombre de tabla y campo

### 2. Tipos de Datos Estandarizados
- **IDs primarios**: `UUID DEFAULT gen_random_uuid()` para nuevas tablas
- **IDs legacy**: `BIGSERIAL` solo si existe sistema anterior
- **Timestamps**: `TIMESTAMP WITH TIME ZONE` siempre
- **Montos**: `DECIMAL(15,2)` para precisión financiera
- **Texto corto**: `VARCHAR(N)` con límite específico
- **Texto largo**: `TEXT` para contenido extenso
- **Enums**: `VARCHAR(20) CHECK (campo IN ('VALOR1', 'VALOR2'))`

### 3. Auditoría Obligatoria
```sql
-- Campos de auditoría en TODAS las tablas
created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
```

### 4. Constraints de Integridad
- **Foreign Keys**: Siempre especificar `ON DELETE` y `ON UPDATE`
  - `ON DELETE RESTRICT` para proteger datos críticos
  - `ON DELETE CASCADE` solo para datos dependientes
  - `ON UPDATE CASCADE` por defecto
- **Check Constraints**: Para validaciones de dominio
- **Unique Constraints**: Para campos únicos compuestos

### 5. Índices de Performance
- **Obligatorios**:
  - Campos de búsqueda frecuente
  - Foreign keys
  - Campos de ordenamiento
  - Campos de filtros WHERE
- **Índices compuestos**: Para consultas con múltiples filtros
- **Índices parciales**: Para filtros con condiciones específicas

### 6. Documentación PostgreSQL
```sql
-- OBLIGATORIO: Comentarios para todas las tablas y columnas críticas
COMMENT ON TABLE tabla IS 'Descripción del propósito y uso';
COMMENT ON COLUMN tabla.campo IS 'Descripción detallada del campo';
COMMENT ON INDEX indice IS 'Propósito del índice y consultas que optimiza';
```

## Versionado y Evolución

### Estrategia de Versionado
- **v_1_0_0**: Versión inicial con estructura base
- **v_1_X_0**: Cambios menores (nuevas columnas, índices)
- **v_X_0_0**: Cambios mayores (nuevas tablas, restructuración)

### Changesets Incrementales
```yaml
# Para modificaciones de estructura existente
- changeSet:
    id: v_1_1_0_add_column
    author: "Developer"
    comment: "Agregar nueva columna a tabla existente"
    sql: |
      ALTER TABLE tabla_existente 
      ADD COLUMN nueva_columna VARCHAR(100);
      
      COMMENT ON COLUMN tabla_existente.nueva_columna 
      IS 'Descripción de la nueva columna';
```

## Configuraciones de Performance

### PostgreSQL Específico
```sql
-- Estadísticas extendidas para consultas complejas
CREATE STATISTICS IF NOT EXISTS stats_tabla_campos 
ON campo1, campo2 FROM tabla;

-- Configuración de autovacuum para tablas grandes
ALTER TABLE tabla_grande SET (
  autovacuum_vacuum_scale_factor = 0.1,
  autovacuum_analyze_scale_factor = 0.05
);
```

## Rollback y Recuperación

### Estrategias de Rollback
- **Data Changes**: Especificar rollback SQL explícito
- **Schema Changes**: Liquibase auto-rollback cuando sea posible
- **Backup Strategy**: Siempre respaldar antes de migraciones mayores

### Validaciones Pre-Migración
```sql
-- Verificaciones antes de ejecutar cambios críticos
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'tabla_requerida') THEN
    RAISE EXCEPTION 'Prerequisito no cumplido: tabla_requerida no existe';
  END IF;
END $$;
```

## Integración con Spring Boot

### Configuración Avanzada
```yaml
spring:
  liquibase:
    change-log: classpath:/db/changelog.yaml
    enabled: true
    drop-first: false
    default-schema: public
    liquibase-schema: liquibase  # Schema para metadatos
    database-change-log-table: databasechangelog
    database-change-log-lock-table: databasechangeloglock
    test-rollback-on-update: false
```

### Perfiles por Ambiente
```yaml
---
spring:
  config:
    activate:
      on-profile: dev
  liquibase:
    contexts: dev,data-load

---
spring:
  config:
    activate:
      on-profile: prod
  liquibase:
    contexts: prod
    drop-first: false
```

Esta estructura garantiza migraciones consistentes, trazables y mantenibles para todos los proyectos Spring Boot del repositorio.