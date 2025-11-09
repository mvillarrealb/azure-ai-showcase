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
  private baseUrl = `${environment.personalFinance}`;

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
    // Procesar la fecha para asegurar formato ISO con timestamp
    const processedTransaction = { ...transaction };
    
    if (processedTransaction.date) {
      // Si la fecha no incluye hora, agregar hora arbitraria (12:00:00)
      const date = new Date(processedTransaction.date);
      
      // Verificar si la fecha ya incluye hora específica
      const dateString = processedTransaction.date;
      const hasTime = dateString.includes('T') && dateString.includes(':');
      
      if (!hasTime) {
        // Si solo es fecha (YYYY-MM-DD), agregar hora arbitraria
        date.setHours(12, 0, 0, 0); // 12:00:00 PM
      }
      
      // Convertir a ISO string con zona horaria
      processedTransaction.date = date.toISOString();
    }
    
    return this.http.post<Transaction>(`${this.baseUrl}/transactions`, processedTransaction);
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