import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ClaimService } from '../services/claim.service';
import { Claim, ClaimFilters } from '../interfaces/claim.interface';
import { 
  PageableAdapter, 
  PageableResult, 
  SortDirection 
} from '../../../components/pageable-grid/pageable-adapter.interface';

@Injectable()
export class ClaimsPageableAdapter implements PageableAdapter<Claim> {
  private claimService = inject(ClaimService);
  private currentFilters: ClaimFilters = {};

  /**
   * Carga inicial de datos
   */
  loadData(): Observable<PageableResult<Claim>> {
    return this.loadClaims({ page: 1, limit: 15 });
  }

  /**
   * Cambio de página
   */
  changePage(page: number, pageSize: number): Observable<PageableResult<Claim>> {
    return this.loadClaims({ 
      ...this.currentFilters, 
      page: page + 1, // El componente usa base 0, API usa base 1
      limit: pageSize 
    });
  }

  /**
   * Cambio de ordenamiento (no implementado en API actual)
   */
  changeSort(sortField: string, sortDirection: SortDirection): Observable<PageableResult<Claim>> {
    // Por ahora retorna los datos sin ordenamiento ya que la API no lo soporta
    return this.loadClaims(this.currentFilters);
  }

  /**
   * Cambio de página y ordenamiento combinado
   */
  changePageAndSort(
    page: number, 
    pageSize: number, 
    sortField: string, 
    sortDirection: SortDirection
  ): Observable<PageableResult<Claim>> {
    return this.changePage(page, pageSize);
  }

  /**
   * Aplicar filtros específicos
   */
  applyFilters(filters: ClaimFilters): Observable<PageableResult<Claim>> {
    this.currentFilters = { ...filters };
    return this.loadClaims(this.currentFilters);
  }

  /**
   * Limpiar filtros
   */
  clearFilters(): Observable<PageableResult<Claim>> {
    this.currentFilters = {};
    return this.loadData();
  }

  /**
   * Carga los reclamos y transforma la respuesta al formato requerido
   */
  private loadClaims(filters: ClaimFilters): Observable<PageableResult<Claim>> {
    return this.claimService.getClaims(filters).pipe(
      map(response => ({
        data: response.data,
        totalElements: response.pagination.total,
        totalPages: response.pagination.totalPages,
        currentPage: (response.pagination.page - 1), // Convertir a base 0
        pageSize: response.pagination.limit,
        hasNext: response.pagination.page < response.pagination.totalPages,
        hasPrevious: response.pagination.page > 1,
        sortField: undefined, // API actual no soporta ordenamiento
        sortDirection: undefined
      }))
    );
  }

  /**
   * Refresca los datos con los filtros actuales
   */
  refresh(): Observable<PageableResult<Claim>> {
    return this.loadClaims(this.currentFilters);
  }
}