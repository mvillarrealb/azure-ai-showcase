import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreditManagementService } from '../../services/credit-management.service';
import { 
  EvaluationRequest, 
  EvaluationResponse 
} from '../../interfaces/credit-management.interface';

@Component({
  selector: 'app-credit-evaluation-wizard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './credit-evaluation-wizard.html'
})
export class CreditEvaluationWizardComponent {
  private creditManagementService = inject(CreditManagementService);

  // Signals para el estado del componente
  currentStep = signal(1);
  isEvaluating = signal(false);
  evaluationResult = signal<EvaluationResponse | null>(null);

  // Signals para validación y notificaciones
  validationErrors = signal<{[key: string]: string}>({});
  notification = signal<{message: string, type: 'success' | 'error' | 'info'} | null>(null);

  // Datos del formulario de evaluación
  evaluationRequest: EvaluationRequest = {
    identityDocument: '',
    requestedAmount: 0
  };

  /**
   * Obtener título del paso actual
   */
  getStepTitle(): string {
    switch (this.currentStep()) {
      case 1:
        return 'Datos del Cliente';
      case 2:
        return 'Resultados de Evaluación';
      default:
        return '';
    }
  }

  /**
   * Mostrar notificación
   */
  private showNotification(message: string, type: 'success' | 'error' | 'info'): void {
    this.notification.set({ message, type });
    // Auto-ocultar después de 5 segundos
    setTimeout(() => {
      this.notification.set(null);
    }, 5000);
  }

  /**
   * Cerrar notificación manualmente
   */
  closeNotification(): void {
    this.notification.set(null);
  }

  /**
   * Validar campo individual
   */
  private validateField(fieldName: string): void {
    const errors = { ...this.validationErrors() };
    
    switch (fieldName) {
      case 'identityDocument':
        if (!this.evaluationRequest.identityDocument?.trim()) {
          errors[fieldName] = 'El documento de identidad es requerido';
        } else {
          const documentPattern = /^\d{8,15}$/;
          if (!documentPattern.test(this.evaluationRequest.identityDocument)) {
            errors[fieldName] = 'Debe contener entre 8 y 15 dígitos';
          } else {
            delete errors[fieldName];
          }
        }
        break;
      
      case 'requestedAmount':
        if (!this.evaluationRequest.requestedAmount || this.evaluationRequest.requestedAmount <= 0) {
          errors[fieldName] = 'El monto debe ser mayor a 0';
        } else if (this.evaluationRequest.requestedAmount > 1000000) {
          errors[fieldName] = 'El monto máximo es S/ 1,000,000';
        } else {
          delete errors[fieldName];
        }
        break;
    }
    
    this.validationErrors.set(errors);
  }

  /**
   * Manejar cambios en los campos del formulario
   */
  onFieldChange(fieldName: 'identityDocument' | 'requestedAmount'): void {
    this.validateField(fieldName);
  }

  /**
   * Enviar evaluación
   */
  async onSubmitEvaluation(): Promise<void> {
    if (this.isEvaluating()) return;

    // Validar todos los campos
    this.validateField('identityDocument');
    this.validateField('requestedAmount');

    // Verificar si hay errores de validación
    if (Object.keys(this.validationErrors()).length > 0) {
      this.showNotification('Por favor, corrija los errores en el formulario', 'error');
      return;
    }

    this.isEvaluating.set(true);

    try {
      const result = await this.creditManagementService.evaluateClient(this.evaluationRequest).toPromise();
      
      if (result) {
        this.evaluationResult.set(result);
        this.currentStep.set(2);
        this.showNotification('Evaluación completada exitosamente', 'success');
      } else {
        throw new Error('No se recibieron resultados de la evaluación');
      }
      
    } catch (error: any) {
      console.error('Error en evaluación:', error);
      const errorMessage = error.error?.message || error.message || 'Error desconocido al procesar la evaluación';
      this.showNotification(`Error: ${errorMessage}`, 'error');
    } finally {
      this.isEvaluating.set(false);
    }
  }

  /**
   * Validar formulario de evaluación (método legacy simplificado)
   */
  private validateEvaluationForm(): boolean {
    this.validateField('identityDocument');
    this.validateField('requestedAmount');
    return Object.keys(this.validationErrors()).length === 0;
  }

  /**
   * Reiniciar wizard
   */
  resetWizard(): void {
    this.currentStep.set(1);
    this.evaluationResult.set(null);
    this.isEvaluating.set(false);
    this.validationErrors.set({});
    this.notification.set(null);
    this.evaluationRequest = {
      identityDocument: '',
      requestedAmount: 0
    };
  }

  /**
   * Obtener clase CSS para el nivel de riesgo
   */
  getRiskLevelClass(): string {
    const riskLevel = this.evaluationResult()?.clientProfile?.riskLevel;
    switch (riskLevel?.toLowerCase()) {
      case 'bajo':
        return 'text-green-600';
      case 'medio':
        return 'text-yellow-600';
      case 'alto':
        return 'text-red-600';
      default:
        return 'text-gray-600';
    }
  }

  /**
   * Formatear moneda
   */
  formatCurrency(amount: number | undefined | null): string {
    return this.creditManagementService.formatCurrency(amount, 'S/');
  }

  /**
   * Formatear porcentaje
   */
  formatPercentage(rate: number | undefined | null): string {
    return this.creditManagementService.formatPercentage(rate);
  }

  /**
   * Exportar resultados
   */
  exportResults(): void {
    const result = this.evaluationResult();
    if (!result) {
      this.showNotification('No hay resultados para exportar', 'error');
      return;
    }

    try {
      const exportData = {
        evaluacion: {
          fecha: new Date().toISOString().split('T')[0],
          documento: this.evaluationRequest.identityDocument,
          montoSolicitado: this.evaluationRequest.requestedAmount
        },
        cliente: result.clientProfile,
        resumen: result.summary,
        productosRecomendados: result.eligibleProducts
      };

      const dataStr = JSON.stringify(exportData, null, 2);
      const dataBlob = new Blob([dataStr], { type: 'application/json' });
      const url = URL.createObjectURL(dataBlob);
      
      const link = document.createElement('a');
      link.href = url;
      link.download = `evaluacion_crediticia_${this.evaluationRequest.identityDocument}_${new Date().toISOString().split('T')[0]}.json`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      URL.revokeObjectURL(url);
      
      this.showNotification('Resultados exportados exitosamente', 'success');
    } catch (error) {
      console.error('Error al exportar:', error);
      this.showNotification('Error al exportar los resultados', 'error');
    }
  }
}