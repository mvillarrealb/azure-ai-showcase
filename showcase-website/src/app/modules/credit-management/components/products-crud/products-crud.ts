import { Component, OnInit, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CreditManagementService } from '../../services/credit-management.service';
import { ProductsPageableAdapter } from '../../adapters/products-pageable.adapter';
import { Product } from '../../interfaces/credit-management.interface';
import { PageableGridConfig, GridColumn } from '../../../../components/pageable-grid/pageable-adapter.interface';
import { PageableGridComponent } from '../../../../components/pageable-grid/pageable-grid.component';

@Component({
  selector: 'app-products-crud',
  standalone: true,
  imports: [CommonModule, FormsModule, PageableGridComponent],
  templateUrl: './products-crud.html',
  providers: [ProductsPageableAdapter]
})
export class ProductsCrudComponent implements OnInit {
  private creditManagementService = inject(CreditManagementService);
  private router = inject(Router);
  
  // Output para comunicar al padre
  @Output() showProductForm = new EventEmitter<void>();
  
  // Adaptador para el grid
  productsAdapter = inject(ProductsPageableAdapter);

  // Configuración del grid siguiendo estándar CRUD_SPEC
  gridConfig: PageableGridConfig = {
    pageSize: 15,
    pageSizeOptions: [15],
    showPageSizeSelector: false,
    loadingText: 'Cargando productos...',
    emptyText: 'No se encontraron productos crediticios'
  };

  // Definición de columnas para el grid basado en el OpenAPI
  gridColumns: GridColumn[] = [
    { key: 'name', label: 'Producto', sortable: true, width: '25%' },
    { key: 'category', label: 'Categoría', sortable: true, width: '15%' },
    { key: 'subcategory', label: 'Subcategoría', sortable: false, width: '15%' },
    { 
      key: 'currency', 
      label: 'Moneda', 
      sortable: false, 
      width: '10%',
      align: 'center'
    },
    { 
      key: 'minimumAmount', 
      label: 'Monto Mín.', 
      sortable: true, 
      type: 'currency', 
      align: 'right', 
      width: '12%',
      format: (value: number, item?: Product) => item ? this.formatAmount(value, item.currency) : this.formatAmountFallback(value)
    },
    { 
      key: 'maximumAmount', 
      label: 'Monto Máx.', 
      sortable: true, 
      type: 'currency', 
      align: 'right', 
      width: '12%',
      format: (value: number, item?: Product) => item ? this.formatAmount(value, item.currency) : this.formatAmountFallback(value)
    },
    { 
      key: 'minimumRate', 
      label: 'Tasa', 
      sortable: true, 
      align: 'center', 
      width: '11%',
      format: (value: number, item?: Product) => item ? this.formatRateRange(item.minimumRate, item.maximumRate) : this.formatSingleRate(value)
    }
  ];

  async ngOnInit() {
    // El adapter manejará la carga automáticamente
  }

  onViewDetails(product: Product) {
    // Mostrar detalles del producto (solo lectura)
    console.log('Ver detalles de producto:', product);
  }

  /**
   * Formatear monto con moneda
   */
  private formatAmount(amount: number, currency: 'S/' | 'USD'): string {
    return this.creditManagementService.formatCurrency(amount, currency);
  }

  /**
   * Formatear monto sin moneda específica (fallback para tooltips)
   */
  private formatAmountFallback(amount: number): string {
    return this.creditManagementService.formatCurrency(amount, 'S/');
  }

  /**
   * Formatear rango de tasas
   */
  private formatRateRange(minRate: number, maxRate: number): string {
    if (minRate === maxRate) {
      return this.creditManagementService.formatPercentage(minRate);
    }
    return `${this.creditManagementService.formatPercentage(minRate)} - ${this.creditManagementService.formatPercentage(maxRate)}`;
  }

  /**
   * Formatear una sola tasa (fallback para tooltips)
   */
  private formatSingleRate(rate: number): string {
    return this.creditManagementService.formatPercentage(rate);
  }
}