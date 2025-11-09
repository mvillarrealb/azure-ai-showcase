import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  Claim, 
  ClaimListResponse, 
  CreateClaimRequest, 
  ResolveClaimRequest, 
  ImportResponse, 
  ClaimFilters 
} from '../interfaces/claim.interface';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ClaimService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/claims`;

  /**
   * Obtiene lista paginada de reclamos con filtros opcionales
   */
  getClaims(filters: ClaimFilters = {}): Observable<ClaimListResponse> {
    let params = new HttpParams();
    
    if (filters.identityDocument) {
      params = params.append('identityDocument', filters.identityDocument);
    }
    if (filters.status) {
      params = params.append('status', filters.status);
    }
    if (filters.page) {
      params = params.append('page', filters.page.toString());
    }
    if (filters.limit) {
      params = params.append('limit', filters.limit.toString());
    }

    return this.http.get<ClaimListResponse>(this.baseUrl, { params });
  }

  /**
   * Obtiene un reclamo específico por ID
   */
  getClaimById(id: string): Observable<Claim> {
    return this.http.get<Claim>(`${this.baseUrl}/${id}`);
  }

  /**
   * Crea un nuevo reclamo
   */
  createClaim(claim: CreateClaimRequest): Observable<Claim> {
    return this.http.post<Claim>(this.baseUrl, claim);
  }

  /**
   * Marca un reclamo como resuelto
   */
  resolveClaim(id: string, resolution: ResolveClaimRequest): Observable<Claim> {
    return this.http.post<Claim>(`${this.baseUrl}/${id}/resolve`, resolution);
  }

  /**
   * Importa reclamos desde archivo Excel
   */
  importClaims(file: File): Observable<ImportResponse> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<ImportResponse>(`${this.baseUrl}/import`, formData);
  }
}