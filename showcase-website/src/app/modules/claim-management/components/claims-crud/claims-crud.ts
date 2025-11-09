import { Component, OnInit, inject, signal, Output, EventEmitter, HostListener, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ClaimsPageableAdapter } from '../../adapters/claims-pageable.adapter';
import { ClaimEventsService } from '../../services/claim-events.service';
import { Claim, ClaimStatusLabels } from '../../interfaces/claim.interface';
import { 
  PageableGridConfig, 
  GridColumn 
} from '../../../../components/pageable-grid/pageable-adapter.interface';
import { PageableGridComponent } from '../../../../components/pageable-grid/pageable-grid.component';

@Component({
  selector: 'app-claims-crud',
  standalone: true,
  imports: [CommonModule, FormsModule, PageableGridComponent],
  templateUrl: './claims-crud.html',
  styleUrls: ['./claims-crud.scss'],
  providers: [ClaimsPageableAdapter]
})
export class ClaimsCrudComponent implements OnInit, OnDestroy {
  // Adaptador para el grid
  claimsAdapter = inject(ClaimsPageableAdapter);
  
  // Service para eventos de refresh
  private claimEventsService = inject(ClaimEventsService);
  
  // Subscription para el refresh
  private refreshSubscription?: Subscription;

  // Outputs para comunicar eventos al componente padre
  @Output() showCreateForm = new EventEmitter<void>();
  @Output() showResolveForm = new EventEmitter<Claim>();

  // Signal para el claim seleccionado
  selectedClaim = signal<Claim | null>(null);

  // Datos actuales para manejar acciones
  private currentData: Claim[] = [];

  // Configuración del grid siguiendo estándares CRUD_SPEC
  gridConfig: PageableGridConfig = {
    pageSize: 15,
    pageSizeOptions: [15],
    showPageSizeSelector: false,
    loadingText: 'Cargando reclamos...',
    emptyText: 'No se encontraron reclamos'
  };

  // Definición de columnas basada en Claim interface y diseño minimalista
  gridColumns: GridColumn[] = [
    { 
      key: 'id', 
      label: 'ID Reclamo', 
      sortable: false, 
      width: '15%',
      type: 'text'
    },
    { 
      key: 'identityDocument', 
      label: 'Documento', 
      sortable: false, 
      width: '12%',
      type: 'text'
    },
    { 
      key: 'description', 
      label: 'Descripción', 
      sortable: false, 
      width: '30%',
      type: 'text',
      multiline: true
    },
    { 
      key: 'amount', 
      label: 'Monto', 
      sortable: false, 
      type: 'currency', 
      align: 'right', 
      width: '12%'
    },
    { 
      key: 'status', 
      label: 'Estado', 
      sortable: false, 
      width: '12%',
      type: 'status',
      format: (value: string) => ClaimStatusLabels[value as keyof typeof ClaimStatusLabels] || value
    },
    { 
      key: 'date', 
      label: 'Fecha', 
      sortable: false, 
      type: 'date', 
      width: '12%'
    }
  ];

  async ngOnInit() {
    // El adapter manejará la carga inicial automáticamente
    // Suscribirse a cambios de datos para mantener referencia local
    this.claimsAdapter.loadData().subscribe({
      next: (result) => {
        this.currentData = result.data;
      },
      error: (error) => {
        console.error('Error loading claims:', error);
      }
    });
    
    // Suscribirse a eventos de refresh
    this.refreshSubscription = this.claimEventsService.refresh$.subscribe(() => {
      this.refreshGrid();
    });
  }

  ngOnDestroy() {
    // Limpiar subscription
    this.refreshSubscription?.unsubscribe();
  }
  
  /**
   * Refresca los datos del grid
   */
  private refreshGrid() {
    this.claimsAdapter.loadData().subscribe({
      next: (result) => {
        this.currentData = result.data;
      },
      error: (error) => {
        console.error('Error refreshing claims:', error);
      }
    });
  }

  /**
   * Maneja el click en una fila para seleccionar el claim
   */
  onViewDetails(claim: Claim) {
    this.selectedClaim.set(claim);
  }

  /**
   * Maneja el click del botón resolver (ahora será llamado desde el botón externo)
   */
  onResolveClaim() {
    const claim = this.selectedClaim();
    if (claim && claim.status !== 'resolved') {
      this.showResolveForm.emit(claim);
    }
  }

  /**
   * Maneja el click del botón crear nuevo reclamo
   */
  onCreateClaim() {
    this.showCreateForm.emit();
  }

}