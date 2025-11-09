import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { 
  PageableAdapter, 
  PageableResult, 
  SortDirection 
} from '../../../components/pageable-grid/pageable-adapter.interface';
import { CreditManagementService } from '../services/credit-management.service';
import { Product, ProductFilters } from '../interfaces/credit-management.interface';

@Injectable()
export class ProductsPageableAdapter implements PageableAdapter<Product> {
  private creditManagementService = inject(CreditManagementService);
  
  private currentFilters: ProductFilters = {};
  private currentPageSize = 15; // Siguiendo el estándar CRUD_SPEC

  loadData(): Observable<PageableResult<Product>> {
    return this.creditManagementService.getProducts({
      page: 0, // Primera página
      size: this.currentPageSize,
      ...this.currentFilters
    }).pipe(
      map(response => ({
        data: response.data,
        totalElements: response.total,
        totalPages: response.totalPages || Math.ceil(response.total / this.currentPageSize),
        currentPage: response.currentPage || 0,
        pageSize: this.currentPageSize,
        hasNext: response.currentPage ? response.currentPage < (response.totalPages || 1) - 1 : false,
        hasPrevious: response.currentPage ? response.currentPage > 0 : false
      }))
    );
  }

  changePage(page: number, pageSize: number): Observable<PageableResult<Product>> {
    this.currentPageSize = pageSize;
    return this.creditManagementService.getProducts({
      page: page,
      size: pageSize,
      ...this.currentFilters
    }).pipe(
      map(response => ({
        data: response.data,
        totalElements: response.total,
        totalPages: response.totalPages || Math.ceil(response.total / pageSize),
        currentPage: page,
        pageSize: pageSize,
        hasNext: page < (response.totalPages || 1) - 1,
        hasPrevious: page > 0
      }))
    );
  }

  changeSort(sortField: string, sortDirection: SortDirection): Observable<PageableResult<Product>> {
    // La API soporta ordenamiento por el campo 'sort'
    const sortValue = sortDirection === 'desc' ? `-${sortField}` : sortField;
    
    return this.creditManagementService.getProducts({
      page: 0,
      size: this.currentPageSize,
      sort: sortValue,
      ...this.currentFilters
    }).pipe(
      map(response => ({
        data: response.data,
        totalElements: response.total,
        totalPages: response.totalPages || Math.ceil(response.total / this.currentPageSize),
        currentPage: 0,
        pageSize: this.currentPageSize,
        hasNext: response.totalPages ? response.totalPages > 1 : false,
        hasPrevious: false,
        sortField,
        sortDirection
      }))
    );
  }

  changePageAndSort(
    page: number, 
    pageSize: number, 
    sortField: string, 
    sortDirection: SortDirection
  ): Observable<PageableResult<Product>> {
    this.currentPageSize = pageSize;
    const sortValue = sortDirection === 'desc' ? `-${sortField}` : sortField;
    
    return this.creditManagementService.getProducts({
      page: page,
      size: pageSize,
      sort: sortValue,
      ...this.currentFilters
    }).pipe(
      map(response => ({
        data: response.data,
        totalElements: response.total,
        totalPages: response.totalPages || Math.ceil(response.total / pageSize),
        currentPage: page,
        pageSize: pageSize,
        hasNext: page < (response.totalPages || 1) - 1,
        hasPrevious: page > 0,
        sortField,
        sortDirection
      }))
    );
  }

  /**
   * Método para actualizar filtros
   */
  setFilters(filters: ProductFilters): void {
    this.currentFilters = filters;
  }

  /**
   * Obtener filtros actuales
   */
  getFilters(): ProductFilters {
    return { ...this.currentFilters };
  }

  /**
   * Limpiar filtros
   */
  clearFilters(): void {
    this.currentFilters = {};
  }
}