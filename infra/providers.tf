terraform {
  required_version = ">= 1.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.51.0"
    }
    null = {
      source  = "hashicorp/null"
      version = "~> 3.2"
    }
  }
}

provider "azurerm" {
  # subscription_id se lee automáticamente de ARM_SUBSCRIPTION_ID
  features {
    resource_group {
      prevent_deletion_if_contains_resources = false
    }
  }
}