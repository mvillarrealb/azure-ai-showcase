# 🔐 Gestión de Variables de Entorno para Terraform

Este proyecto utiliza variables de entorno para manejar información sensible como contraseñas y claves de API.

## 📁 Estructura de Archivos

```
infra/
├── main.env.example     # 📖 Archivo de ejemplo (versionado)
├── main.env            # 🔐 Variables reales (NO versionado, en .gitignore)
├── deploy.sh           # 🚀 Script que carga automáticamente main.env
└── environments/
    └── dev.tfvars.json # ⚙️ Configuración no sensible
```

## 🚀 Configuración Inicial

### 1. Crear archivo de variables sensibles:
```bash
cd infra
cp main.env.example main.env
nano main.env  # o tu editor preferido
```

### 2. Editar `main.env` con valores reales:
```bash
# 🐘 POSTGRESQL (REQUERIDO)
export TF_VAR_postgres_administrator_password="TuPasswordSegura123!"

# 🤖 AI SERVICES (Opcional - solo si los usas)
export TF_VAR_document_intelligence_key="tu-clave-real"
export TF_VAR_open_ai_key="sk-proj-tu-clave-real"
# ... resto de variables según necesites
```

## 🎯 Uso

El script `deploy.sh` **automáticamente** carga las variables de `main.env`:

```bash
# El script carga main.env automáticamente
./deploy.sh dev init    # Cargar variables → Init → Plan
./deploy.sh dev apply   # Cargar variables → Apply
./deploy.sh dev all     # Cargar variables → Init → Plan → Apply
```

## 🔒 Seguridad

- ✅ `main.env` está en `.gitignore` - **NO se versiona**
- ✅ `main.env.example` se versiona para documentación
- ✅ Variables sensibles como `TF_VAR_*` tienen **mayor prioridad** que JSON
- ✅ El script verifica que `main.env` exista antes de continuar

## 📋 Precedencia de Variables

Terraform aplica variables en este orden (menor → mayor prioridad):

1. **Defaults en `variables.tf`** (menor prioridad)
2. **Archivo `dev.tfvars.json`**
3. **Variables de entorno `TF_VAR_*`** ← desde `main.env`
4. **CLI con `-var`** (mayor prioridad)

## ❌ Qué NO hacer

```bash
# ❌ NO hagas commit de main.env
git add main.env  # NUNCA!

# ❌ NO pongas secretos en dev.tfvars.json
{
  "postgres_administrator_password": "secret123"  # MAL!
}

# ❌ NO ejecutes terraform directamente sin cargar variables
terraform plan  # Faltarán las variables sensibles
```

## ✅ Flujo Recomendado

```bash
# 1. Configurar una sola vez
cp main.env.example main.env
nano main.env

# 2. Usar siempre el script (carga automáticamente las variables)
./deploy.sh dev init
./deploy.sh dev apply

# 3. Para desarrollo, cambiar solo main.env
nano main.env  # Cambiar password o claves
./deploy.sh dev init  # Las nuevas variables se cargan automáticamente
```

## 🔧 Troubleshooting

### Error: "main.env no encontrado"
```bash
⚠️  Archivo main.env no encontrado. Crea uno basado en main.env.example
💡 Comando: cp main.env.example main.env && nano main.env
```

**Solución:**
```bash
cp main.env.example main.env
nano main.env  # Editar con valores reales
```

### Variable no encontrada en Terraform
**Problema:** `No declaration found for "var.postgres_administrator_password"`

**Solución:** Verificar que la variable esté definida en `main.env`:
```bash
# Verificar contenido
cat main.env | grep postgres_administrator_password

# Debe mostrar:
export TF_VAR_postgres_administrator_password="TuPassword"
```

## 📚 Variables Disponibles

| Variable en main.env | Descripción | Requerida |
|---------------------|-------------|-----------|
| `TF_VAR_postgres_administrator_password` | Contraseña PostgreSQL | ✅ **SÍ** |
| `TF_VAR_document_intelligence_key` | Azure Document Intelligence | ❌ Opcional |
| `TF_VAR_open_ai_key` | OpenAI/Azure OpenAI | ❌ Opcional |
| `TF_VAR_ai_search_key` | Azure AI Search | ❌ Opcional |

Ver `main.env.example` para la lista completa y ejemplos.