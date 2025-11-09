#!/bin/bash

# =============================================
# Script para crear índices en Azure AI Search
# Author: Marco Villarreal
# =============================================

# Verificar que las variables de entorno estén cargadas
if [ -z "$AI_SEARCH_ENDPOINT" ] || [ -z "$AI_SEARCH_KEY" ]; then
    echo "❌ Error: Variables de entorno no encontradas"
    echo "💡 Ejecuta primero: export \$(cat main.env | xargs)"
    exit 1
fi

echo "🚀 Creando índices en Azure AI Search"
echo "======================================"
echo "📍 Endpoint: $AI_SEARCH_ENDPOINT"
echo "🔑 Key: ${AI_SEARCH_KEY:0:10}..."
echo ""
echo "📝 Índices a crear:"
echo "   - products: Para productos crediticios con embeddings"
echo "   - ranks: Para rangos de clientes con embeddings"
echo ""

# =============================================
# Crear índice "products"
# =============================================
echo "📦 Creando índice 'products'..."

PRODUCTS_INDEX_DEFINITION='{
  "name": "products",
  "fields": [
    {
      "name": "id",
      "type": "Edm.String",
      "key": true,
      "retrievable": true,
      "searchable": false,
      "filterable": false,
      "sortable": false,
      "facetable": false
    },
    {
      "name": "name",
      "type": "Edm.String",
      "retrievable": true,
      "searchable": true,
      "filterable": false,
      "sortable": true,
      "facetable": false
    },
    {
      "name": "description",
      "type": "Edm.String",
      "retrievable": true,
      "searchable": true,
      "filterable": false,
      "sortable": false,
      "facetable": false
    },
    {
      "name": "category",
      "type": "Edm.String",
      "retrievable": true,
      "searchable": false,
      "filterable": true,
      "sortable": true,
      "facetable": true
    },
    {
      "name": "subcategory",
      "type": "Edm.String",
      "retrievable": true,
      "searchable": false,
      "filterable": true,
      "sortable": true,
      "facetable": true
    },
    {
      "name": "minimumAmount",
      "type": "Edm.Double",
      "retrievable": true,
      "searchable": false,
      "filterable": true,
      "sortable": true,
      "facetable": false
    },
    {
      "name": "maximumAmount",
      "type": "Edm.Double",
      "retrievable": true,
      "searchable": false,
      "filterable": true,
      "sortable": true,
      "facetable": false
    },
    {
      "name": "currency",
      "type": "Edm.String",
      "retrievable": true,
      "searchable": false,
      "filterable": true,
      "sortable": false,
      "facetable": true
    },
    {
      "name": "term",
      "type": "Edm.String",
      "retrievable": true,
      "searchable": false,
      "filterable": true,
      "sortable": false,
      "facetable": true
    },
    {
      "name": "minimumRate",
      "type": "Edm.Double",
      "retrievable": true,
      "searchable": false,
      "filterable": true,
      "sortable": true,
      "facetable": false
    },
    {
      "name": "maximumRate",
      "type": "Edm.Double",
      "retrievable": true,
      "searchable": false,
      "filterable": true,
      "sortable": true,
      "facetable": false
    },
    {
      "name": "requirements",
      "type": "Collection(Edm.String)",
      "retrievable": true,
      "searchable": true,
      "filterable": false,
      "sortable": false,
      "facetable": false
    },
    {
      "name": "features",
      "type": "Collection(Edm.String)",
      "retrievable": true,
      "searchable": true,
      "filterable": false,
      "sortable": false,
      "facetable": false
    },
    {
      "name": "benefits",
      "type": "Collection(Edm.String)",
      "retrievable": true,
      "searchable": true,
      "filterable": false,
      "sortable": false,
      "facetable": false
    },
    {
      "name": "active",
      "type": "Edm.Boolean",
      "retrievable": true,
      "searchable": false,
      "filterable": true,
      "sortable": false,
      "facetable": true
    },
    {
      "name": "allowedRanks",
      "type": "Collection(Edm.String)",
      "retrievable": true,
      "searchable": true,
      "filterable": true,
      "sortable": false,
      "facetable": true
    },
    {
      "name": "embedding",
      "type": "Collection(Edm.Single)",
      "retrievable": false,
      "searchable": true,
      "filterable": false,
      "sortable": false,
      "facetable": false,
      "dimensions": 1536,
      "vectorSearchProfile": "vector-config"
    }
  ],
  "vectorSearch": {
    "algorithms": [
      {
        "name": "vector-algorithm",
        "kind": "hnsw",
        "hnswParameters": {
          "m": 4,
          "efConstruction": 400,
          "efSearch": 500,
          "metric": "cosine"
        }
      }
    ],
    "profiles": [
      {
        "name": "vector-config",
        "algorithm": "vector-algorithm"
      }
    ]
  }
}'

response=$(curl -s -w "\n%{http_code}" \
    -X POST \
    -H "Content-Type: application/json" \
    -H "api-key: $AI_SEARCH_KEY" \
    -d "$PRODUCTS_INDEX_DEFINITION" \
    "$AI_SEARCH_ENDPOINT/indexes?api-version=2023-11-01")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 201 ]; then
    echo "✅ Índice 'products' creado exitosamente (HTTP: $http_code)"
elif [ "$http_code" -eq 409 ]; then
    echo "ℹ️  Índice 'products' ya existe (HTTP: $http_code)"
else
    echo "❌ Error creando índice 'products' (HTTP: $http_code)"
    echo "   Respuesta: $body"
fi

echo ""

# =============================================
# Crear índice "ranks"
# =============================================
echo "🏆 Creando índice 'ranks'..."

RANKS_INDEX_DEFINITION='{
  "name": "ranks",
  "fields": [
    {
      "name": "id",
      "type": "Edm.String",
      "key": true,
      "retrievable": true,
      "searchable": false,
      "filterable": false,
      "sortable": false,
      "facetable": false
    },
    {
      "name": "name",
      "type": "Edm.String",
      "retrievable": true,
      "searchable": true,
      "filterable": true,
      "sortable": true,
      "facetable": true
    },
    {
      "name": "description",
      "type": "Edm.String",
      "retrievable": true,
      "searchable": true,
      "filterable": false,
      "sortable": false,
      "facetable": false
    },
    {
      "name": "embedding",
      "type": "Collection(Edm.Single)",
      "retrievable": false,
      "searchable": true,
      "filterable": false,
      "sortable": false,
      "facetable": false,
      "dimensions": 1536,
      "vectorSearchProfile": "vector-config"
    }
  ],
  "vectorSearch": {
    "algorithms": [
      {
        "name": "vector-algorithm",
        "kind": "hnsw",
        "hnswParameters": {
          "m": 4,
          "efConstruction": 400,
          "efSearch": 500,
          "metric": "cosine"
        }
      }
    ],
    "profiles": [
      {
        "name": "vector-config",
        "algorithm": "vector-algorithm"
      }
    ]
  }
}'

ranks_response=$(curl -s -w "\n%{http_code}" \
    -X POST \
    -H "Content-Type: application/json" \
    -H "api-key: $AI_SEARCH_KEY" \
    -d "$RANKS_INDEX_DEFINITION" \
    "$AI_SEARCH_ENDPOINT/indexes?api-version=2023-11-01")

ranks_http_code=$(echo "$ranks_response" | tail -n1)
ranks_body=$(echo "$ranks_response" | sed '$d')

if [ "$ranks_http_code" -eq 201 ]; then
    echo "✅ Índice 'ranks' creado exitosamente (HTTP: $ranks_http_code)"
elif [ "$ranks_http_code" -eq 409 ]; then
    echo "ℹ️  Índice 'ranks' ya existe (HTTP: $ranks_http_code)"
else
    echo "❌ Error creando índice 'ranks' (HTTP: $ranks_http_code)"
    echo "   Respuesta: $ranks_body"
fi

echo ""
echo "🎯 Resumen de Índices:"
echo "====================="
echo "✅ products: Listo para usar"
echo "✅ ranks: Listo para usar"
echo ""
echo "💡 Ahora puedes reiniciar la aplicación y probar:"
echo "   cd /Users/marcovillarreal/workspaces/GENIA_TON_IFS/credit-management-api"
echo "   export \$(cat main.env | xargs) && ./gradlew bootRun"
echo ""
echo "📦 Y luego probar la sincronización con:"
echo "   ./test-ai-search-sync.sh"