# Introducción

 Ecosistema completo de aplicaciones financieras que demuestra las capacidades avanzadas de Azure AI Services a través de casos de uso reales del sector bancario y financiero. El proyecto incluye tres microservicios especializados (gestión de finanzas personales, evaluación crediticia y manejo de reclamos) con una interfaz web moderna, desplegados en Azure Container Apps con infraestructura como código completamente automatizada.

# Tecnologías

## Personal Finance API

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 | Runtime y lenguaje base |
| Spring Boot | 3.5.7 | Framework de microservicios |
| Spring WebFlux | - | Programación reactiva |
| Spring Data JPA | - | Persistencia de datos |
| PostgreSQL | 14+ | Base de datos relacional |
| Liquibase | - | Versionado de esquemas |
| MapStruct | 1.5.5.Final | Mapeo de objetos |
| Azure Document Intelligence SDK | 1.0.6 | Procesamiento de documentos |

## Credit Management API

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 | Runtime y lenguaje base |
| Spring Boot | 3.5.7 | Framework de microservicios |
| Spring WebFlux | - | Programación reactiva |
| Spring Data JPA | - | Persistencia de datos |
| PostgreSQL | 14+ | Base de datos relacional |
| Liquibase | - | Versionado de esquemas |
| Azure AI Search SDK | 11.6.0 | Búsqueda vectorial semántica |
| Azure OpenAI SDK | 1.0.0-beta.7 | Generación de embeddings |

## Claim Management API

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 | Runtime y lenguaje base |
| Spring Boot | 3.5.7 | Framework de microservicios |
| Spring WebFlux | - | Programación reactiva |
| Spring Data JPA | - | Persistencia de datos |
| PostgreSQL | 14+ | Base de datos relacional |
| Apache POI | 5.4.0 | Procesamiento de archivos Excel |
| Azure OpenAI SDK | 1.0.0-beta.7 | Análisis inteligente de texto |

## Showcase Website

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Angular | 20.3.0 | Framework frontend |
| TypeScript | 5.9.2 | Lenguaje tipado |
| TailwindCSS | 4.1.13 | Framework de CSS |
| RxJS | 7.8.0 | Programación reactiva |
| Chart.js | 4.5.1 | Visualización de datos |
| ngx-markdown | 20.1.0 | Renderizado de Markdown |

## Infraestructura

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Terraform | 1.5+ | Infraestructura como código |
| Azure CLI | - | Gestión de recursos Azure |
| Azure Container Apps | - | Hosting de microservicios |
| Azure Container Registry | - | Registro de imágenes Docker |
| PostgreSQL Flexible Server | - | Base de datos administrada |

# Componentes

| Componente | Descripción | Enlace |
|------------|-------------|--------|
| Personal Finance API | Microservicio para gestión de finanzas personales con procesamiento de documentos mediante Azure Document Intelligence | [personal-finance-api](./personal-finance-api) |
| Credit Management API | Microservicio para evaluación crediticia con búsqueda semántica y análisis vectorial usando Azure AI Search y OpenAI | [credit-management-api](./credit-management-api) |
| Claim Management API | Microservicio para gestión de reclamos bancarios con análisis inteligente de texto mediante Azure OpenAI | [claim-management-api](./claim-management-api) |
| Showcase Website | Aplicación web Angular 20 que proporciona interfaces de usuario para todos los microservicios | [showcase-website](./showcase-website) |
| Infrastructure | Infraestructura como código con Terraform para despliegue automatizado en Azure Container Apps | [infra](./infra) |

# Uso de Azure AI

## Azure Document Intelligence

### Descripción

Azure Document Intelligence se utiliza en el Personal Finance API para procesamiento automático de facturas en formato PDF. El servicio emplea el modelo preentrenado "prebuilt-invoice" para extraer campos estructurados como número de factura, fecha, monto total, información del proveedor y líneas de productos mediante reconocimiento óptico de caracteres avanzado. La integración permite a los usuarios subir facturas PDF y obtener automáticamente datos estructurados para crear transacciones financieras sin intervención manual.

### Consideraciones

El servicio requiere configuración de endpoint y clave de API específicos para Document Intelligence. Se debe considerar el límite de tamaño de archivo (máximo 50 MB) y los formatos soportados (PDF, JPEG, PNG, TIFF). La precisión de extracción depende de la calidad del documento original y el formato de la factura. Es recomendable implementar validación de datos extraídos para garantizar exactitud.

### Futuros Usos

Expansión para procesar otros tipos de documentos financieros como estados de cuenta bancarios, comprobantes de pago, contratos de préstamo y documentos de identidad. Implementación de modelos personalizados para formatos específicos de la institución financiera. Integración con workflows de aprobación automática basados en contenido de documentos extraídos.

## Azure OpenAI

### Descripción

Azure OpenAI se utiliza en dos contextos diferentes dentro del ecosistema. En Credit Management API, se emplea para generar embeddings vectoriales de alta calidad usando el modelo text-embedding-ada-002, permitiendo búsquedas semánticas de productos crediticios y clasificación de perfiles de clientes mediante similitud vectorial. En Claim Management API, se utilizan modelos GPT para análisis inteligente de descripciones de reclamos en lenguaje natural, generando automáticamente categorías, razones de procesamiento y clasificaciones mediante prompt engineering especializado para el dominio financiero.

### Consideraciones

La configuración requiere endpoints específicos para Azure OpenAI y claves de API dedicadas. Se debe considerar el costo por token tanto para embeddings como para completions de chat. La calidad de los prompts es crítica para obtener resultados consistentes en el análisis de reclamos. Para embeddings, es importante mantener consistencia en el modelo utilizado para garantizar comparabilidad vectorial.

### Futuros Usos

Implementación de chatbots inteligentes para atención al cliente con conocimiento especializado del dominio financiero. Generación automática de reportes de análisis de reclamos con insights y recomendaciones. Análisis de sentimiento en retroalimentación de clientes. Desarrollo de asistentes virtuales para asesoría financiera personalizada basada en perfiles de usuario y productos disponibles.

## Azure AI Search

### Descripción

Azure AI Search se implementa en Credit Management API para proporcionar capacidades avanzadas de búsqueda vectorial semántica sobre productos crediticios y rangos de clasificación. El sistema mantiene índices especializados que almacenan embeddings generados por Azure OpenAI, permitiendo búsquedas que van más allá de coincidencias exactas mediante vectores de similitud. La sincronización automática garantiza que los cambios en productos crediticios se reflejen inmediatamente en los índices de búsqueda.

### Consideraciones

Requiere configuración de servicio de Azure AI Search con tier apropiado para el volumen de datos esperado. La sincronización automática debe manejarse de manera asíncrona para no impactar el rendimiento de las APIs. Es importante configurar políticas de indexación adecuadas y monitorear el uso de unidades de búsqueda. La calidad de los resultados depende de la calidad de los embeddings generados.

### Futuros Usos

Implementación de recomendaciones de productos más sofisticadas basadas en historial de transacciones y comportamiento del cliente. Desarrollo de sistemas de matching automático para conectar clientes con productos financieros óptimos. Análisis de tendencias de mercado mediante búsquedas agregadas en datos históricos. Integración con sistemas de scoring crediticio más avanzados utilizando múltiples vectores de características.
