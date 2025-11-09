import { Component, OnInit, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CreditManagementService } from '../../services/credit-management.service';
import { RanksPageableAdapter } from '../../adapters/ranks-pageable.adapter';
import { Rank } from '../../interfaces/credit-management.interface';
import { PageableGridConfig, GridColumn } from '../../../../components/pageable-grid/pageable-adapter.interface';
import { PageableGridComponent } from '../../../../components/pageable-grid/pageable-grid.component';

@Component({
  selector: 'app-ranks-crud',
  standalone: true,
  imports: [CommonModule, FormsModule, PageableGridComponent],
  templateUrl: './ranks-crud.html',
  providers: [RanksPageableAdapter]
})
export class RanksCrudComponent implements OnInit {
  private creditManagementService = inject(CreditManagementService);
  private router = inject(Router);
  
  // Output para comunicar al padre
  @Output() showRankForm = new EventEmitter<void>();
  
  // Adaptador para el grid
  ranksAdapter = inject(RanksPageableAdapter);

  // Configuración del grid siguiendo estándar CRUD_SPEC
  gridConfig: PageableGridConfig = {
    pageSize: 15,
    pageSizeOptions: [15],
    showPageSizeSelector: false,
    loadingText: 'Cargando rangos...',
    emptyText: 'No se encontraron rangos de crédito'
  };

  // Definición de columnas para el grid
  gridColumns: GridColumn[] = [
    { key: 'id', label: 'ID', sortable: true, width: '15%', align: 'center' },
    { key: 'name', label: 'Nombre', sortable: true, width: '20%' },
    { 
      key: 'description', 
      label: 'Descripción', 
      sortable: false, 
      width: '55%',
      multiline: true 
    },
    { 
      key: 'createdAt', 
      label: 'Creado', 
      sortable: true, 
      type: 'date', 
      width: '10%',
      align: 'center'
    }
  ];

  async ngOnInit() {
    // El adapter manejará la carga automáticamente
  }

  onViewDetails(rank: Rank) {
    // Mostrar detalles del rango (solo lectura)
    console.log('Ver detalles de rango:', rank);
  }
}