/**
 * Interfaces para gestión de reclamos basadas en OpenAPI spec
 */

/**
 * Entidad principal de reclamo
 */
export interface Claim {
  id: string;
  date: string;
  amount: number;
  identityDocument: string;
  description: string;
  reason: string;
  subReason: string;
  status: ClaimStatus;
  comments?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * Estados posibles de un reclamo
 */
export type ClaimStatus = 'open' | 'inProgress' | 'resolved';

/**
 * Descripción de estados para UI
 */
export const ClaimStatusLabels: Record<ClaimStatus, string> = {
  open: 'Abierto',
  inProgress: 'En Proceso',
  resolved: 'Resuelto'
};

/**
 * Request para crear un nuevo reclamo
 */
export interface CreateClaimRequest {
  date: string;
  amount: number;
  identityDocument: string;
  description: string;
  reason: string;
  subReason: string;
}

/**
 * Request para resolver un reclamo
 */
export interface ResolveClaimRequest {
  comments: string;
}

/**
 * Respuesta de lista paginada de reclamos
 */
export interface ClaimListResponse {
  data: Claim[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}

/**
 * Respuesta de importación de reclamos
 */
export interface ImportResponse {
  message: string;
  totalProcessed: number;
  successful: number;
  failed: number;
  claimsCreated: ImportedClaim[];
  errors: Array<{ row: number; error: string; }>;
}

/**
 * Reclamo importado con datos básicos y motivos asignados por IA
 */
export interface ImportedClaim {
  id: string;
  identityDocument: string;
  amount: number;
  reason: string;
  subReason: string;
}

/**
 * Response genérica de error
 */
export interface ErrorResponse {
  error: string;
  message: string;
  details?: Array<{ field: string; error: string; }>;
}

/**
 * Filtros para búsqueda de reclamos
 */
export interface ClaimFilters {
  identityDocument?: string;
  status?: ClaimStatus;
  page?: number;
  limit?: number;
}