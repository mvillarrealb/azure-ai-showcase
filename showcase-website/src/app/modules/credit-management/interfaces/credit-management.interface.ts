// Interfaces basadas en el OpenAPI spec de credit-management.yaml

export interface Product {
  id: string;
  name: string;
  description: string;
  category: string;
  subcategory: string;
  minimumAmount: number;
  maximumAmount: number;
  currency: 'S/' | 'USD';
  term: string;
  minimumRate: number;
  maximumRate: number;
  requirements: string[];
  features: string[];
  benefits: string[];
}

export interface CreateProductRequest {
  id: string;
  name: string;
  description: string;
  category: string;
  subcategory: string;
  minimumAmount: number;
  maximumAmount: number;
  currency: 'S/' | 'USD';
  term: string;
  minimumRate: number;
  maximumRate: number;
  requirements: string[];
  features: string[];
  benefits: string[];
  active?: boolean;
}

export interface ProductListResponse {
  data: Product[];
  total: number;
  totalPages?: number;
  currentPage?: number;
}

export interface EvaluationRequest {
  identityDocument: string;
  requestedAmount: number;
}

export interface ClientProfile {
  identityDocument: string;
  name?: string;
  income?: number;
  creditScore?: number;
  employmentStatus?: string;
  rank?: string;
}

export interface EligibleProduct {
  productId: string;
  productName: string;
  eligibilityScore: number;
  recommendedAmount: number;
  recommendedRate: number;
  conditions: string[];
}

export interface EvaluationSummary {
  overallScore: number;
  recommendation: string;
  approvalProbability: number;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
}

export interface EvaluationResponse {
  clientProfile: ClientProfile;
  eligibleProducts: EligibleProduct[];
  summary: EvaluationSummary;
}

export interface RankUploadRequest {
  id: string;
  name: string;
  description: string;
}

export interface RankBatchUploadRequest {
  ranks: RankUploadRequest[];
}

export interface RankUploadResponse {
  success: boolean;
  message: string;
  rankId?: string;
  rankName?: string;
}

export interface RankBatchUploadResponse {
  success: boolean;
  message: string;
  totalRanks: number;
  successfulUploads: number;
  failedUploads: number;
}

export interface ErrorResponse {
  error: string;
  message: string;
  details?: string[];
}

// Interfaces adicionales para el manejo en el frontend
export interface Rank {
  id: string;
  name: string;
  description: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProductFilters {
  category?: string;
  currency?: 'S/' | 'USD';
  minAmount?: number;
  maxAmount?: number;
}

export interface RankFilters {
  name?: string;
}

export interface RankListResponse {
  data: Rank[];
  total: number;
  totalPages: number;
  currentPage: number;
}