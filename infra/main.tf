# Local values for consistent naming
locals {
  name_prefix = "${var.project_name}-${var.environment}"
  
  # 🆕 NOMBRE SIMPLE Y VÁLIDO PARA CONTAINER REGISTRY
  # Remueve caracteres inválidos y mantiene solo alfanuméricos
  acr_name = lower(replace(replace("${var.project_name}${var.environment}acr", "-", ""), "_", ""))
  
  common_tags = merge(var.tags, {
    Environment = var.environment
    Location    = var.location
  })
}

# Data source para obtener información del cliente actual
data "azurerm_client_config" "current" {}

# Resource Group Module
module "resource_group" {
  source = "./modules/resource_group"

  name     = "${local.name_prefix}-rg"
  location = var.location
  tags     = local.common_tags
}

# Container Registry Module
module "container_registry" {
  source = "./modules/container_registry"

  name                = local.acr_name  # 🆕 Nombre simple sin caracteres inválidos
  resource_group_name = module.resource_group.name
  location            = var.location
  tags                = local.common_tags

  depends_on = [module.resource_group]
}

# Log Analytics Workspace Module
module "log_analytics_workspace" {
  source = "./modules/log_analytics_workspace"

  name                = "${local.name_prefix}-law"
  resource_group_name = module.resource_group.name
  location            = var.location
  tags                = local.common_tags

  depends_on = [module.resource_group]
}

# Container App Environment Module
module "container_app_environment" {
  source = "./modules/container_app_environment"

  name                         = "${local.name_prefix}-cae"
  resource_group_name          = module.resource_group.name
  location                     = var.location
  log_analytics_workspace_id   = module.log_analytics_workspace.id
  tags                         = local.common_tags

  depends_on = [module.log_analytics_workspace]
}

# Container App Instance Module
module "container_app_instance" {
  source = "./modules/container_app_instance"

  name                         = "personal-finance-api"
  resource_group_name          = module.resource_group.name
  location                     = var.location
  container_app_environment_id = module.container_app_environment.id
  container_registry_server    = module.container_registry.login_server
  container_registry_name      = module.container_registry.name
  source_code_path             =" ${path.module}/personal-finance-api"
  image_name                   = "personal-finance-api"
  image_tag                    = "0.0.1"
  
  # 🆕 AUTENTICACIÓN DEL CONTAINER REGISTRY
  registry_username = module.container_registry.admin_username
  registry_password = module.container_registry.admin_password
  
  # 🆕 CONFIGURACIÓN DEL PUERTO
  target_port      = var.app_port != null ? var.app_port : 8080
  external_enabled = var.external_access
  
  # 🆕 VARIABLES DE ENTORNO
  environment_variables = [
    { name  = "APP_VERSION", value = "1.0.0"},
    { name  = "AZURE_DOCUMENT_INTELLIGENCE_KEY", value= var.document_intelligence_key},
    { name  = "AZURE_DOCUMENT_INTELLIGENCE_ENDPOINT", value= var.document_intelligence_endpoint},
    { name  = "DB_USERNAME", value=},
    { name  = "DB_PASSWORD", value=},
  ]
  
  # Configuración de recursos
  cpu         = var.container_cpu
  memory      = var.container_memory
  min_replicas = var.min_replicas
  max_replicas = var.max_replicas
  
  tags = local.common_tags

  depends_on = [module.container_app_environment, module.container_registry]
}


module "container_app_instance" {
  source = "./modules/container_app_instance"

  name                         = "claim-management-api"
  resource_group_name          = module.resource_group.name
  location                     = var.location
  container_app_environment_id = module.container_app_environment.id
  container_registry_server    = module.container_registry.login_server
  container_registry_name      = module.container_registry.name
  source_code_path             =" ${path.module}/claim-management-api"
  image_name                   = "claim-management-api"
  image_tag                    = "0.0.1"
  
  # 🆕 AUTENTICACIÓN DEL CONTAINER REGISTRY
  registry_username = module.container_registry.admin_username
  registry_password = module.container_registry.admin_password
  
  # 🆕 CONFIGURACIÓN DEL PUERTO
  target_port      = var.app_port != null ? var.app_port : 8080
  external_enabled = var.external_access
  
  # 🆕 VARIABLES DE ENTORNO
  environment_variables = [
    { name  = "APP_VERSION", value = "1.0.0"},
    { name  = "OPEN_AI_ENDPOINT", value = var.open_api_endpoint },
    { name  = "OPEN_AI_DEPLOYMENT_NAME", value = var.model_deployment_name },
    { name  = "OPEN_AI_MODEL_NAME", value = var.open_api_embedding_model },
    { name  = "OPEN_AI_API_KEY", value = var.open_ai_key },
    { name  = "DB_USERNAME", value=},
    { name  = "DB_PASSWORD", value=},
  ]
  
  # Configuración de recursos
  cpu         = var.container_cpu
  memory      = var.container_memory
  min_replicas = var.min_replicas
  max_replicas = var.max_replicas
  
  tags = local.common_tags

  depends_on = [module.container_app_environment, module.container_registry]
}

module "container_app_instance" {
  source = "./modules/container_app_instance"

  name                         = "credit-management-api"
  resource_group_name          = module.resource_group.name
  location                     = var.location
  container_app_environment_id = module.container_app_environment.id
  container_registry_server    = module.container_registry.login_server
  container_registry_name      = module.container_registry.name
  source_code_path             =" ${path.module}/credit-management-api"
  image_name                   = "credit-management-api"
  image_tag                    = "0.0.1"
  
  # 🆕 AUTENTICACIÓN DEL CONTAINER REGISTRY
  registry_username = module.container_registry.admin_username
  registry_password = module.container_registry.admin_password
  
  # 🆕 CONFIGURACIÓN DEL PUERTO
  target_port      = var.app_port != null ? var.app_port : 8080
  external_enabled = var.external_access
  
  # 🆕 VARIABLES DE ENTORNO
  environment_variables = [
    { name  = "APP_VERSION", value = "1.0.0"},
    { name  = "OPEN_AI_ENDPOINT", value = var.open_api_endpoint},
    { name  = "OPEN_AI_KEY", value = var.open_ai_key },
    { name  = "OPEN_AI_EMBEDDING_MODEL", value = var.open_api_embedding_model},
    { name  = "AI_SEARCH_ENDPOINT", value = var.ai_searh_endpoint },
    { name  = "AI_SEARCH_KEY", value = var.ai_search_key },
    { name  = "DB_USERNAME", value=},
    { name  = "DB_PASSWORD", value=},
  ]
  
  # Configuración de recursos
  cpu         = var.container_cpu
  memory      = var.container_memory
  min_replicas = var.min_replicas
  max_replicas = var.max_replicas
  
  tags = local.common_tags

  depends_on = [module.container_app_environment, module.container_registry]
}