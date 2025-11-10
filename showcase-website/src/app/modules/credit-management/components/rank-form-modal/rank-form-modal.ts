import { Component, inject, signal, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreditManagementService } from '../../services/credit-management.service';
import { RanksPageableAdapter } from '../../adapters/ranks-pageable.adapter';
import { RankUploadRequest, Rank } from '../../interfaces/credit-management.interface';

@Component({
  selector: 'app-rank-form-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rank-form-modal.html'
})
export class RankFormModalComponent {
  private creditManagementService = inject(CreditManagementService);

  @Input() set visible(value: boolean) {
    this.isVisible.set(value);
  }

  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() rankCreated = new EventEmitter<void>();

  // Signals para el estado del componente
  isVisible = signal(false);
  isSubmitting = signal(false);
  hasIdError = signal(false);
  isIdValid = signal(false);

  // Datos del formulario
  formData: RankUploadRequest = {
    id: '',
    name: '',
    description: ''
  };

  /**
   * Validar entrada del ID en tiempo real
   */
  validateIdInput(event: any): void {
    let value = event.target.value;
    
    // Auto-convertir a mayúsculas y limpiar caracteres inválidos
    const cleanValue = value.toUpperCase().replace(/[^A-Z0-9_]/g, '');
    
    // Si el valor cambió, actualizar el input
    if (cleanValue !== value) {
      this.formData.id = cleanValue;
      event.target.value = cleanValue;
      value = cleanValue;
    }
    
    // Validar patrón: solo mayúsculas, números y guión bajo
    const isValidPattern = /^[A-Z0-9_]*$/.test(value);
    
    // Validar longitud mínima y que no termine o empiece con _
    const hasValidLength = value.length >= 2;
    const hasValidFormat = value.length === 0 || (!value.startsWith('_') && !value.endsWith('_'));
    
    // Validar que tenga contenido meaningful (no solo guiones bajos)
    const hasMeaningfulContent = /[A-Z0-9]/.test(value);
    
    const isValid = isValidPattern && hasValidLength && hasValidFormat && hasMeaningfulContent;
    
    this.hasIdError.set(!isValidPattern || (value.length > 0 && (!hasValidFormat || !hasMeaningfulContent)));
    this.isIdValid.set(isValid && value.length >= 2);
  }

  /**
   * Verificar si el formulario es válido
   */
  isFormValid(): boolean {
    const { id, name, description } = this.formData;
    
    // Campos obligatorios
    if (!id?.trim() || !name?.trim() || !description?.trim()) {
      return false;
    }
    
    // ID válido
    const idPattern = /^[A-Z0-9_]+$/;
    if (!idPattern.test(id) || id.length < 2) {
      return false;
    }
    
    // Descripción mínima
    if (description.trim().length < 20) {
      return false;
    }
    
    return true;
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
    if (!this.isFormValid()) {
      alert('Por favor, complete todos los campos correctamente.');
      return;
    }

    this.isSubmitting.set(true);

    try {
      // Llamar al servicio para crear el rango en Azure AI Search
      const result = await this.creditManagementService.uploadRank(this.formData).toPromise();
      
      if (result?.success) {
        // También agregar al adapter local para que aparezca en la grilla inmediatamente
        // En un caso real esto se haría con una recarga desde la API
        const ranksAdapter = inject(RanksPageableAdapter);
        
        // Refrescar el adaptador para mostrar el nuevo rango
        ranksAdapter.refresh();
        
        // Éxito
        alert('Rango creado exitosamente y sincronizado con Azure AI Search');
        this.rankCreated.emit();
        this.close();
      } else {
        throw new Error(result?.message || 'Error al crear el rango');
      }
      
    } catch (error: any) {
      console.error('Error al crear rango:', error);
      alert('Error al crear el rango: ' + (error.error?.message || error.message || 'Error desconocido'));
    } finally {
      this.isSubmitting.set(false);
    }
  }

  /**
   * Resetear formulario
   */
  private resetForm(): void {
    this.formData = {
      id: '',
      name: '',
      description: ''
    };
    this.hasIdError.set(false);
    this.isIdValid.set(false);
  }
}