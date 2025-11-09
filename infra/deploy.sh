#!/bin/bash

# 🚀 Script de despliegue granular para Terraform con variables de entorno
# Uso: ./deploy.sh [environment] [command]
# Comandos:
#   init    - Inicializa y genera plan (init + validate + plan)
#   apply   - Aplica el plan generado
#   destroy - Destruye recursos (con confirmación)
#   all     - Flujo completo (init + plan + apply) - REQUIERE CONFIRMACIÓN
# 
# Ejemplos:
#   ./deploy.sh dev init     # Prepara el plan para dev
#   ./deploy.sh dev apply    # Aplica el plan de dev
#   ./deploy.sh dev destroy  # Destruye recursos de dev
#   ./deploy.sh dev all      # Flujo completo con confirmación
#   ./deploy.sh dev          # Por defecto hace 'init'

set -e  # Exit on any error

# 🔐 CARGAR VARIABLES DE ENTORNO SENSIBLES
if [ -f "main.env" ]; then
    echo "🔐 Cargando variables de entorno sensibles desde main.env..."
    source main.env
else
    echo "⚠️  Archivo main.env no encontrado. Crea uno basado en main.env.example"
    echo "💡 Comando: cp main.env.example main.env && nano main.env"
    exit 1
fi

# 🎯 CONFIGURACIÓN
DEFAULT_ENV="dev"
DEFAULT_CMD="init"
ENV=${1:-$DEFAULT_ENV}
CMD=${2:-$DEFAULT_CMD}
TFVARS_FILE="environments/${ENV}.tfvars.json"
PLAN_FILE="terraform.${ENV}.plan"

# 🎨 COLORES PARA OUTPUT
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 📋 FUNCIONES AUXILIARES
print_step() {
    echo -e "${BLUE}==== $1 ====${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

show_usage() {
    echo -e "${BLUE}Uso:${NC} ./deploy.sh [environment] [command]"
    echo ""
    echo -e "${YELLOW}Comandos disponibles:${NC}"
    echo "  init    - Inicializa y genera plan"
    echo "  apply   - Aplica el plan generado"
    echo "  destroy - Destruye recursos"
    echo "  all     - Flujo completo (con confirmación)"
    echo ""
    echo -e "${YELLOW}Ejemplos:${NC}"
    echo "  ./deploy.sh dev init"
    echo "  ./deploy.sh dev apply" 
    echo "  ./deploy.sh dev destroy"
    echo "  ./deploy.sh dev all"
}

# 📁 VERIFICACIONES INICIALES
if [ ! -f "main.tf" ]; then
    print_error "Este script debe ejecutarse desde el directorio infra/"
    exit 1
fi

# 🆘 MANEJAR HELP ANTES DE VERIFICAR ARCHIVOS
if [ "$CMD" = "help" ] || [ "$CMD" = "-h" ] || [ "$CMD" = "--help" ] || [ "$ENV" = "help" ]; then
    show_usage
    exit 0
fi

if [ ! -f "$TFVARS_FILE" ]; then
    print_error "Archivo de configuración no encontrado: $TFVARS_FILE"
    echo -e "${YELLOW}Archivos disponibles:${NC}"
    ls -la environments/*.tfvars.json 2>/dev/null || echo "No hay archivos tfvars disponibles"
    exit 1
fi

print_success "Entorno: $ENV | Comando: $CMD | Config: $TFVARS_FILE"

# 🔧 FUNCIÓN: INIT + PLAN
execute_init_and_plan() {
    print_step "INICIALIZACIÓN Y PLAN para entorno: $ENV"
    
    terraform init
    print_success "Terraform inicializado"

    terraform validate
    print_success "Configuración validada"

    terraform plan -var-file="$TFVARS_FILE" -out="$PLAN_FILE"
    print_success "Plan generado: $PLAN_FILE"
    
    echo ""
    print_success "✅ Plan listo para aplicar!"
    echo -e "${BLUE}Siguiente paso:${NC} ./deploy.sh $ENV apply"
}

# 🚀 FUNCIÓN: APPLY
execute_apply() {
    if [ ! -f "$PLAN_FILE" ]; then
        print_error "No se encontró el archivo de plan: $PLAN_FILE"
        echo -e "${YELLOW}Ejecuta primero:${NC} ./deploy.sh $ENV init"
        exit 1
    fi
    
    print_step "APLICANDO PLAN para entorno: $ENV"
    
    terraform apply "$PLAN_FILE"
    print_success "¡Despliegue completado!"

    terraform output
    
    rm -f "$PLAN_FILE"
    print_success "Plan limpiado"
    
    echo ""
    print_success "🎉 ¡Despliegue completo! Entorno: $ENV"
}

# 🗑️ FUNCIÓN: DESTROY
execute_destroy() {
    print_step "DESTRUIR RECURSOS para entorno: $ENV"
    
    echo ""
    print_warning "🚨 ADVERTENCIA: Vas a DESTRUIR todos los recursos del entorno: $ENV"
    print_warning "Esta acción es IRREVERSIBLE"
    echo ""
    print_warning "¿Estás seguro? Escribe 'DESTROY' para confirmar:"
    read -r confirmation

    if [ "$confirmation" != "DESTROY" ]; then
        print_error "Operación cancelada"
        exit 1
    fi

    terraform destroy -var-file="$TFVARS_FILE" -auto-approve
    print_success "🗑️ Recursos destruidos exitosamente"
}

# 🔄 FUNCIÓN: FLUJO COMPLETO
execute_all() {
    execute_init_and_plan
    
    echo ""
    print_warning "¿Deseas aplicar los cambios? El plan se ejecutará en el entorno: $ENV"
    echo -e "${YELLOW}Presiona Enter para continuar o Ctrl+C para cancelar${NC}"
    read -r
    
    execute_apply
}

# 🎯 EJECUTAR COMANDO SELECCIONADO
case $CMD in
    "init")
        execute_init_and_plan
        ;;
    "apply")
        execute_apply
        ;;
    "destroy")
        execute_destroy
        ;;
    "all")
        execute_all
        ;;
    "help"|"-h"|"--help")
        show_usage
        ;;
    *)
        print_error "Comando no válido: $CMD"
        echo ""
        show_usage
        exit 1
        ;;
esac