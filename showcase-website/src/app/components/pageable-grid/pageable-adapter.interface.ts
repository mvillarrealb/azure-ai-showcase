import { Observable } from 'rxjs';

/**
 * Interfaz que deben implementar los servicios que quieran usar PageableGrid
 * Abstrae los llamados paginables y de ordenamiento
 */
export interface PageableAdapter<T> {
  /**
   * Carga inicial de datos con paginación por defecto
   */
  loadData(): Observable<PageableResult<T>>;
  
  /**
   * Cambio de página
   * @param page Número de página (base 0)
   * @param pageSize Tamaño de página
   */
  changePage(page: number, pageSize: number): Observable<PageableResult<T>>;
  
  /**
   * Cambio de ordenamiento
   * @param sortField Campo por el que ordenar
   * @param sortDirection Dirección del ordenamiento ('asc' | 'desc')
   */
  changeSort(sortField: string, sortDirection: SortDirection): Observable<PageableResult<T>>;
  
  /**
   * Cambio de página y ordenamiento combinado
   * @param page Número de página
   * @param pageSize Tamaño de página
   * @param sortField Campo por el que ordenar
   * @param sortDirection Dirección del ordenamiento
   */
  changePageAndSort(
    page: number, 
    pageSize: number, 
    sortField: string, 
    sortDirection: SortDirection
  ): Observable<PageableResult<T>>;
}

/**
 * Resultado paginable que debe retornar el adapter
 */
export interface PageableResult<T> {
  data: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
  sortField?: string;
  sortDirection?: SortDirection;
}

/**
 * Dirección del ordenamiento
 */
export type SortDirection = 'asc' | 'desc';

/**
 * Configuración de columna para el grid
 */
export interface GridColumn {
  key: string;
  label: string;
  sortable?: boolean;
  type?: 'text' | 'number' | 'date' | 'currency' | 'status' | 'email';
  width?: string;
  align?: 'left' | 'center' | 'right';
  format?: (value: any) => string;
  multiline?: boolean; // Permite saltos de línea en el contenido
}

/**
 * Configuración del PageableGrid
 */
export interface PageableGridConfig {
  pageSize?: number;
  pageSizeOptions?: number[];
  showPageSizeSelector?: boolean;
  defaultSortField?: string;
  defaultSortDirection?: SortDirection;
  enableMultiSort?: boolean;
  loadingText?: string;
  emptyText?: string;
}