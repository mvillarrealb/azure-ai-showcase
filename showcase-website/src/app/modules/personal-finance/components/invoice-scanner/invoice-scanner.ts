import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PersonalFinanceService } from '../../services/personal-finance.service';
import { InvoiceAnalysis, TransactionCreate, Category } from '../../interfaces/personal-finance.interface';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

@Component({
  selector: 'app-invoice-scanner',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './invoice-scanner.html',
  styleUrl: './invoice-scanner.scss'
})
export class InvoiceScannerComponent {
  private personalFinanceService = inject(PersonalFinanceService);

  // State signals
  isDragOver = signal<boolean>(false);
  isUploading = signal<boolean>(false);
  scanResult = signal<InvoiceAnalysis | null>(null);
  errorMessage = signal<string>('');
  successMessage = signal<string>('');
  isCreatingTransaction = signal<boolean>(false);
  selectedCategoryId = signal<string>('');
  
  // Categories for transaction creation
  categories = signal<Category[]>([]);

  constructor() {
    this.loadCategories();
  }

  private loadCategories() {
    this.personalFinanceService.getCategories().subscribe({
      next: (categories) => {
        this.categories.set(categories);
        // Pre-seleccionar la primera categoría de gasto disponible
        const expenseCategories = this.getExpenseCategories();
        if (expenseCategories.length > 0) {
          this.selectedCategoryId.set(expenseCategories[0].id);
        }
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }

  getExpenseCategories(): Category[] {
    return this.categories().filter(cat => cat.type === 'expense');
  }

  getSelectedCategoryName(): string {
    const selectedId = this.selectedCategoryId();
    if (!selectedId) return '';
    const category = this.categories().find(cat => cat.id === selectedId);
    return category?.name || '';
  }

  onCategoryChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    this.selectedCategoryId.set(select.value);
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver.set(true);
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver.set(false);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver.set(false);
    
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.processFile(files[0]);
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) {
      this.processFile(file);
    }
  }

  private processFile(file: File) {
    // Reset states
    this.errorMessage.set('');
    this.successMessage.set('');
    this.scanResult.set(null);

    // Validate file
    if (!file.type.includes('pdf')) {
      this.errorMessage.set('Por favor selecciona un archivo PDF válido.');
      return;
    }

    if (file.size > 10 * 1024 * 1024) { // 10MB
      this.errorMessage.set('El archivo es muy grande. El tamaño máximo es de 10MB.');
      return;
    }

    // Start upload
    this.isUploading.set(true);

    this.personalFinanceService.scanInvoice(file).subscribe({
      next: (result) => {
        this.isUploading.set(false);
        this.scanResult.set(result);
      },
      error: (error) => {
        this.isUploading.set(false);
        console.error('Error scanning invoice:', error);
        
        let errorMsg = 'Error al procesar el documento. ';
        if (error.status === 400) {
          errorMsg += 'El archivo no se pudo procesar correctamente.';
        } else if (error.status >= 500) {
          errorMsg += 'Error interno del servidor. Inténtalo más tarde.';
        } else {
          errorMsg += 'Por favor, inténtalo nuevamente.';
        }
        
        this.errorMessage.set(errorMsg);
      }
    });
  }

  registerTransaction() {
    const result = this.scanResult();
    const categoryId = this.selectedCategoryId();
    
    if (!result) return;

    if (!categoryId) {
      this.errorMessage.set('Por favor selecciona una categoría de gasto.');
      return;
    }

    const transaction: TransactionCreate = {
      amount: -Math.abs(result.totalAmount), // Negative for expense
      date: result.date,
      categoryId: categoryId,
      description: `${result.vendor} - ${result.invoiceNumber}`
    };

    this.isCreatingTransaction.set(true);

    this.personalFinanceService.createTransaction(transaction).subscribe({
      next: (createdTransaction) => {
        this.isCreatingTransaction.set(false);
        const categoryName = this.getSelectedCategoryName();
        this.scanResult.set(null);
        this.successMessage.set(
          `Transacción registrada por ${this.formatCurrency(Math.abs(createdTransaction.amount))} en categoría "${categoryName}"`
        );
      },
      error: (error) => {
        this.isCreatingTransaction.set(false);
        console.error('Error creating transaction:', error);
        this.errorMessage.set('Error al registrar la transacción. Inténtalo nuevamente.');
      }
    });
  }

  resetScanner() {
    this.scanResult.set(null);
    this.errorMessage.set('');
    this.successMessage.set('');
    this.isUploading.set(false);
    this.isDragOver.set(false);
    this.selectedCategoryId.set('');
    
    // Re-establecer la primera categoría de gasto disponible
    const expenseCategories = this.getExpenseCategories();
    if (expenseCategories.length > 0) {
      this.selectedCategoryId.set(expenseCategories[0].id);
    }
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '';
    try {
      return format(new Date(dateString), 'dd/MM/yyyy', { locale: es });
    } catch {
      return dateString;
    }
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN'
    }).format(amount);
  }
}