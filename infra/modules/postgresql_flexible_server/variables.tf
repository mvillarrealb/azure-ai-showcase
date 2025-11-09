variable "server_name" {
  description = "Nombre del servidor PostgreSQL Flexible"
  type        = string
}

variable "databases" {
  description = "Lista de bases de datos a crear"
  type        = list(string)
}

variable "administrator_login" {
  description = "Usuario administrador"
  type        = string
}

variable "administrator_password" {
  description = "Contraseña administrador"
  type        = string
  sensitive   = true
}

variable "resource_group_name" {
  description = "Resource Group name"
  type        = string
}

variable "location" {
  description = "Azure region"
  type        = string
}

# Valores por defecto para configuraciones adicionales
variable "sku_name" {
  description = "SKU del servidor PostgreSQL"
  type        = string
  default     = "B_Standard_B1ms"
}

variable "storage_mb" {
  description = "Almacenamiento en MB"
  type        = number
  default     = 32768
}

variable "postgresql_version" {
  description = "Versión de PostgreSQL"
  type        = string
  default     = "15"
}

variable "backup_retention_days" {
  description = "Días de retención de backup"
  type        = number
  default     = 7
}