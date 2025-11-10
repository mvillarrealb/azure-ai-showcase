output "resource_group_name" {
  description = "Name of the created resource group"
  value       = module.resource_group.name
}

output "resource_group_id" {
  description = "ID of the created resource group"
  value       = module.resource_group.id
}

output "container_registry_login_server" {
  description = "Login server URL for the container registry"
  value       = module.container_registry.login_server
}

output "container_registry_name" {
  description = "Name of the container registry"
  value       = module.container_registry.name
}

# 🆕 NOMBRE GENERADO SIMPLE PARA ACR
output "container_registry_generated_name" {
  description = "Nombre generado para el Container Registry (sin caracteres inválidos)"
  value       = local.acr_name
}

output "log_analytics_workspace_id" {
  description = "ID of the Log Analytics workspace"
  value       = module.log_analytics_workspace.id
}

output "container_app_environment_id" {
  description = "ID of the Container App Environment"
  value       = module.container_app_environment.id
}

# 🆕 OUTPUTS PARA PERSONAL FINANCE API
output "personal_finance_app_url" {
  description = "URL of the Personal Finance API"
  value       = module.personal_finance_api.app_url
}

output "personal_finance_app_fqdn" {
  description = "FQDN of the Personal Finance API"
  value       = module.personal_finance_api.fqdn
}

# 🆕 OUTPUTS PARA CLAIM MANAGEMENT API
output "claim_management_app_url" {
  description = "URL of the Claim Management API"
  value       = module.claim_management_api.app_url
}

output "claim_management_app_fqdn" {
  description = "FQDN of the Claim Management API"
  value       = module.claim_management_api.fqdn
}

# 🆕 OUTPUTS PARA CREDIT MANAGEMENT API
output "credit_management_app_url" {
  description = "URL of the Credit Management API"
  value       = module.credit_management_api.app_url
}

output "credit_management_app_fqdn" {
  description = "FQDN of the Credit Management API"
  value       = module.credit_management_api.fqdn
}

# 🆕 OUTPUTS GENERALES DE CONTAINER APPS
output "all_container_app_urls" {
  description = "URLs de todas las Container Apps desplegadas"
  value = {
    personal_finance = module.personal_finance_api.app_url
    claim_management = module.claim_management_api.app_url
    credit_management = module.credit_management_api.app_url
  }
}

# 🆕 INFORMACIÓN DE POSTGRESQL
output "postgresql_server_fqdn" {
  description = "FQDN del servidor PostgreSQL"
  value       = module.postgresql_flexible_server.server_fqdn
}