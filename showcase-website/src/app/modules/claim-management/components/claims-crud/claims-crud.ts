import { Component, OnInit, inject, signal, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClaimsPageableAdapter } from '../../adapters/claims-pageable.adapter';
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
export class ClaimsCrudComponent implements OnInit {
  // Adaptador para el grid
  claimsAdapter = inject(ClaimsPageableAdapter);

  // Outputs para comunicar eventos al componente padre
  @Output() showCreateForm = new EventEmitter<void>();
  @Output() showResolveForm = new EventEmitter<Claim>();

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
    },
    // Columna de acciones removida temporalmente hasta implementar correctamente
    // Se manejará con row clicks por ahora
  ];

  async ngOnInit() {
    // El adapter manejará la carga inicial automáticamente
  }

  /**
   * Maneja el click en una fila para mostrar detalles
   */
  onViewDetails(claim: Claim) {
    console.log('Ver detalles de reclamo:', claim);
    // Aquí se puede implementar un modal de detalles si es necesario
  }

  /**
   * Maneja clicks en acciones del grid
   */
  onGridAction(event: any) {
    const target = event.target as HTMLElement;
    
    if (target.closest('.resolve-btn')) {
      const claimId = target.closest('.resolve-btn')?.getAttribute('data-id');
      if (claimId) {
        // Buscar el claim completo para pasar al modal
        // En una implementación real, esto se optimizaría
        console.log('Resolver reclamo:', claimId);
        // this.showResolveForm.emit(claim);
      }
    }
  }

  /**
   * Emite evento para mostrar modal de creación
   */
  onCreateClaim() {
    this.showCreateForm.emit();
  }

  /**
   * Refresca los datos del grid
   */
  refreshData() {
    this.claimsAdapter.refresh().subscribe();
  }
}