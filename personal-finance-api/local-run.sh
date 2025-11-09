#!/bin/bash

# Script para ejecutar localmente el Personal Finance API
# Carga las variables de entorno desde main.env y ejecuta la aplicación

set -e

# Verificar que existe el archivo main.env
if [ ! -f "main.env" ]; then
    echo "Error: El archivo main.env no existe."
    echo "Por favor, copia main.env.example a main.env y configura las variables necesarias."
    exit 1
fi

echo "🚀 Iniciando Personal Finance API..."
echo "📁 Cargando variables de entorno desde main.env"

# Exportar variables de entorno desde main.env
set -a
source main.env
set +a

echo "🔧 Ejecutando gradlew bootRun..."
./gradlew bootRun