import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { 
  PageableAdapter, 
  PageableResult, 
  SortDirection 
} from '../../../components/pageable-grid/pageable-adapter.interface';
import { PersonalFinanceService } from '../services/personal-finance.service';
import { Transaction } from '../interfaces/personal-finance.interface';

@Injectable()
export class TransactionsPageableAdapter implements PageableAdapter<Transaction> {
  private personalFinanceService = inject(PersonalFinanceService);
  
  private currentFilters: {
    categoryId?: string;
    startDate?: string;
    endDate?: string;
  } = {};

  private currentPageSize = 10;

  loadData(): Observable<PageableResult<Transaction>> {
    return this.personalFinanceService.getTransactions({
      page: 1,
      limit: this.currentPageSize,
      ...this.currentFilters
    }).pipe(
      map(response => ({
        data: response.data,
        totalElements: response.pagination.totalItems,
        totalPages: response.pagination.totalPages,
        currentPage: response.pagination.currentPage - 1, // API usa 1-indexed, grid usa 0-indexed
        pageSize: this.currentPageSize,
        hasNext: response.pagination.currentPage < response.pagination.totalPages,
        hasPrevious: response.pagination.currentPage > 1
      }))
    );
  }

  changePage(page: number, pageSize: number): Observable<PageableResult<Transaction>> {
    this.currentPageSize = pageSize;
    return this.personalFinanceService.getTransactions({
      page: page + 1, // API usa 1-indexed
      limit: pageSize,
      ...this.currentFilters
    }).pipe(
      map(response => ({
        data: response.data,
        totalElements: response.pagination.totalItems,
        totalPages: response.pagination.totalPages,
        currentPage: page,
        pageSize: pageSize,
        hasNext: response.pagination.currentPage < response.pagination.totalPages,
        hasPrevious: response.pagination.currentPage > 1
      }))
    );
  }

  changeSort(sortField: string, sortDirection: SortDirection): Observable<PageableResult<Transaction>> {
    // La API no soporta ordenamiento según el OpenAPI, retornamos los datos actuales
    return this.loadData().pipe(
      map(result => ({
        ...result,
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
  ): Observable<PageableResult<Transaction>> {
    this.currentPageSize = pageSize;
    return this.personalFinanceService.getTransactions({
      page: page + 1, // API usa 1-indexed
      limit: pageSize,
      ...this.currentFilters
    }).pipe(
      map(response => ({
        data: response.data,
        totalElements: response.pagination.totalItems,
        totalPages: response.pagination.totalPages,
        currentPage: page,
        pageSize: pageSize,
        hasNext: response.pagination.currentPage < response.pagination.totalPages,
        hasPrevious: response.pagination.currentPage > 1,
        sortField,
        sortDirection
      }))
    );
  }

  /**
   * Método para actualizar filtros
   */
  setFilters(filters: {
    categoryId?: string;
    startDate?: string;
    endDate?: string;
  }): void {
    this.currentFilters = filters;
  }
}