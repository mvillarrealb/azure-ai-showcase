import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { 
  PageableAdapter, 
  PageableResult, 
  SortDirection 
} from '../../../components/pageable-grid/pageable-adapter.interface';
import { CreditManagementService } from '../services/credit-management.service';
import { Rank, RankFilters } from '../interfaces/credit-management.interface';

@Injectable()
export class RanksPageableAdapter implements PageableAdapter<Rank> {
  private creditManagementService = inject(CreditManagementService);
  
  private currentFilters: RankFilters = {};
  private currentPageSize = 15; // Siguiendo el estándar CRUD_SPEC

  loadData(): Observable<PageableResult<Rank>> {
    return this.getFilteredData(0, this.currentPageSize);
  }

  changePage(page: number, pageSize: number): Observable<PageableResult<Rank>> {
    this.currentPageSize = pageSize;
    return this.getFilteredData(page, pageSize);
  }

  changeSort(sortField: string, sortDirection: SortDirection): Observable<PageableResult<Rank>> {
    return this.getFilteredData(0, this.currentPageSize, sortField, sortDirection);
  }

  changePageAndSort(
    page: number, 
    pageSize: number, 
    sortField: string, 
    sortDirection: SortDirection
  ): Observable<PageableResult<Rank>> {
    this.currentPageSize = pageSize;
    return this.getFilteredData(page, pageSize, sortField, sortDirection);
  }

  private getFilteredData(
    page: number, 
    pageSize: number, 
    sortField?: string, 
    sortDirection?: SortDirection
  ): Observable<PageableResult<Rank>> {
    // Construir parámetros para la API
    const filters = {
      ...this.currentFilters,
      page,
      size: pageSize,
      sort: sortField || 'name'
    };

    // Llamar a la API real
    return this.creditManagementService.getRanks(filters).pipe(
      map(response => {
        const result: PageableResult<Rank> = {
          data: response.data,
          totalElements: response.total,
          totalPages: response.totalPages,
          currentPage: response.currentPage,
          pageSize,
          hasNext: response.currentPage < response.totalPages - 1,
          hasPrevious: response.currentPage > 0,
          sortField,
          sortDirection
        };
        return result;
      })
    );
  }

  /**
   * Método para actualizar filtros
   */
  setFilters(filters: RankFilters): void {
    this.currentFilters = filters;
  }

  /**
   * Obtener filtros actuales
   */
  getFilters(): RankFilters {
    return { ...this.currentFilters };
  }

  /**
   * Limpiar filtros
   */
  clearFilters(): void {
    this.currentFilters = {};
  }

  /**
   * Refrescar datos (útil después de crear/editar rangos)
   */
  refresh(): Observable<PageableResult<Rank>> {
    return this.getFilteredData(0, this.currentPageSize);
  }
}