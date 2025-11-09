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

  // Datos del formulario
  formData: RankUploadRequest = {
    id: '',
    name: '',
    description: ''
  };

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
      alert('Por favor, complete todos los campos obligatorios.');
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
        const newRank: Rank = {
          ...this.formData,
          createdAt: new Date().toISOString().split('T')[0],
          updatedAt: new Date().toISOString().split('T')[0]
        };
        ranksAdapter.addRank(newRank);
        
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
   * Validar formulario
   */
  private validateForm(): boolean {
    const required = ['id', 'name', 'description'];

    for (const field of required) {
      if (!this.formData[field as keyof RankUploadRequest]?.trim()) {
        return false;
      }
    }

    // Validación adicional para ID (solo mayúsculas, números y guiones)
    const idPattern = /^[A-Z0-9_-]+$/;
    if (!idPattern.test(this.formData.id)) {
      alert('El ID debe contener solo letras mayúsculas, números, guiones y guiones bajos');
      return false;
    }

    // Validación de longitud mínima para descripción
    if (this.formData.description.trim().length < 20) {
      alert('La descripción debe tener al menos 20 caracteres para un mejor análisis semántico');
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
      description: ''
    };
  }
}