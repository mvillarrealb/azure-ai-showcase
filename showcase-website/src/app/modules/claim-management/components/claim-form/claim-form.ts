import { Component, OnInit, inject, signal, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ClaimService } from '../../services/claim.service';
import { CreateClaimRequest } from '../../interfaces/claim.interface';

@Component({
  selector: 'app-claim-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './claim-form.html',
  styleUrls: ['./claim-form.scss']
})
export class ClaimFormComponent implements OnInit {
  private claimService = inject(ClaimService);

  @Input() show = signal<boolean>(false);
  @Output() closeModal = new EventEmitter<void>();
  @Output() claimCreated = new EventEmitter<void>();

  // Estado del formulario
  isLoading = signal<boolean>(false);
  errorMessage = signal<string>('');

  // Modelo del formulario basado en CreateClaimRequest
  claimForm: CreateClaimRequest = {
    date: '',
    amount: 0,
    identityDocument: '',
    description: '',
    reason: '',
    subReason: ''
  };

  // Opciones predefinidas para los select
  reasonOptions = [
    'Cargo indebido',
    'Transacción no autorizada',
    'Error en procesamiento',
    'Problema con transferencia',
    'Comisión incorrecta',
    'Otros'
  ];

  subReasonOptions: Record<string, string[]> = {
    'Cargo indebido': [
      'Transacción duplicada',
      'Monto incorrecto',
      'Comercio desconocido',
      'Cargo después de cancelación'
    ],
    'Transacción no autorizada': [
      'Uso fraudulento de tarjeta',
      'Robo de credenciales',
      'Transacción en línea no realizada',
      'Retiro no autorizado en ATM'
    ],
    'Error en procesamiento': [
      'Falla en sistema',
      'Error de comunicación',
      'Problema técnico',
      'Timeout en transacción'
    ],
    'Problema con transferencia': [
      'Transferencia no recibida',
      'Monto incorrecto transferido',
      'Cuenta destino incorrecta',
      'Demora en procesamiento'
    ],
    'Comisión incorrecta': [
      'Comisión no aplicable',
      'Monto de comisión excesivo',
      'Doble cobro de comisión',
      'Comisión no informada'
    ],
    'Otros': [
      'Consulta general',
      'Solicitud de información',
      'Problema no clasificado'
    ]
  };

  currentSubReasons: string[] = [];

  ngOnInit() {
    this.claimForm.date = new Date().toISOString().slice(0, 16);
  }

  /**
   * Maneja el cambio en el motivo principal para actualizar sub-motivos
   */
  onReasonChange() {
    this.currentSubReasons = this.subReasonOptions[this.claimForm.reason] || [];
    this.claimForm.subReason = ''; // Reset submotivo
  }

  /**
   * Submite el formulario
   */
  onSubmit(form: NgForm) {
    if (form.valid && !this.isLoading()) {
      this.isLoading.set(true);
      this.errorMessage.set('');

      // Convertir fecha al formato ISO requerido por la API
      const formData: CreateClaimRequest = {
        ...this.claimForm,
        date: new Date(this.claimForm.date).toISOString()
      };

      this.claimService.createClaim(formData).subscribe({
        next: (claim) => {
          console.log('Reclamo creado exitosamente:', claim);
          this.claimCreated.emit();
          this.resetForm();
          this.onClose();
        },
        error: (error) => {
          console.error('Error al crear reclamo:', error);
          this.errorMessage.set(
            error.error?.message || 'Error al crear el reclamo. Intente nuevamente.'
          );
        },
        complete: () => {
          this.isLoading.set(false);
        }
      });
    }
  }

  /**
   * Cierra el modal
   */
  onClose() {
    this.resetForm();
    this.closeModal.emit();
  }

  /**
   * Reset del formulario
   */
  private resetForm() {
    this.claimForm = {
      date: new Date().toISOString().slice(0, 16),
      amount: 0,
      identityDocument: '',
      description: '',
      reason: '',
      subReason: ''
    };
    this.currentSubReasons = [];
    this.errorMessage.set('');
    this.isLoading.set(false);
  }
}