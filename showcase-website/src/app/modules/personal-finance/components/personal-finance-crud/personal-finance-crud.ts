import { Component, OnInit, inject, signal, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PersonalFinanceService } from '../../services/personal-finance.service';
import { TransactionsPageableAdapter } from '../../adapters/transactions-pageable.adapter';
import { Transaction } from '../../interfaces/personal-finance.interface';
import { PageableGridConfig, GridColumn } from '../../../../components/pageable-grid/pageable-adapter.interface';
import { PageableGridComponent } from '../../../../components/pageable-grid/pageable-grid.component';

@Component({
  selector: 'app-personal-finance-crud',
  standalone: true,
  imports: [CommonModule, FormsModule, PageableGridComponent],
  templateUrl: './personal-finance-crud.html',
  styleUrls: ['./personal-finance-crud.scss'],
  providers: [TransactionsPageableAdapter]
})
export class PersonalFinanceCrudComponent implements OnInit {
  private personalFinanceService = inject(PersonalFinanceService);
  private router = inject(Router);
  
  // Output para comunicar al padre
  @Output() showTransactionForm = new EventEmitter<void>();
  
  // Adaptador para el grid
  transactionsAdapter = inject(TransactionsPageableAdapter);

  // Configuración del grid minimalista
  gridConfig: PageableGridConfig = {
    pageSize: 15,
    pageSizeOptions: [15],
    showPageSizeSelector: false,
    loadingText: 'Cargando transacciones...',
    emptyText: 'No se encontraron transacciones'
  };

  // Definición de columnas para el grid basado en el OpenAPI
  gridColumns: GridColumn[] = [
    { key: 'description', label: 'Descripción', sortable: false, width: '35%' },
    { key: 'amount', label: 'Monto', sortable: false, type: 'currency', align: 'right', width: '20%' },
    { key: 'categoryId', label: 'Categoría', sortable: false, width: '25%' },
    { key: 'date', label: 'Fecha', sortable: false, type: 'date', width: '20%' }
  ];

  async ngOnInit() {
    // Cargar datos iniciales sin filtros
    // El adapter manejará la carga automáticamente
  }

  onViewDetails(transaction: Transaction) {
    // Mostrar detalles de la transacción (solo lectura)
    console.log('Ver detalles de transacción:', transaction);
  }
}