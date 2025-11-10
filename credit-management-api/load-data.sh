#!/bin/bash

# =============================================
# Script de carga de datos para Credit Management API
# Crea ranks y productos usando los endpoints REST
# Author: Marco Villarreal
# =============================================

# Configuración del servidor
BASE_URL="http://localhost:8082"
RANKS_ENDPOINT="$BASE_URL/ranks"
PRODUCTS_ENDPOINT="$BASE_URL/products"

# Archivos de datos
RANKS_FILE="ranks-data.json"
PRODUCTS_FILE="products-api-compatible.json"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir mensajes con colores
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Función para verificar que los archivos existen
check_files() {
    if [ ! -f "$RANKS_FILE" ]; then
        print_error "Archivo $RANKS_FILE no encontrado"
        exit 1
    fi
    
    if [ ! -f "$PRODUCTS_FILE" ]; then
        print_error "Archivo $PRODUCTS_FILE no encontrado"
        exit 1
    fi
    
    print_info "Archivos de datos encontrados ✓"
}

# Función para verificar conectividad con el servidor
check_server() {
    print_info "Verificando conectividad con $BASE_URL..."
    
    if curl -s --connect-timeout 5 "$BASE_URL/actuator/health" > /dev/null 2>&1; then
        print_success "Servidor disponible ✓"
    else
        print_error "No se puede conectar al servidor en $BASE_URL"
        print_info "Asegúrate de que el servidor esté ejecutándose"
        exit 1
    fi
}

# Función para crear ranks
create_ranks() {
    print_info "Iniciando creación de ranks..."
    
    # Extraer ranks del archivo JSON
    ranks_count=$(jq '.ranks | length' "$RANKS_FILE")
    print_info "Se crearán $ranks_count ranks"
    
    success_count=0
    error_count=0
    
    for i in $(seq 0 $((ranks_count-1))); do
        # Extraer rank individual
        rank=$(jq ".ranks[$i]" "$RANKS_FILE")
        rank_id=$(echo "$rank" | jq -r '.id')
        rank_name=$(echo "$rank" | jq -r '.name')
        
        print_info "Creando rank: $rank_id ($rank_name)"
        
        # Hacer POST request
        response=$(curl -s -w "\n%{http_code}" \
            -H "Content-Type: application/json" \
            -H "Accept: application/json" \
            -d "$rank" \
            "$RANKS_ENDPOINT")
        
        # Separar body y status code
        body=$(echo "$response" | head -n -1)
        status_code=$(echo "$response" | tail -n 1)
        
        if [ "$status_code" = "201" ]; then
            print_success "Rank $rank_id creado exitosamente"
            ((success_count++))
        elif [ "$status_code" = "409" ]; then
            print_warning "Rank $rank_id ya existe (conflicto)"
            ((success_count++))
        else
            print_error "Error al crear rank $rank_id (HTTP $status_code)"
            echo "$body" | jq '.' 2>/dev/null || echo "$body"
            ((error_count++))
        fi
        
        # Pequeña pausa entre requests
        sleep 0.5
    done
    
    print_info "Resumen creación de ranks: $success_count exitosos, $error_count errores"
    echo ""
}

# Función para crear productos
create_products() {
    print_info "Iniciando creación de productos..."
    
    # Extraer productos del archivo JSON
    products_count=$(jq '.products | length' "$PRODUCTS_FILE")
    print_info "Se crearán $products_count productos"
    
    success_count=0
    error_count=0
    
    for i in $(seq 0 $((products_count-1))); do
        # Extraer producto individual
        product=$(jq ".products[$i]" "$PRODUCTS_FILE")
        product_id=$(echo "$product" | jq -r '.id')
        product_name=$(echo "$product" | jq -r '.name')
        
        print_info "Creando producto: $product_id ($product_name)"
        
        # Hacer POST request
        response=$(curl -s -w "\n%{http_code}" \
            -H "Content-Type: application/json" \
            -H "Accept: application/json" \
            -d "$product" \
            "$PRODUCTS_ENDPOINT")
        
        # Separar body y status code
        body=$(echo "$response" | head -n -1)
        status_code=$(echo "$response" | tail -n 1)
        
        if [ "$status_code" = "201" ]; then
            print_success "Producto $product_id creado exitosamente"
            ((success_count++))
        elif [ "$status_code" = "409" ]; then
            print_warning "Producto $product_id ya existe (conflicto)"
            ((success_count++))
        else
            print_error "Error al crear producto $product_id (HTTP $status_code)"
            echo "$body" | jq '.' 2>/dev/null || echo "$body"
            ((error_count++))
        fi
        
        # Pequeña pausa entre requests
        sleep 0.5
    done
    
    print_info "Resumen creación de productos: $success_count exitosos, $error_count errores"
    echo ""
}

# Función para mostrar ayuda
show_help() {
    echo "Uso: $0 [opciones]"
    echo ""
    echo "Opciones:"
    echo "  -u, --url URL          URL base del servidor (default: http://localhost:8082)"
    echo "  -r, --ranks-only       Solo crear ranks"
    echo "  -p, --products-only    Solo crear productos"
    echo "  -h, --help            Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0                                    # Crear ranks y productos en localhost:8082"
    echo "  $0 -u http://localhost:8080          # Usar puerto diferente"
    echo "  $0 --ranks-only                      # Solo crear ranks"
    echo "  $0 --products-only                   # Solo crear productos"
    echo ""
}

# Procesar argumentos de línea de comandos
RANKS_ONLY=false
PRODUCTS_ONLY=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -u|--url)
            BASE_URL="$2"
            RANKS_ENDPOINT="$BASE_URL/ranks"
            PRODUCTS_ENDPOINT="$BASE_URL/products"
            shift 2
            ;;
        -r|--ranks-only)
            RANKS_ONLY=true
            shift
            ;;
        -p|--products-only)
            PRODUCTS_ONLY=true
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            print_error "Opción desconocida: $1"
            show_help
            exit 1
            ;;
    esac
done

# Verificar que jq esté instalado
if ! command -v jq &> /dev/null; then
    print_error "jq no está instalado. Por favor instala jq para continuar:"
    echo "  macOS: brew install jq"
    echo "  Ubuntu/Debian: sudo apt-get install jq"
    echo "  CentOS/RHEL: sudo yum install jq"
    exit 1
fi

# Verificar que curl esté disponible
if ! command -v curl &> /dev/null; then
    print_error "curl no está instalado"
    exit 1
fi

# Imprimir configuración
echo "============================================="
echo "Credit Management API - Carga de Datos"
echo "============================================="
print_info "URL del servidor: $BASE_URL"
print_info "Endpoint de ranks: $RANKS_ENDPOINT"
print_info "Endpoint de productos: $PRODUCTS_ENDPOINT"
echo ""

# Verificaciones previas
check_files
check_server

# Ejecutar según las opciones
if [ "$RANKS_ONLY" = true ]; then
    create_ranks
elif [ "$PRODUCTS_ONLY" = true ]; then
    create_products
else
    # Crear ambos (orden importante: ranks primero)
    create_ranks
    create_products
fi

print_success "Proceso completado ✓"
echo ""
print_info "Para verificar los datos creados puedes usar:"
echo "  curl $BASE_URL/ranks"
echo "  curl $BASE_URL/products"