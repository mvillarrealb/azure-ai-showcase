/**
 * Interfaces para el módulo de finanzas personales
 * Basadas exactamente en el OpenAPI spec v1.0.0
 */

export interface Category {
  id: string;
  name: string;
  type: 'income' | 'expense';
}

export interface Transaction {
  id: string;
  amount: number;
  date: string; // ISO 8601 format
  categoryId: string;
  categoryName: string;
  categoryType: 'income' | 'expense';
  description: string;
}

export interface TransactionCreate {
  amount: number;
  date: string; // ISO 8601 format
  categoryId: string;
  description: string;
}

export interface Pagination {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  itemsPerPage: number;
}

export interface TransactionsResponse {
  data: Transaction[];
  pagination: Pagination;
}

export interface InvoiceLineItem {
  description: string;
  quantity: number;
  unitPrice: number | null;
  totalPrice: number;
}

export interface InvoiceAnalysis {
  invoiceNumber: string;
  date: string; // ISO 8601 format
  totalAmount: number;
  vendor: string;
  lineItems: InvoiceLineItem[];
}

export interface CategoryBreakdown {
  categoryId: string;
  categoryName: string;
  categoryType: 'income' | 'expense';
  totalAmount: number;
}

export interface MonthlyReport {
  month: string; // YYYY-MM format
  totalIncome: number;
  totalExpense: number;
  netSavings: number;
  categoryBreakdown: CategoryBreakdown[];
}