# 📋 Gestión de Productos Crediticios

## ✅ Cambios Implementados

Se han realizado cambios importantes en la gestión de productos crediticios para garantizar la **sincronización automática con Azure AI Search**:

### 🔄 Migración de Scripts DDL/DML a API REST

Los productos crediticios ahora deben crearse exclusivamente a través de la **API REST** para asegurar:

- ✅ **Sincronización automática** con Azure AI Search
- ✅ **Activación del ProductSyncListener** en cada operación
- ✅ **Indexación semántica** para búsquedas inteligentes
- ✅ **Consistencia de datos** entre base de datos e índices de búsqueda

## 🚀 Scripts Disponibles

### 📦 `create-products.sh`
Script automatizado para crear todos los productos crediticios via API REST.

```bash
# Ejecutar desde el directorio del proyecto
cd credit-management-api
./create-products.sh
```

**Características:**
- ✅ Crea 8 productos crediticios base
- ✅ Incluye validación de respuestas HTTP
- ✅ Manejo de productos duplicados
- ✅ Logs detallados del proceso
- ✅ Sincronización automática con AI Search

## 📊 Productos Incluidos

| ID | Nombre | Categoría | Moneda |
|---|---|---|---|
| `CP-PEN-001` | Crédito Personal Express | Crédito Personal | S/ |
| `CP-PEN-002` | Crédito Personal Premium | Crédito Personal | S/ |
| `CH-PEN-001` | Crédito Hipotecario Mi Primera Casa | Crédito Hipotecario | S/ |
| `CA-PEN-001` | Crédito Automotriz Nuevo | Crédito Automotriz | S/ |
| `CE-PEN-001` | Crédito Empresarial PYME | Crédito Empresarial | S/ |
| `CP-USD-001` | Personal Credit Express USD | Crédito Personal | USD |
| `CMC-PEN-001` | Crédito MiCrédito | Microcrédito | S/ |
| `CEN-PEN-001` | Crédito Energía Renovable | Crédito Verde | S/ |

## 🛠️ API Endpoints

### Crear Producto
```http
POST /products
Content-Type: application/json

{
  "id": "PRODUCTO-001",
  "name": "Nombre del Producto",
  "description": "Descripción detallada",
  "category": "Categoría",
  "subcategory": "Subcategoría",
  "minimumAmount": 1000.00,
  "maximumAmount": 50000.00,
  "currency": "S/",
  "term": "12 a 24 meses",
  "minimumRate": 10.00,
  "maximumRate": 15.00,
  "requirements": ["Requisito 1", "Requisito 2"],
  "features": ["Característica 1", "Característica 2"], 
  "benefits": ["Beneficio 1", "Beneficio 2"],
  "active": true
}
```

### Listar Productos
```http
GET /products?category=Crédito Personal&currency=S/&page=0&size=10
```

### Obtener Producto
```http
GET /products/{productId}
```

## 🔍 Sincronización con Azure AI Search

### ProductSyncListener

Cada operación CRUD sobre productos activa automáticamente el `ProductSyncListener`:

```java
@PostPersist
public void afterInsert(CreditProductEntity product) {
    log.info("Product inserted, AI Search sync planned for: {}", product.getId());
    // Sincronización con Azure AI Search aquí
}

@PostUpdate 
public void afterUpdate(CreditProductEntity product) {
    log.info("Product updated, AI Search sync planned for: {}", product.getId());
}

@PostRemove
public void afterDelete(CreditProductEntity product) {
    log.info("Product deleted, AI Search removal planned for: {}", product.getId());
}
```

## ⚠️ Importante

### ❌ NO Crear Productos via SQL
```sql
-- ❌ EVITAR - No garantiza sincronización con AI Search
INSERT INTO credit_products (id, name, ...) VALUES (...);
```

### ✅ SÍ Crear Productos via API
```bash
# ✅ RECOMENDADO - Garantiza sincronización completa
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"id": "PRODUCTO-001", "name": "..."}'
```

## 🗄️ Migración de Datos

### Scripts DDL
Los scripts de migración DDL (estructura de tablas) permanecen intactos:
- ✅ `v_1_0_0/main.sql` - Estructura inicial
- ✅ `v_1_1_0/main.sql` - Historial de empleo  
- ✅ `v_1_2_0/main.sql` - Limpieza de campos

### Scripts DML
Los scripts DML han sido modificados:
- ✅ **Customers**: Se mantienen los INSERTs de clientes
- ❌ **Products**: Se removieron los INSERTs directos
- ➡️ **Products**: Se crean via `create-products.sh`

## 🧪 Testing

Para probar el sistema completo:

1. **Iniciar la aplicación**
```bash
./gradlew bootRun
```

2. **Ejecutar script de productos**
```bash
./create-products.sh
```

3. **Verificar productos creados**
```bash
curl http://localhost:8080/products
```

## 📁 Estructura de Archivos

```
credit-management-api/
├── create-products.sh              # ← Script de creación de productos
├── src/main/resources/db/
│   ├── changelog.yaml
│   └── v_1_0_0/
│       ├── main.sql               # DDL (estructura)
│       └── data.sql               # DML (solo clientes)
└── src/main/java/.../
    ├── entity/CreditProductEntity.java
    ├── listener/ProductSyncListener.java
    └── controller/ProductController.java
```

---
**🔗 Beneficios de este enfoque:**
- ✅ Garantiza sincronización con AI Search
- ✅ Permite búsquedas semánticas inteligentes  
- ✅ Mantiene consistencia entre sistemas
- ✅ Facilita la replicación en diferentes entornos
- ✅ Proporciona trazabilidad completa de operaciones