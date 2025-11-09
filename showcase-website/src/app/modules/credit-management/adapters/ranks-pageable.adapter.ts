import { Injectable, inject } from '@angular/core';
import { Observable, map, of } from 'rxjs';
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
  
  // Datos mock para los rangos ya que la API solo permite upload
  // En un caso real estos vendrían de un endpoint GET /ranks
  private mockRanks: Rank[] = [
    {
      id: 'ORO',
      name: 'ORO',
      description: 'Cliente premium con ingresos sólidos entre S/6,000 - S/12,000 mensuales. Profesional universitario, ejecutivo medio, empresario con negocio establecido 3+ años.',
      createdAt: '2024-01-15',
      updatedAt: '2024-01-15'
    },
    {
      id: 'PLATA',
      name: 'PLATA',
      description: 'Cliente con perfil medio-alto, ingresos entre S/3,000 - S/6,000 mensuales. Profesional técnico, empleado con estabilidad laboral, pequeño empresario.',
      createdAt: '2024-01-15',
      updatedAt: '2024-01-15'
    },
    {
      id: 'BRONCE',
      name: 'BRONCE',
      description: 'Cliente con perfil medio, ingresos entre S/1,500 - S/3,000 mensuales. Empleado dependiente, trabajador independiente con ingresos regulares.',
      createdAt: '2024-01-15',
      updatedAt: '2024-01-15'
    },
    {
      id: 'BASICO',
      name: 'BÁSICO',
      description: 'Cliente inicial con ingresos entre S/930 - S/1,500 mensuales. Primer empleo, trabajador dependiente con poco historial crediticio.',
      createdAt: '2024-01-15',
      updatedAt: '2024-01-15'
    },
    {
      id: 'PREMIUM',
      name: 'PREMIUM',
      description: 'Cliente VIP con ingresos superiores a S/12,000 mensuales. Alto ejecutivo, empresario consolidado, inversionista con patrimonio significativo.',
      createdAt: '2024-01-15',
      updatedAt: '2024-01-15'
    }
  ];

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
    // Aplicar filtros
    let filteredData = [...this.mockRanks];
    
    if (this.currentFilters.name) {
      const nameFilter = this.currentFilters.name.toLowerCase();
      filteredData = filteredData.filter(rank => 
        rank.name.toLowerCase().includes(nameFilter) ||
        rank.description.toLowerCase().includes(nameFilter)
      );
    }

    // Aplicar ordenamiento
    if (sortField && sortDirection) {
      filteredData.sort((a, b) => {
        const aValue = (a as any)[sortField];
        const bValue = (b as any)[sortField];
        
        if (aValue < bValue) return sortDirection === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortDirection === 'asc' ? 1 : -1;
        return 0;
      });
    }

    // Calcular paginación
    const totalElements = filteredData.length;
    const totalPages = Math.ceil(totalElements / pageSize);
    const startIndex = page * pageSize;
    const endIndex = Math.min(startIndex + pageSize, totalElements);
    const pageData = filteredData.slice(startIndex, endIndex);

    const result: PageableResult<Rank> = {
      data: pageData,
      totalElements,
      totalPages,
      currentPage: page,
      pageSize,
      hasNext: page < totalPages - 1,
      hasPrevious: page > 0,
      sortField,
      sortDirection
    };

    return of(result);
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
   * Agregar nuevo rango a los datos mock
   * En un caso real esto triggearía una recarga desde la API
   */
  addRank(rank: Rank): void {
    this.mockRanks.unshift({
      ...rank,
      createdAt: new Date().toISOString().split('T')[0],
      updatedAt: new Date().toISOString().split('T')[0]
    });
  }

  /**
   * Obtener todos los rangos (para uso en otros componentes)
   */
  getAllRanks(): Rank[] {
    return [...this.mockRanks];
  }
}