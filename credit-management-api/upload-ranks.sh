#!/bin/bash

# =============================================
# Script para cargar Ranks a Azure AI Search
# Carga las 8 tipificaciones de clientes directamente
# Author: Marco Villarreal
# =============================================

BASE_URL="http://localhost:8080"
RANKS_FILE="ranks-data.json"

echo "🏆 Carga de Ranks a Azure AI Search"
echo "==================================="
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

# Función para cargar un rank individual
load_rank() {
    local rank_data="$1"
    local rank_id=$(echo "$rank_data" | jq -r '.id')
    local rank_name=$(echo "$rank_data" | jq -r '.name')
    
    echo "📤 Cargando rank: $rank_name ($rank_id)..."
    
    response=$(curl -s -w "\n%{http_code}" \
        -X POST \
        -H "Content-Type: application/json" \
        -H "Accept: application/json" \
        -d "$rank_data" \
        "$BASE_URL/ranks/upload")
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -eq 201 ] || [ "$http_code" -eq 200 ]; then
        echo "   ✅ Rank $rank_name cargado exitosamente (HTTP: $http_code)"
    else
        echo "   ❌ Error cargando rank $rank_name (HTTP: $http_code)"
        echo "   📄 Respuesta: $body"
        return 1
    fi
    
    return 0
}

echo "🚀 Iniciando carga de ranks..."
echo ""

# Contadores
total_ranks=0
successful_ranks=0
failed_ranks=0

# Procesar cada rank del archivo JSON
while IFS= read -r rank_data; do
    if [ -n "$rank_data" ] && [ "$rank_data" != "null" ]; then
        total_ranks=$((total_ranks + 1))
        
        if load_rank "$rank_data"; then
            successful_ranks=$((successful_ranks + 1))
        else
            failed_ranks=$((failed_ranks + 1))
        fi
        
        # Pequeña pausa entre requests
        sleep 1
    fi
done < <(jq -c '.ranks[]' "$RANKS_FILE")

echo ""
echo "📊 Resumen de Carga"
echo "=================="
echo "📈 Total de ranks: $total_ranks"
echo "✅ Cargados exitosamente: $successful_ranks"
echo "❌ Fallidos: $failed_ranks"

if [ $failed_ranks -eq 0 ]; then
    echo ""
    echo "🎉 ¡Todos los ranks fueron cargados exitosamente!"
    echo "🔍 Los ranks están ahora disponibles para clasificación semántica"
    echo "🎯 Tipificaciones disponibles: BASE, BRONCE, PLATA, ORO, PLATINO, PREMIUM, EXCLUSIVE, BUSINESS"
    echo ""
    echo "💡 Para probar la clasificación:"
    echo "   POST $BASE_URL/evaluation/evaluate"
    echo "   (La clasificación de ranks se hace automáticamente basada en el perfil del cliente)"
else
    echo ""
    echo "⚠️  Algunos ranks no pudieron ser cargados"
    echo "   Revisa los logs de la aplicación para más detalles"
    exit 1
fi