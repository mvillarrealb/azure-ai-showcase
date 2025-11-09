# Introducción

Claim Management API es un microservicio REST para la gestión integral de reclamos bancarios que incorpora procesamiento inteligente mediante Azure OpenAI para el análisis automático de descripciones, categorización y generación de razones de procesamiento de reclamos importados desde archivos Excel.

# Tecnologías

- Java 21
- Spring Boot 3.5.7
- Spring WebFlux
- Spring Data JPA
- PostgreSQL
- Liquibase
- MapStruct 1.5.5.Final
- Lombok
- Apache POI 5.4.0
- Azure OpenAI SDK 1.0.0-beta.7
- Jackson ObjectMapper
- Hibernate Types 2.21.1

# Componentes Azure AI

La aplicación integra Azure OpenAI Service utilizando modelos GPT para el análisis inteligente de reclamos bancarios. El sistema procesa descripciones de reclamos en lenguaje natural y genera automáticamente categorías, razones de procesamiento y clasificaciones mediante prompt engineering especializado para el dominio financiero.

```mermaid
graph LR
    A[Excel Import] --> B[Claim Data Parser]
    B --> C[OpenAI Client]
    C --> D[GPT Model]
    D --> E[Prompt Processing]
    E --> F[JSON Response]
    F --> G[Reason Assignment]
    G --> H[PostgreSQL Database]
    
    C -.->|Chat Completions API| D
    E -.->|System Prompt + Claims| D
    F -.->|Structured Analysis| G
    G -.->|Categorized Claims| H
```

# Funcionalidades

- Creación y consulta de reclamos bancarios individuales con validación completa
- Importación masiva de reclamos desde archivos Excel con procesamiento por lotes
- Análisis automático de descripciones mediante Azure OpenAI para categorización inteligente
- Generación automática de razones de procesamiento basadas en análisis textual
- Gestión de estados del ciclo de vida de reclamos (creación, procesamiento, resolución)
- Validación de datos de importación con reportes detallados de errores
- Procesamiento reactivo no bloqueante para optimización de rendimiento

Para detalles específicos de los endpoints y esquemas de datos, consultar la especificación OpenAPI en [specs/claim-management.yaml](../specs/claim-management.yaml).

# Ejecutar Localmente

## Requisitos Previos
- Java 21
- PostgreSQL 14+
- Azure OpenAI Service con modelo GPT desplegado

## Configuración
```bash
# Variables de entorno requeridas
export OPENAI_ENDPOINT=your_azure_openai_endpoint
export OPENAI_API_KEY=your_api_key
export OPENAI_DEPLOYMENT_NAME=your_deployment_name
export OPENAI_SYSTEM_PROMPT="your_specialized_prompt"
export DB_HOST=localhost
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

## Ejecución
```bash
cd claim-management-api
./gradlew bootRun
```

La aplicación estará disponible en http://localhost:8080

## Colección Postman

Para facilitar las pruebas de la API, se proporciona una colección de Postman con todos los endpoints configurados en [postman/claim-management.postman_collection.json](../postman/claim-management.postman_collection.json).

# Referencias

- [Azure OpenAI Service Documentation](https://docs.microsoft.com/en-us/azure/cognitive-services/openai/)
- [Azure OpenAI REST API Reference](https://docs.microsoft.com/en-us/azure/cognitive-services/openai/reference)
- [Apache POI Documentation](https://poi.apache.org/components/spreadsheet/)
- [Spring Boot WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Reactor Core Reference](https://projectreactor.io/docs/core/release/reference/)