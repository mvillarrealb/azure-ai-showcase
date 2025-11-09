import { Component, inject, signal, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreditManagementService } from '../../services/credit-management.service';
import { CreateProductRequest } from '../../interfaces/credit-management.interface';

@Component({
  selector: 'app-product-form-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-form-modal.html'
})
export class ProductFormModalComponent {
  private creditManagementService = inject(CreditManagementService);

  @Input() set visible(value: boolean) {
    this.isVisible.set(value);
  }

  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() productCreated = new EventEmitter<void>();

  // Signals para el estado del componente
  isVisible = signal(false);
  isSubmitting = signal(false);

  // Datos del formulario
  formData: CreateProductRequest = {
    id: '',
    name: '',
    description: '',
    category: '',
    subcategory: '',
    minimumAmount: 0,
    maximumAmount: 0,
    currency: 'S/',
    term: '',
    minimumRate: 0,
    maximumRate: 0,
    requirements: [''],
    features: [''],
    benefits: [''],
    active: true
  };

  // Categorías disponibles
  categories: string[] = this.creditManagementService.getProductCategories();
  
  // Subcategorías disponibles basadas en la categoría seleccionada
  availableSubcategories: string[] = [];

  /**
   * Manejar cambio de categoría
   */
  onCategoryChange(): void {
    this.availableSubcategories = this.creditManagementService.getSubcategoriesByCategory(this.formData.category);
    this.formData.subcategory = ''; // Limpiar subcategoría cuando cambia la categoría
  }

  /**
   * Cerrar modal
   */
  close(): void {
    this.isVisible.set(false);
    this.visibleChange.emit(false);
    this.resetForm();
  }

  /**
   * Enviar formulario
   */
  async onSubmit(): Promise<void> {
    if (this.isSubmitting()) return;

    // Validaciones básicas
    if (!this.validateForm()) {
      alert('Por favor, complete todos los campos obligatorios correctamente.');
      return;
    }

    this.isSubmitting.set(true);

    try {
      // Limpiar arrays vacíos
      const cleanFormData = {
        ...this.formData,
        requirements: this.formData.requirements.filter(req => req.trim() !== ''),
        features: this.formData.features.filter(feature => feature.trim() !== ''),
        benefits: this.formData.benefits.filter(benefit => benefit.trim() !== '')
      };

      await this.creditManagementService.createProduct(cleanFormData).toPromise();
      
      // Éxito
      alert('Producto creado exitosamente');
      this.productCreated.emit();
      this.close();
      
    } catch (error: any) {
      console.error('Error al crear producto:', error);
      alert('Error al crear el producto: ' + (error.error?.message || error.message || 'Error desconocido'));
    } finally {
      this.isSubmitting.set(false);
    }
  }

  /**
   * Validar formulario
   */
  private validateForm(): boolean {
    const required = [
      'id', 'name', 'description', 'category', 'subcategory', 
      'currency', 'term'
    ];

    for (const field of required) {
      if (!this.formData[field as keyof CreateProductRequest]) {
        return false;
      }
    }

    // Validar montos
    if (this.formData.minimumAmount <= 0 || this.formData.maximumAmount <= 0) {
      return false;
    }

    if (this.formData.minimumAmount >= this.formData.maximumAmount) {
      alert('El monto mínimo debe ser menor al monto máximo');
      return false;
    }

    // Validar tasas
    if (this.formData.minimumRate <= 0 || this.formData.maximumRate <= 0) {
      return false;
    }

    if (this.formData.minimumRate > this.formData.maximumRate) {
      alert('La tasa mínima debe ser menor o igual a la tasa máxima');
      return false;
    }

    // Validar que haya al menos un elemento en cada array
    const hasRequirements = this.formData.requirements.some(req => req.trim() !== '');
    const hasFeatures = this.formData.features.some(feature => feature.trim() !== '');
    const hasBenefits = this.formData.benefits.some(benefit => benefit.trim() !== '');

    if (!hasRequirements || !hasFeatures || !hasBenefits) {
      alert('Debe agregar al menos un requisito, una característica y un beneficio');
      return false;
    }

    return true;
  }

  /**
   * Resetear formulario
   */
  private resetForm(): void {
    this.formData = {
      id: '',
      name: '',
      description: '',
      category: '',
      subcategory: '',
      minimumAmount: 0,
      maximumAmount: 0,
      currency: 'S/',
      term: '',
      minimumRate: 0,
      maximumRate: 0,
      requirements: [''],
      features: [''],
      benefits: [''],
      active: true
    };
    this.availableSubcategories = [];
  }

  // ========================================
  // MÉTODOS PARA MANEJAR LISTAS DINÁMICAS
  // ========================================

  addRequirement(): void {
    this.formData.requirements.push('');
  }

  removeRequirement(index: number): void {
    this.formData.requirements.splice(index, 1);
    if (this.formData.requirements.length === 0) {
      this.formData.requirements.push('');
    }
  }

  addFeature(): void {
    this.formData.features.push('');
  }

  removeFeature(index: number): void {
    this.formData.features.splice(index, 1);
    if (this.formData.features.length === 0) {
      this.formData.features.push('');
    }
  }

  addBenefit(): void {
    this.formData.benefits.push('');
  }

  removeBenefit(index: number): void {
    this.formData.benefits.splice(index, 1);
    if (this.formData.benefits.length === 0) {
      this.formData.benefits.push('');
    }
  }
}