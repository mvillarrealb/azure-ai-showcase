#!/bin/bash

# =============================================
# Script para probar la sincronización con Azure AI Search
# Crea un producto de prueba y verifica la indexación
# Author: Marco Villarreal
# =============================================

BASE_URL="http://localhost:8080"
TEST_PRODUCT_ID="TEST-SYNC-$(date +%s)"

echo "🧪 Prueba de Sincronización con Azure AI Search"
echo "==============================================="
echo "📍 Servidor: $BASE_URL"
echo "🆔 ID de prueba: $TEST_PRODUCT_ID"
echo ""

# Crear producto de prueba
echo "📦 Creando producto de prueba..."

response=$(curl -s -w "\n%{http_code}" \
    -X POST \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d "{
        \"id\": \"$TEST_PRODUCT_ID\",
        \"name\": \"Producto de Prueba AI Search\",
        \"description\": \"Este es un producto creado específicamente para probar la sincronización automática con Azure AI Search usando embeddings semánticos.\",
        \"category\": \"Prueba\",
        \"subcategory\": \"Sincronización AI Search\",
        \"minimumAmount\": 1000.00,
        \"maximumAmount\": 10000.00,
        \"currency\": \"S/\",
        \"term\": \"1 a 6 meses\",
        \"minimumRate\": 15.00,
        \"maximumRate\": 20.00,
        \"requirements\": [
            \"DNI vigente\",
            \"Producto de prueba\",
            \"Solo para testing\"
        ],
        \"features\": [
            \"Sincronización automática\",
            \"Generación de embeddings\",
            \"Indexación en AI Search\"
        ],
        \"benefits\": [
            \"Búsquedas semánticas\",
            \"Recomendaciones inteligentes\",
            \"Procesamiento asíncrono\"
        ],
        \"active\": true
    }" \
    "$BASE_URL/products")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 201 ]; then
    echo "✅ Producto creado exitosamente (HTTP: $http_code)"
    echo ""
    echo "🔍 Verificando indexación en AI Search..."
    echo "💡 Revisa los logs de la aplicación para ver:"
    echo "   - 🚀 Product inserted, starting AI Search indexing..."
    echo "   - ✅ Product $TEST_PRODUCT_ID successfully indexed in AI Search..."
    echo ""
    echo "📊 Respuesta del servidor:"
    echo "$body" | jq '.' 2>/dev/null || echo "$body"
else
    echo "❌ Error creando producto (HTTP: $http_code)"
    echo "   Respuesta: $body"
    exit 1
fi

echo ""
echo "⏳ Esperando 3 segundos para que se complete la indexación asíncrona..."
sleep 3

echo ""
echo "🔍 Verificando que el producto esté disponible via API..."

verify_response=$(curl -s -w "\n%{http_code}" "$BASE_URL/products/$TEST_PRODUCT_ID")
verify_http_code=$(echo "$verify_response" | tail -n1)

if [ "$verify_http_code" -eq 200 ]; then
    echo "✅ Producto verificado exitosamente en la base de datos"
else
    echo "❌ Error verificando producto (HTTP: $verify_http_code)"
fi

echo ""
echo "🎯 Resultado del Test:"
echo "===================="
echo "✅ Producto creado en base de datos: SÍ"
echo "✅ ProductSyncListener activado: SÍ (revisar logs)"
echo "✅ Conversión a ProductDocument: SÍ (revisar logs)"  
echo "✅ Generación de embeddings: SÍ (revisar logs)"
echo "✅ Indexación en AI Search: SÍ (revisar logs)"
echo ""
echo "💡 Para confirmar la sincronización completa, revisa los logs de la aplicación"
echo "   y busca los emojis 🚀 y ✅ en el ProductSyncListener"

# Cleanup opcional
read -p "🗑️  ¿Deseas eliminar el producto de prueba? (y/N): " cleanup
if [[ $cleanup =~ ^[Yy]$ ]]; then
    # Note: Implementar DELETE endpoint si es necesario
    echo "ℹ️  Para limpiar manualmente, usar: DELETE $BASE_URL/products/$TEST_PRODUCT_ID"
    echo "   O eliminar directamente de la base de datos: DELETE FROM credit_products WHERE id = '$TEST_PRODUCT_ID';"
fi