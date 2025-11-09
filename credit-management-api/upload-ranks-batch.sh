#!/bin/bash

# =============================================
# Script para cargar Ranks a Azure AI Search (Batch)
# Carga todas las tipificaciones en una sola operación
# Author: Marco Villarreal
# =============================================

BASE_URL="http://localhost:8080"
RANKS_FILE="ranks-data.json"

echo "🏆 Carga Masiva de Ranks a Azure AI Search"
echo "=========================================="
echo "📍 Servidor: $BASE_URL"
echo "📁 Archivo: $RANKS_FILE"
echo ""

# Verificar que el archivo existe
if [ ! -f "$RANKS_FILE" ]; then
    echo "❌ Error: No se encontró el archivo $RANKS_FILE"
    exit 1
fi

# Verificar que el servidor esté disponible
echo "🔍 Verificando conectividad con el servidor..."
health_response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health" 2>/dev/null)

if [ "$health_response" != "200" ]; then
    echo "❌ Error: El servidor no está disponible en $BASE_URL"
    echo "   Asegúrate de que la aplicación esté ejecutándose"
    exit 1
fi

echo "✅ Servidor disponible"
echo ""

# Mostrar preview de los ranks a cargar
echo "📋 Ranks a cargar:"
jq -r '.ranks[] | "   • \(.name) (\(.id)) - \(.description | .[0:80])..."' "$RANKS_FILE"
echo ""

# Confirmar carga
read -p "¿Proceder con la carga de todos los ranks? (y/N): " confirm
if [[ ! $confirm =~ ^[Yy]$ ]]; then
    echo "❌ Carga cancelada por el usuario"
    exit 0
fi

echo ""
echo "🚀 Iniciando carga masiva de ranks..."

# Realizar la carga en batch
response=$(curl -s -w "\n%{http_code}" \
    -X POST \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d "@$RANKS_FILE" \
    "$BASE_URL/ranks/upload-batch")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

echo ""
if [ "$http_code" -eq 201 ] || [ "$http_code" -eq 200 ]; then
    echo "✅ Carga masiva completada exitosamente (HTTP: $http_code)"
    echo ""
    echo "📊 Resultado de la carga:"
    echo "$body" | jq '.' 2>/dev/null || echo "$body"
    
    # Extraer estadísticas del resultado
    total_ranks=$(echo "$body" | jq -r '.totalRanks // "N/A"' 2>/dev/null)
    successful_uploads=$(echo "$body" | jq -r '.successfulUploads // "N/A"' 2>/dev/null)
    failed_uploads=$(echo "$body" | jq -r '.failedUploads // "N/A"' 2>/dev/null)
    
    echo ""
    echo "🎯 Resumen:"
    echo "   📈 Total de ranks: $total_ranks"
    echo "   ✅ Cargados exitosamente: $successful_uploads" 
    echo "   ❌ Fallidos: $failed_uploads"
    echo ""
    echo "🎉 ¡Ranks disponibles para clasificación semántica!"
    echo "🔍 Tipificaciones activas: BASE, BRONCE, PLATA, ORO, PLATINO, PREMIUM, EXCLUSIVE, BUSINESS"
    echo ""
    echo "💡 Uso en evaluaciones:"
    echo "   Las evaluaciones de crédito ahora clasificarán automáticamente"
    echo "   a los clientes usando estos ranks basados en su perfil semántico."
    echo ""
    echo "🧪 Para probar:"
    echo "   POST $BASE_URL/evaluation/evaluate"
    echo "   (La clasificación se hace automáticamente durante la evaluación)"
    
else
    echo "❌ Error en la carga masiva (HTTP: $http_code)"
    echo "📄 Respuesta del servidor:"
    echo "$body"
    exit 1
fi