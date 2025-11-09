import { Component, inject, signal, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ClaimService } from '../../services/claim.service';
import { ClaimEventsService } from '../../services/claim-events.service';
import { Claim, ResolveClaimRequest, ClaimStatusLabels } from '../../interfaces/claim.interface';

@Component({
  selector: 'app-resolve-claim',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './resolve-claim.html',
  styleUrls: ['./resolve-claim.scss']
})
export class ResolveClaimComponent {
  private claimService = inject(ClaimService);
  private claimEventsService = inject(ClaimEventsService);

  @Input() show = signal<boolean>(false);
  @Input() claim = signal<Claim | null>(null);
  @Output() closeModal = new EventEmitter<void>();
  @Output() claimResolved = new EventEmitter<void>();

  // Estado del formulario
  isLoading = signal<boolean>(false);
  errorMessage = signal<string>('');

  // Modelo del formulario
  resolutionForm: ResolveClaimRequest = {
    comments: ''
  };

  // Helper para obtener el label del estado
  getStatusLabel(status: string): string {
    return ClaimStatusLabels[status as keyof typeof ClaimStatusLabels] || status;
  }

  /**
   * Formatear fecha para mostrar
   */
  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * Formatear monto como moneda
   */
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN'
    }).format(amount);
  }

  /**
   * Submite la resolución del reclamo
   */
  onSubmit(form: NgForm) {
    const currentClaim = this.claim();
    
    if (form.valid && !this.isLoading() && currentClaim) {
      this.isLoading.set(true);
      this.errorMessage.set('');

      this.claimService.resolveClaim(currentClaim.id, this.resolutionForm).subscribe({
        next: (resolvedClaim) => {
          console.log('Reclamo resuelto exitosamente:', resolvedClaim);
          this.claimResolved.emit();
          this.claimEventsService.emitRefresh(); // Emite evento para refrescar el grid
          this.resetForm();
          this.onClose();
        },
        error: (error) => {
          console.error('Error al resolver reclamo:', error);
          this.errorMessage.set(
            error.error?.message || 'Error al resolver el reclamo. Intente nuevamente.'
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
    this.resolutionForm = {
      comments: ''
    };
    this.errorMessage.set('');
    this.isLoading.set(false);
  }
}