#!/bin/bash

# =============================================
# Script para crear productos crediticios via API REST
# Garantiza sincronización automática con Azure AI Search
# Author: Marco Villarreal
# =============================================

# Configuración del servidor
BASE_URL="http://localhost:8080"
PRODUCTS_ENDPOINT="/products"

echo "🚀 Iniciando creación de productos crediticios via API..."
echo "📍 Servidor: $BASE_URL"
echo "⚡ Endpoint: $PRODUCTS_ENDPOINT"
echo ""

# Función para crear un producto via API
create_product() {
    local product_data="$1"
    local product_id=$(echo "$product_data" | jq -r '.id')
    
    echo "📦 Creando producto: $product_id"
    
    response=$(curl -s -w "\n%{http_code}" \
        -X POST \
        -H "Content-Type: application/json" \
        -H "Accept: application/json" \
        -d "$product_data" \
        "$BASE_URL$PRODUCTS_ENDPOINT")
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -eq 201 ]; then
        echo "✅ Producto $product_id creado exitosamente"
    elif [ "$http_code" -eq 409 ]; then
        echo "⚠️  Producto $product_id ya existe (omitiendo)"
    else
        echo "❌ Error creando producto $product_id (HTTP: $http_code)"
        echo "   Respuesta: $body"
    fi
    echo ""
}

# Producto 1: Crédito Personal Express
echo "📋 1/8 - Crédito Personal Express"
create_product '{
    "id": "CP-PEN-001",
    "name": "Crédito Personal Express",
    "description": "Crédito personal de rápida aprobación para gastos inmediatos sin garantía específica.",
    "category": "Crédito Personal",
    "subcategory": "Crédito Personal a Corto Plazo",
    "minimumAmount": 1000.00,
    "maximumAmount": 15000.00,
    "currency": "S/",
    "term": "6 a 12 meses",
    "minimumRate": 12.00,
    "maximumRate": 16.00,
    "requirements": [
        "DNI vigente",
        "Recibos de ingresos de los últimos 3 meses",
        "Constancia laboral",
        "Historial crediticio regular",
        "Ingresos mínimos S/ 1,500"
    ],
    "features": [
        "Aprobación rápida",
        "Sin garantía específica", 
        "Cuotas fijas"
    ],
    "benefits": [
        "Tasa preferencial",
        "Proceso 100% digital",
        "Desembolso inmediato"
    ],
    "active": true
}'

# Producto 2: Crédito Personal Premium
echo "📋 2/8 - Crédito Personal Premium"
create_product '{
    "id": "CP-PEN-002",
    "name": "Crédito Personal Premium",
    "description": "Crédito personal con mejores condiciones para clientes con excelente historial crediticio.",
    "category": "Crédito Personal",
    "subcategory": "Crédito Personal a Mediano Plazo",
    "minimumAmount": 15000.00,
    "maximumAmount": 50000.00,
    "currency": "S/",
    "term": "13 a 24 meses",
    "minimumRate": 14.00,
    "maximumRate": 20.00,
    "requirements": [
        "DNI vigente",
        "Recibos de ingresos de los últimos 6 meses",
        "Constancia laboral con mínimo 1 año de antigüedad",
        "Historial crediticio bueno",
        "Ingresos mínimos S/ 3,000"
    ],
    "features": [
        "Montos altos",
        "Plazos flexibles",
        "Cuotas fijas"
    ],
    "benefits": [
        "Tasa competitiva",
        "Sin penalidad por prepago",
        "Asesoría financiera"
    ],
    "active": true
}'

# Producto 3: Crédito Hipotecario Mi Primera Casa
echo "📋 3/8 - Crédito Hipotecario Mi Primera Casa"
create_product '{
    "id": "CH-PEN-001",
    "name": "Crédito Hipotecario Mi Primera Casa",
    "description": "Crédito hipotecario especial para la compra de primera vivienda con beneficios del estado.",
    "category": "Crédito Hipotecario",
    "subcategory": "Crédito Hipotecario Primera Vivienda",
    "minimumAmount": 50000.00,
    "maximumAmount": 200000.00,
    "currency": "S/",
    "term": "10 a 20 años",
    "minimumRate": 6.00,
    "maximumRate": 8.50,
    "requirements": [
        "DNI vigente",
        "Recibos de ingresos de los últimos 6 meses",
        "Constancia laboral con mínimo 2 años de antigüedad",
        "Historial crediticio bueno",
        "Ingresos familiares mínimos S/ 5,000",
        "Tasación de la propiedad",
        "Certificado de no poseer otra vivienda"
    ],
    "features": [
        "Beneficios estatales",
        "Tasas preferenciales",
        "Plazos largos"
    ],
    "benefits": [
        "Subsidio gubernamental",
        "Deducción fiscal",
        "Seguro de desgravamen incluido"
    ],
    "active": true
}'

# Producto 4: Crédito Automotriz Nuevo
echo "📋 4/8 - Crédito Automotriz Nuevo"
create_product '{
    "id": "CA-PEN-001",
    "name": "Crédito Automotriz Nuevo",
    "description": "Financiamiento para la compra de vehículos nuevos con tasas preferenciales.",
    "category": "Crédito Automotriz",
    "subcategory": "Crédito Automotriz Vehículo Nuevo",
    "minimumAmount": 15000.00,
    "maximumAmount": 80000.00,
    "currency": "S/",
    "term": "2 a 5 años",
    "minimumRate": 8.00,
    "maximumRate": 12.00,
    "requirements": [
        "DNI vigente",
        "Licencia de conducir vigente",
        "Recibos de ingresos de los últimos 3 meses",
        "Constancia laboral con mínimo 1 año de antigüedad",
        "Historial crediticio bueno",
        "Ingresos mínimos S/ 2,500",
        "Cuota inicial del 20% mínimo"
    ],
    "features": [
        "Vehículos nuevos",
        "Tasas preferenciales",
        "Seguro vehicular"
    ],
    "benefits": [
        "SOAT incluido",
        "Seguro de desgravamen",
        "Mantenimiento gratuito primer año"
    ],
    "active": true
}'

# Producto 5: Crédito Empresarial PYME
echo "📋 5/8 - Crédito Empresarial PYME"
create_product '{
    "id": "CE-PEN-001",
    "name": "Crédito Empresarial PYME",
    "description": "Financiamiento para pequeñas y medianas empresas para capital de trabajo e inversión.",
    "category": "Crédito Empresarial",
    "subcategory": "Crédito Empresarial PYME",
    "minimumAmount": 10000.00,
    "maximumAmount": 200000.00,
    "currency": "S/",
    "term": "1 a 5 años",
    "minimumRate": 7.00,
    "maximumRate": 10.00,
    "requirements": [
        "RUC vigente",
        "Estados financieros de los últimos 2 años",
        "Flujo de caja proyectado",
        "Constitución de la empresa",
        "Historial crediticio empresarial bueno",
        "Ventas anuales mínimas S/ 120,000",
        "Garantías específicas según monto"
    ],
    "features": [
        "Capital de trabajo",
        "Inversión en activos",
        "Línea de crédito"
    ],
    "benefits": [
        "Asesoría empresarial",
        "Tasas competitivas",
        "Plazos flexibles"
    ],
    "active": true
}'

# Producto 6: Personal Credit Express USD
echo "📋 6/8 - Personal Credit Express USD"
create_product '{
    "id": "CP-USD-001",
    "name": "Personal Credit Express USD",
    "description": "Crédito personal en dólares para gastos en moneda extranjera con aprobación rápida.",
    "category": "Crédito Personal",
    "subcategory": "Crédito Personal a Corto Plazo",
    "minimumAmount": 300.00,
    "maximumAmount": 5000.00,
    "currency": "USD",
    "term": "6 a 12 meses",
    "minimumRate": 12.50,
    "maximumRate": 17.00,
    "requirements": [
        "DNI vigente",
        "Recibos de ingresos de los últimos 3 meses",
        "Constancia laboral",
        "Historial crediticio regular",
        "Ingresos mínimos USD 450"
    ],
    "features": [
        "Moneda dólares",
        "Aprobación rápida",
        "Proceso digital"
    ],
    "benefits": [
        "Protección cambiaria",
        "Cuotas en dólares",
        "Sin comisión por cambio"
    ],
    "active": true
}'

# Producto 7: Crédito MiCrédito
echo "📋 7/8 - Crédito MiCrédito"
create_product '{
    "id": "CMC-PEN-001",
    "name": "Crédito MiCrédito",
    "description": "Microcrédito para emprendedores y pequeños negocios con montos accesibles y requisitos flexibles.",
    "category": "Microcrédito",
    "subcategory": "Microcrédito Emprendimiento",
    "minimumAmount": 500.00,
    "maximumAmount": 8000.00,
    "currency": "S/",
    "term": "3 a 18 meses",
    "minimumRate": 15.00,
    "maximumRate": 25.00,
    "requirements": [
        "DNI vigente",
        "Recibo de servicios del domicilio",
        "Constancia de ingresos del negocio",
        "Referencias comerciales",
        "Historial crediticio básico",
        "Ingresos mínimos S/ 800"
    ],
    "features": [
        "Requisitos flexibles",
        "Montos accesibles",
        "Apoyo al emprendimiento"
    ],
    "benefits": [
        "Capacitación empresarial",
        "Red de proveedores",
        "Seguimiento personalizado"
    ],
    "active": true
}'

# Producto 8: Crédito Energía Renovable
echo "📋 8/8 - Crédito Energía Renovable"
create_product '{
    "id": "CEN-PEN-001",
    "name": "Crédito Energía Renovable",
    "description": "Financiamiento para instalación de paneles solares, sistemas de energía renovable y eficiencia energética.",
    "category": "Crédito Verde",
    "subcategory": "Crédito Energía Solar",
    "minimumAmount": 8000.00,
    "maximumAmount": 60000.00,
    "currency": "S/",
    "term": "2 a 8 años",
    "minimumRate": 6.00,
    "maximumRate": 10.00,
    "requirements": [
        "DNI vigente",
        "Título de propiedad de la vivienda",
        "Cotización técnica del sistema",
        "Estudio de factibilidad técnica",
        "Constancia laboral",
        "Recibos de ingresos de los últimos 6 meses",
        "Historial crediticio bueno",
        "Ingresos mínimos S/ 3,500",
        "Certificación de instalador autorizado"
    ],
    "features": [
        "Energía renovable",
        "Beneficios ambientales",
        "Ahorro energético"
    ],
    "benefits": [
        "Deducción fiscal",
        "Ahorro en factura eléctrica",
        "Contribución ambiental"
    ],
    "active": true
}'

echo "🎉 Script de creación de productos completado!"
echo "📝 Todos los productos han sido procesados."
echo "🔍 Revisa los logs arriba para verificar que todos los productos se crearon correctamente."
echo ""
echo "💡 Nota: Los productos se sincronizan automáticamente con Azure AI Search"
echo "   gracias al ProductSyncListener configurado en la aplicación."