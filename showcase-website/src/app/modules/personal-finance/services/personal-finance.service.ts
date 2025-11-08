import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { 
  Category, 
  Transaction, 
  TransactionCreate, 
  TransactionsResponse, 
  InvoiceAnalysis, 
  MonthlyReport 
} from '../interfaces/personal-finance.interface';

@Injectable({
  providedIn: 'root'
})
export class PersonalFinanceService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}`;

  /**
   * GET /categories
   * Obtener todas las categorías
   */
  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.baseUrl}/categories`);
  }

  /**
   * GET /transactions
   * Obtener transacciones con paginación y filtros
   */
  getTransactions(params: {
    page?: number;
    limit?: number;
    categoryId?: string;
    startDate?: string;
    endDate?: string;
  } = {}): Observable<TransactionsResponse> {
    let httpParams = new HttpParams();
    
    if (params.page) httpParams = httpParams.set('page', params.page.toString());
    if (params.limit) httpParams = httpParams.set('limit', params.limit.toString());
    if (params.categoryId) httpParams = httpParams.set('categoryId', params.categoryId);
    if (params.startDate) httpParams = httpParams.set('startDate', params.startDate);
    if (params.endDate) httpParams = httpParams.set('endDate', params.endDate);

    return this.http.get<TransactionsResponse>(`${this.baseUrl}/transactions`, { params: httpParams });
  }

  /**
   * POST /transactions
   * Crear una nueva transacción
   */
  createTransaction(transaction: TransactionCreate): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.baseUrl}/transactions`, transaction);
  }

  /**
   * POST /invoices/scan
   * Escanear factura PDF
   */
  scanInvoice(file: File): Observable<InvoiceAnalysis> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<InvoiceAnalysis>(`${this.baseUrl}/invoices/scan`, formData);
  }

  /**
   * GET /reports/monthly
   * Obtener reporte mensual
   */
  getMonthlyReport(month: string): Observable<MonthlyReport> {
    const params = new HttpParams().set('month', month);
    return this.http.get<MonthlyReport>(`${this.baseUrl}/reports/monthly`, { params });
  }
}