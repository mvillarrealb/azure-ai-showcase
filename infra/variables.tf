variable "environment" {
  description = "Environment name (e.g., dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "location" {
  description = "Azure region where resources will be created"
  type        = string
  default     = "westus"
}

variable "project_name" {
  description = "Name of the project used for resource naming"
  type        = string
  default     = "mcp-app"
}

variable "tags" {
  description = "Common tags to be applied to all resources"
  type        = map(string)
  default = {
    Environment = "dev"
    Project     = "mcp-app"
    ManagedBy   = "terraform"
  }
}

# 🆕 NUEVAS VARIABLES PARA CONTAINER APP
variable "app_port" {
  description = "Puerto en el que escucha la aplicación"
  type        = number
  default     = 8080
  
  validation {
    condition     = var.app_port >= 1 && var.app_port <= 65535
    error_message = "El puerto debe estar entre 1 y 65535."
  }
}

variable "external_access" {
  description = "Si la aplicación debe ser accesible externamente"
  type        = bool
  default     = true
}

variable "container_cpu" {
  description = "CPU asignada al contenedor"
  type        = number
  default     = 0.25
  
  validation {
    condition = contains([0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0], var.container_cpu)
    error_message = "CPU debe ser uno de: 0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0."
  }
}

variable "container_memory" {
  description = "Memoria asignada al contenedor"
  type        = string
  default     = "0.5Gi"
  
  validation {
    condition = contains(["0.5Gi", "1Gi", "1.5Gi", "2Gi", "2.5Gi", "3Gi", "3.5Gi", "4Gi"], var.container_memory)
    error_message = "Memoria debe ser uno de: 0.5Gi, 1Gi, 1.5Gi, 2Gi, 2.5Gi, 3Gi, 3.5Gi, 4Gi."
  }
}

variable "min_replicas" {
  description = "Número mínimo de réplicas"
  type        = number
  default     = 1
  
  validation {
    condition     = var.min_replicas >= 0 && var.min_replicas <= 1000
    error_message = "min_replicas debe estar entre 0 y 1000."
  }
}

variable "max_replicas" {
  description = "Número máximo de réplicas"
  type        = number
  default     = 10
  
  validation {
    condition     = var.max_replicas >= 1 && var.max_replicas <= 1000
    error_message = "max_replicas debe estar entre 1 y 1000."
  }
}

# 🆕 VARIABLES PARA POSTGRESQL
variable "postgres_administrator_login" {
  description = "Usuario administrador para PostgreSQL"
  type        = string
  default     = "pgadmin"
}

variable "postgres_administrator_password" {
  description = "Contraseña del administrador para PostgreSQL"
  type        = string
  sensitive   = true
}

# 🆕 VARIABLES PARA AI SERVICES
variable "document_intelligence_key" {
  description = "Clave del servicio Azure Document Intelligence"
  type        = string
  sensitive   = true
  default     = ""
}

variable "document_intelligence_endpoint" {
  description = "Endpoint del servicio Azure Document Intelligence"
  type        = string
  default     = ""
}

variable "open_ai_endpoint" {
  description = "Endpoint de Azure OpenAI"
  type        = string
  default     = ""
}

variable "open_ai_key" {
  description = "Clave de API para OpenAI/Azure OpenAI"
  type        = string
  sensitive   = true
  default     = ""
}

variable "open_api_embedding_model" {
  description = "Nombre del modelo de embeddings de OpenAI"
  type        = string
  default     = "text-embedding-ada-002"
}

variable "model_deployment_name" {
  description = "Nombre del deployment del modelo en Azure OpenAI"
  type        = string
  default     = ""
}

variable "ai_search_endpoint" {
  description = "Endpoint del servicio Azure AI Search"
  type        = string
  default     = ""
}

variable "ai_search_key" {
  description = "Clave de API para Azure AI Search"
  type        = string
  sensitive   = true
  default     = ""
}

# 🆕 VARIABLES PARA COGNITIVE SERVICES
variable "cognitive_services_endpoint" {
  description = "Endpoint del servicio Azure Cognitive Services"
  type        = string
  default     = ""
}

variable "cognitive_services_key" {
  description = "Clave de API para Azure Cognitive Services"
  type        = string
  sensitive   = true
  default     = ""
}