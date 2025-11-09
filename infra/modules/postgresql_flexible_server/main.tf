# PostgreSQL Flexible Server
resource "azurerm_postgresql_flexible_server" "main" {
  name                = var.server_name
  resource_group_name = var.resource_group_name
  location            = var.location
  version             = var.postgresql_version
  sku_name            = var.sku_name
  storage_mb          = var.storage_mb
  
  administrator_login    = var.administrator_login
  administrator_password = var.administrator_password
  
  backup_retention_days = var.backup_retention_days
  
  lifecycle {
    prevent_destroy = true
  }
}

# Regla de firewall para permitir acceso desde servicios de Azure
resource "azurerm_postgresql_flexible_server_firewall_rule" "azure_services" {
  name             = "allow-azure-services"
  server_id        = azurerm_postgresql_flexible_server.main.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

# Crear las bases de datos especificadas
resource "azurerm_postgresql_flexible_server_database" "databases" {
  count     = length(var.databases)
  name      = var.databases[count.index]
  server_id = azurerm_postgresql_flexible_server.main.id
  collation = "en_US.utf8"
  charset   = "utf8"
}