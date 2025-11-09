# Credit Management API - Azure AI Search Integration

## 🚀 Funcionalidades Implementadas

### ✅ Sincronización Automática con Azure AI Search
- **ProductSyncListener**: Listener JPA que sincroniza automáticamente productos con Azure AI Search al crear nuevos productos
- **Generación de Embeddings**: Usando OpenAI para embeddings semánticos de productos
- **Procesamiento Asíncrono**: Sincronización no bloqueante con thread pool dedicado
- **Solo INSERT**: Implementado únicamente para nuevos productos (no update/delete)

## 📁 Estructura del Proyecto

```
credit-management-api/
├── src/main/java/
│   └── com/geniatonifs/creditmanagement/
│       ├── config/
│       │   └── AsyncConfig.java           # Configuración async para AI Search
│       ├── document/
│       │   ├── ProductDocument.java       # Entidad para Azure AI Search
│       │   └── ProductDocumentMapper.java # Mapper con embeddings OpenAI
│       ├── entity/
│       │   └── CreditProduct.java         # Entidad JPA
│       ├── listener/
│       │   └── ProductSyncListener.java   # ✨ SYNC AUTOMÁTICO AI SEARCH
│       └── search/
│           └── ProductSearchClient.java   # Cliente Azure AI Search
├── scripts/
│   ├── create-products.sh                 # Script para crear productos via API
│   └── test-ai-search-sync.sh             # Script de prueba de sincronización
└── src/main/resources/
    └── db/changelog/
        └── data.sql                       # Migración de datos actualizada
```

## 🔧 Configuración

### Variables de Entorno
```bash
# Azure AI Search
AZURE_SEARCH_ENDPOINT=https://tu-search-service.search.windows.net
AZURE_SEARCH_KEY=tu-api-key

# OpenAI para embeddings
OPENAI_API_KEY=tu-openai-key
```

### Thread Pool Asíncrono
- **Executor**: `aiSearchSyncExecutor`
- **Core Threads**: 5
- **Max Threads**: 10
- **Queue Capacity**: 100

## 🎯 Uso

### 1. Crear Productos (Sincronización Automática)
```bash
# Crear todos los productos del banco
./create-products.sh

# Probar sincronización con producto de prueba  
./test-ai-search-sync.sh
```

### 2. Verificar Sincronización
Los logs mostrarán:
```
🚀 Product inserted, starting AI Search indexing for productId: XXXX
✅ Product XXXX successfully indexed in AI Search with index result: IndexingResult{key='XXXX', isSucceeded=true}
```

### 3. API Endpoints
```bash
# Crear producto (sincroniza automáticamente)
POST /products
Content-Type: application/json

{
  "id": "CREDITO-PERSONAL-001",
  "name": "Crédito Personal Rápido",
  "description": "Crédito personal...",
  // ... resto de campos
}
```

## 🔍 Flujo de Sincronización

1. **Crear Producto** → API POST `/products`
2. **JPA Insert** → Guardar en PostgreSQL 
3. **ProductSyncListener** → `@PostPersist` activado automáticamente
4. **Conversión** → `ProductDocumentMapper.toProductDocument()` 
5. **Embeddings** → OpenAI genera vectores semánticos
6. **Indexación** → `ProductSearchClient.uploadDocuments()` a Azure AI Search
7. **Logs** → Confirmación con emojis 🚀 ✅

## ⚠️ Importante

- **Solo INSERT**: La sincronización solo ocurre al crear productos nuevos
- **Asíncrono**: No bloquea la respuesta de la API
- **Embeddings**: Se generan automáticamente para búsquedas semánticas
- **Error Handling**: Fallos en AI Search no afectan la creación del producto

## 🧪 Testing

```bash
# Probar creación de productos
./create-products.sh

# Probar sincronización específica
./test-ai-search-sync.sh

# Verificar logs para confirmación
tail -f logs/application.log | grep -E "🚀|✅"
```

## 📊 Monitoreo

### Logs Importantes
- `🚀 Product inserted` - Inicio de sincronización
- `✅ Product successfully indexed` - Sincronización exitosa  
- `❌ Error indexing product` - Error en sincronización

### Métricas
- **Thread Pool**: Monitorear `aiSearchSyncExecutor`
- **Azure AI Search**: Verificar índice de productos
- **OpenAI**: Monitorear uso de API para embeddings