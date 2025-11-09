import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  Product, 
  CreateProductRequest, 
  ProductListResponse,
  EvaluationRequest,
  EvaluationResponse,
  RankUploadRequest,
  RankBatchUploadRequest,
  RankUploadResponse,
  RankBatchUploadResponse,
  ProductFilters,
  RankListResponse,
  RankFilters
} from '../interfaces/credit-management.interface';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CreditManagementService {
  private readonly baseUrl = `${environment.creditManagement}`;

  constructor(private http: HttpClient) {}

  /**
   * Obtener lista de productos crediticios con filtros opcionales
   */
  getProducts(filters?: ProductFilters & {
    page?: number;
    size?: number;
    sort?: string;
  }): Observable<ProductListResponse> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.category) params = params.set('category', filters.category);
      if (filters.currency) params = params.set('currency', filters.currency);
      if (filters.minAmount !== undefined) params = params.set('minAmount', filters.minAmount.toString());
      if (filters.maxAmount !== undefined) params = params.set('maxAmount', filters.maxAmount.toString());
      if (filters.page !== undefined) params = params.set('page', filters.page.toString());
      if (filters.size !== undefined) params = params.set('size', filters.size.toString());
      if (filters.sort) params = params.set('sort', filters.sort);
    }

    return this.http.get<ProductListResponse>(`${this.baseUrl}/products`, { params });
  }

  /**
   * Obtener detalles de un producto crediticio específico
   */
  getProductById(productId: string): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/products/${productId}`);
  }

  /**
   * Crear un nuevo producto crediticio
   */
  createProduct(product: CreateProductRequest): Observable<Product> {
    return this.http.post<Product>(`${this.baseUrl}/products`, product);
  }

  /**
   * Evaluar elegibilidad del cliente para productos crediticios
   */
  evaluateClient(request: EvaluationRequest): Observable<EvaluationResponse> {
    return this.http.post<EvaluationResponse>(`${this.baseUrl}/products/evaluate`, request);
  }

  /**
   * Obtener lista paginada de rangos con filtros opcionales
   */
  getRanks(filters?: RankFilters & {
    page?: number;
    size?: number;
    sort?: string;
  }): Observable<RankListResponse> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.name) params = params.set('name', filters.name);
      if (filters.page !== undefined) params = params.set('page', filters.page.toString());
      if (filters.size !== undefined) params = params.set('size', filters.size.toString());
      if (filters.sort) params = params.set('sort', filters.sort);
    }

    return this.http.get<RankListResponse>(`${this.baseUrl}/ranks`, { params });
  }

  /**
   * Cargar una clasificación individual a Azure AI Search
   */
  uploadRank(rank: RankUploadRequest): Observable<RankUploadResponse> {
    return this.http.post<RankUploadResponse>(`${this.baseUrl}/ranks/upload`, rank);
  }

  /**
   * Cargar múltiples clasificaciones en lote a Azure AI Search
   */
  uploadRanksBatch(request: RankBatchUploadRequest): Observable<RankBatchUploadResponse> {
    return this.http.post<RankBatchUploadResponse>(`${this.baseUrl}/ranks/upload-batch`, request);
  }

  /**
   * Obtener categorías de productos disponibles
   * (Este método no está en la API pero lo necesitamos para el frontend)
   */
  getProductCategories(): string[] {
    return [
      'PRESTAMO_PERSONAL',
      'CREDITO_HIPOTECARIO',
      'CREDITO_VEHICULAR',
      'TARJETA_CREDITO',
      'LINEA_CREDITO'
    ];
  }

  /**
   * Obtener subcategorías por categoría
   * (Este método no está en la API pero lo necesitamos para el frontend)
   */
  getSubcategoriesByCategory(category: string): string[] {
    const subcategoriesMap: { [key: string]: string[] } = {
      'PRESTAMO_PERSONAL': ['STANDARD', 'ELITE', 'EMPRESARIAL'],
      'CREDITO_HIPOTECARIO': ['PRIMERA_VIVIENDA', 'SEGUNDA_VIVIENDA', 'CONSTRUCCION'],
      'CREDITO_VEHICULAR': ['NUEVO', 'USADO', 'COMERCIAL'],
      'TARJETA_CREDITO': ['CLASICA', 'GOLD', 'PLATINUM', 'BLACK'],
      'LINEA_CREDITO': ['PERSONAL', 'EMPRESARIAL', 'PYME']
    };
    
    return subcategoriesMap[category] || [];
  }

  /**
   * Formatear moneda
   */
  formatCurrency(amount: number, currency: 'S/' | 'USD'): string {
    const formatter = new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: currency === 'S/' ? 'PEN' : 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
    
    return formatter.format(amount);
  }

  /**
   * Formatear porcentaje
   */
  formatPercentage(rate: number): string {
    return `${rate.toFixed(2)}%`;
  }
}