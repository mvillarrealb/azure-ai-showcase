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
   * Enviar evaluación
   */
  async onSubmitEvaluation(): Promise<void> {
    if (this.isEvaluating()) return;

    // Validaciones básicas
    if (!this.validateEvaluationForm()) {
      alert('Por favor, complete todos los campos correctamente.');
      return;
    }

    this.isEvaluating.set(true);

    try {
      const result = await this.creditManagementService.evaluateClientEligibility(this.evaluationRequest).toPromise();
      
      if (result) {
        this.evaluationResult.set(result);
        this.currentStep.set(2);
      } else {
        throw new Error('No se recibieron resultados de la evaluación');
      }
      
    } catch (error: any) {
      console.error('Error en evaluación:', error);
      alert('Error al evaluar cliente: ' + (error.error?.message || error.message || 'Error desconocido'));
    } finally {
      this.isEvaluating.set(false);
    }
  }

  /**
   * Validar formulario de evaluación
   */
  private validateEvaluationForm(): boolean {
    // Validar documento de identidad
    if (!this.evaluationRequest.identityDocument?.trim()) {
      return false;
    }

    // Validar que sea un DNI peruano válido (8 dígitos) o similar
    const documentPattern = /^\d{8,15}$/;
    if (!documentPattern.test(this.evaluationRequest.identityDocument)) {
      alert('El documento de identidad debe contener entre 8 y 15 dígitos');
      return false;
    }

    // Validar monto solicitado
    if (!this.evaluationRequest.requestedAmount || this.evaluationRequest.requestedAmount <= 0) {
      alert('El monto solicitado debe ser mayor a 0');
      return false;
    }

    return true;
  }

  /**
   * Reiniciar wizard
   */
  resetWizard(): void {
    this.currentStep.set(1);
    this.evaluationResult.set(null);
    this.isEvaluating.set(false);
    this.evaluationRequest = {
      identityDocument: '',
      requestedAmount: 0
    };
  }

  /**
   * Obtener clase CSS para el nivel de riesgo
   */
  getRiskLevelClass(): string {
    const riskLevel = this.evaluationResult()?.summary.riskLevel;
    switch (riskLevel) {
      case 'LOW':
        return 'text-green-600';
      case 'MEDIUM':
        return 'text-yellow-600';
      case 'HIGH':
        return 'text-red-600';
      default:
        return 'text-gray-600';
    }
  }

  /**
   * Formatear moneda
   */
  formatCurrency(amount: number): string {
    return this.creditManagementService.formatCurrency(amount, 'S/');
  }

  /**
   * Exportar resultados
   */
  exportResults(): void {
    const result = this.evaluationResult();
    if (!result) return;

    // Crear objeto para exportar
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

    // Crear y descargar archivo JSON
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
    
    alert('Resultados exportados exitosamente');
  }
}