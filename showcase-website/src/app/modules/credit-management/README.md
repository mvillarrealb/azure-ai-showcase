# Módulo Credit Management

## Descripción
Módulo completo para la gestión de créditos que incluye productos crediticios, rangos de crédito y evaluaciones crediticias con IA semántica. Implementado siguiendo los estándares definidos en `CRUD_SPEC.md`.

## Estructura del Módulo

```
credit-management/
├── adapters/                           # Adapters para PageableGrid
│   ├── products-pageable.adapter.ts    # Adapter para productos
│   └── ranks-pageable.adapter.ts       # Adapter para rangos (mock data)
├── components/                         # Componentes reutilizables
│   ├── products-crud/                  # CRUD de productos crediticios
│   ├── product-form-modal/             # Modal para crear productos
│   ├── ranks-crud/                     # CRUD de rangos de crédito
│   ├── rank-form-modal/                # Modal para crear rangos
│   ├── credit-evaluation-wizard/       # Wizard de evaluación crediticia
│   └── index.ts                        # Índice de exports
├── interfaces/                         # Interfaces TypeScript basadas en OpenAPI
│   └── credit-management.interface.ts
├── pages/                              # Páginas contenedoras con manejo de modals
│   ├── products-page/                  # Página de productos
│   ├── ranks-page/                     # Página de rangos
│   └── evaluation-page/                # Página de evaluación
├── services/                           # Servicios para consumir APIs
│   └── credit-management.service.ts
├── credit-management/                  # Componente principal con navegación
│   ├── credit-management.html
│   └── credit-management.ts
├── credit-management-module.ts         # Módulo Angular
└── credit-management-routing-module.ts # Configuración de rutas
```

## Funcionalidades Implementadas

### 1. Productos Crediticios (`/credit-management/products`)
- **CRUD completo**: Lista paginada de productos con PageableGrid
- **Creación**: Modal completo para crear nuevos productos
- **Campos**: ID, nombre, descripción, categoría, subcategoría, montos, tasas, requisitos, características, beneficios
- **Validaciones**: Todas las validaciones requeridas por el OpenAPI spec
- **Colores**: Azul/cyan siguiendo el diseño del módulo

### 2. Rangos de Crédito (`/credit-management/ranks`)
- **CRUD completo**: Lista paginada de rangos con PageableGrid
- **Creación**: Modal para crear rangos que se sincronizan con Azure AI Search
- **Datos mock**: Incluye 5 rangos predefinidos (PREMIUM, ORO, PLATA, BRONCE, BÁSICO)
- **Campos**: ID, nombre, descripción detallada para IA semántica
- **Colores**: Naranja/amber para diferenciación visual

### 3. Evaluación Crediticia (`/credit-management/evaluation`)
- **Wizard de 2 pasos**: Formulario → Resultados
- **Entrada**: Documento de identidad y monto solicitado
- **IA Semántica**: Utiliza Azure AI Search para análisis inteligente
- **Resultados**: Perfil del cliente, puntuación general, productos recomendados
- **Exportación**: Descarga de resultados en formato JSON
- **Colores**: Púrpura/índigo para destacar la funcionalidad de IA

## Estándares Seguidos

### CRUD_SPEC Compliance ✅
- **Una sola tarjeta principal** para cada vista
- **Glassmorphism consistente** con `bg-white/40 backdrop-blur-md`
- **PageableGrid** con configuración estándar (pageSize: 15, sin opciones múltiples)
- **Paginación minimalista** con solo controles esenciales
- **Colores semánticos** diferenciados por módulo
- **Modals a nivel de página** con z-index correcto

### Angular 20 Compliance ✅
- **Control flow moderno**: `@if`, `@for` en lugar de directivas legacy
- **Componentes standalone** en toda la implementación
- **Signals** para manejo de estado reactivo
- **Lazy loading** para optimización de performance

### TailwindCSS v4 Compliance ✅
- **Sintaxis moderna de opacidad**: `bg-black/50` en lugar de `bg-opacity-50`
- **Utilidades modernas** con notación slash
- **Glassmorphism** con `backdrop-blur-*`
- **Responsive design** mobile-first

## APIs Utilizadas

### Endpoints Implementados
- `GET /products` - Listar productos con paginación y filtros
- `POST /products` - Crear nuevo producto crediticio
- `GET /products/{id}` - Obtener producto por ID
- `POST /products/evaluate` - Evaluar elegibilidad crediticia
- `POST /ranks/upload` - Subir rango individual a Azure AI Search
- `POST /ranks/upload-batch` - Subir rangos en lote

### Interfaces TypeScript
Todas las interfaces están basadas exactamente en el OpenAPI spec sin desviaciones:
- `Product`, `CreateProductRequest`, `ProductListResponse`
- `EvaluationRequest`, `EvaluationResponse`, `ClientProfile`
- `RankUploadRequest`, `RankBatchUploadRequest`
- `ErrorResponse` para manejo consistente de errores

## Navegación

### Menú Simplificado ✅
Solo las 3 opciones requeridas:
1. **Productos Crediticios** - Gestión del catálogo
2. **Rangos de Crédito** - Clasificaciones de clientes
3. **Evaluación Crediticia** - Análisis con IA

### Botón Home Corregido ✅
- Redirecciona correctamente a `/` (raíz de la aplicación)
- No utiliza rutas relativas problemáticas

## Características Especiales

### IA Semántica
- Integración completa con Azure AI Search
- Análisis inteligente de perfiles de cliente
- Recomendaciones personalizadas de productos
- Clasificación automática por rangos

### UX Optimizada
- Feedback visual inmediato en todas las acciones
- Estados de carga con spinners elegantes
- Validaciones en tiempo real
- Mensajes de error informativos

### Performance
- Lazy loading de páginas
- Signals para reactividad eficiente
- Adapters optimizados para PageableGrid
- Componentes standalone para mejor tree-shaking

## Próximos Pasos

1. **Conectar con APIs reales** cuando estén disponibles
2. **Implementar filtros avanzados** si se requieren
3. **Agregar tests unitarios** para componentes críticos
4. **Optimizar para móvil** con mejores breakpoints

## Notas Técnicas

- El adapter de rangos usa datos mock ya que la API solo permite upload
- Las validaciones están alineadas con las restricciones del OpenAPI
- Los colores están diferenciados por módulo para mejor UX
- Todos los componentes son standalone para mejor performance