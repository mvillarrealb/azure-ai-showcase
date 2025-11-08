import { Component, OnInit, inject, signal, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PersonalFinanceService } from '../../services/personal-finance.service';
import { Category, TransactionCreate } from '../../interfaces/personal-finance.interface';

@Component({
  selector: 'app-personal-finance-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './personal-finance-form.html',
  styleUrl: './personal-finance-form.scss',
})
export class PersonalFinanceFormComponent implements OnInit {
  @Input() isModal: boolean = false;
  @Output() transactionCreated = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  private personalFinanceService = inject(PersonalFinanceService);
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);

  // Signals para estado reactivo
  categories = signal<Category[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Reactive Form
  transactionForm: FormGroup;

  constructor() {
    this.transactionForm = this.formBuilder.group({
      amount: [null, [Validators.required, Validators.min(0.01)]],
      date: [this.getTodayString(), [Validators.required]],
      categoryId: ['', [Validators.required]],
      description: ['', [Validators.required, Validators.maxLength(500)]]
    });
  }

  async ngOnInit() {
    await this.loadCategories();
  }

  private async loadCategories() {
    try {
      const categories = await this.personalFinanceService.getCategories().toPromise();
      this.categories.set(categories || []);
    } catch (error) {
      console.error('Error loading categories:', error);
    }
  }

  private getTodayString(): string {
    return new Date().toISOString().split('T')[0];
  }

  async onSubmit() {
    if (this.transactionForm.valid) {
      this.isLoading.set(true);
      this.clearMessages();

      try {
        const formData = this.transactionForm.value;
        const transactionCreate: TransactionCreate = {
          amount: Number(formData.amount),
          date: formData.date,
          categoryId: formData.categoryId,
          description: formData.description.trim()
        };

        const result = await this.personalFinanceService.createTransaction(transactionCreate).toPromise();
        
        if (result) {
          this.successMessage.set('Transacción creada exitosamente');
          this.transactionForm.reset({
            amount: null,
            date: this.getTodayString(),
            categoryId: '',
            description: ''
          });

          if (this.isModal) {
            // Si está en modal, emitir evento y cerrar
            setTimeout(() => {
              this.transactionCreated.emit();
            }, 1000);
          } else {
            // Si no está en modal, navegar después de 2 segundos
            setTimeout(() => {
              this.router.navigate(['/personal-finance/transacciones']);
            }, 2000);
          }
        }
      } catch (error: any) {
        console.error('Error creating transaction:', error);
        this.errorMessage.set(error?.error?.message || 'Error al crear la transacción');
      } finally {
        this.isLoading.set(false);
      }
    } else {
      this.markFormGroupTouched();
    }
  }

  onCancel() {
    if (this.isModal) {
      this.cancelled.emit();
    } else {
      this.router.navigate(['/personal-finance/transacciones']);
    }
  }

  clearMessages() {
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.transactionForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.transactionForm.get(fieldName);
    if (field && field.errors) {
      if (field.errors['required']) return 'Este campo es obligatorio';
      if (field.errors['min']) return 'El monto debe ser mayor a 0';
      if (field.errors['maxlength']) return 'Máximo 500 caracteres';
    }
    return '';
  }

  private markFormGroupTouched() {
    Object.keys(this.transactionForm.controls).forEach(key => {
      const control = this.transactionForm.get(key);
      if (control) {
        control.markAsTouched();
      }
    });
  }

  getCategoryType(categoryId: string): 'income' | 'expense' | null {
    const category = this.categories().find(c => c.id === categoryId);
    return category?.type || null;
  }
}