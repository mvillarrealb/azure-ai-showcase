import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { 
  PageableAdapter, 
  PageableResult, 
  GridColumn, 
  PageableGridConfig, 
  SortDirection 
} from './pageable-adapter.interface';
import { environment } from '../../../environments/environment';

/**
 * Componente PageableGrid reutilizable
 * Implementa paginación, ordenamiento y formateo de datos
 * Compatible con el look & feel actual (Tailwind + glassmorphism)
 */
@Component({
  selector: 'app-pageable-grid',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pageable-grid.component.html',
  styleUrls: ['./pageable-grid.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [`
    :host {
      display: block;
      width: 100%;
      overflow: hidden;
    }
    
    table {
      table-layout: fixed;
      width: 100%;
    }
    
    tbody tr {
      will-change: background-color;
      transform: translateZ(0);
    }
    
    .truncate {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      width: 100%;
      display: block;
    }
    
    .multiline {
      white-space: pre-line;
      word-wrap: break-word;
      overflow-wrap: break-word;
      width: 100%;
      display: block;
      max-height: 4.5em;
      overflow: hidden;
      line-height: 1.5em;
    }
    
    td {
      box-sizing: border-box;
      cursor: help;
    }
    
    th {
      box-sizing: border-box;
    }
    
    td[title]:hover {
      position: relative;
    }
  `]
})
export class PageableGridComponent<T = any> implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  // Inputs
  @Input({ required: true }) adapter!: PageableAdapter<T>;
  @Input({ required: true }) columns!: GridColumn[];
  @Input() title?: string;
  @Input() selectedRowIndex: number | null = null; // Índice de fila seleccionada
  @Input() headerColors: string = 'from-emerald-500 to-green-600'; // Colores del header
  @Input() footerColors: string = 'from-emerald-600 to-green-700'; // Colores del footer
  @Input() config: PageableGridConfig = {
    pageSize: 10,
    pageSizeOptions: [5, 10, 25, 50, 100],
    showPageSizeSelector: true,
    defaultSortField: undefined,
    defaultSortDirection: 'asc',
    enableMultiSort: false,
    loadingText: 'Cargando datos...',
    emptyText: 'No hay datos para mostrar'
  };

  // Outputs
  @Output() rowClick = new EventEmitter<T>();
  @Output() dataLoaded = new EventEmitter<PageableResult<T>>();
  @Output() loadingChanged = new EventEmitter<boolean>();

  // Señales reactivas
  data = signal<T[]>([]);
  isLoading = signal(false);
  totalElements = signal(0);
  totalPages = signal(0);
  currentPage = signal(0);
  currentPageSize = signal(10);
  hasNext = signal(false);
  hasPrevious = signal(false);
  loadError = signal<string | null>(null);
  
  currentSort = signal<{field: string; direction: SortDirection}>({
    field: '',
    direction: 'asc'
  });

  // Computed properties
  startRecord = computed(() => this.currentPage() * this.currentPageSize() + 1);
  endRecord = computed(() => Math.min((this.currentPage() + 1) * this.currentPageSize(), this.totalElements()));

  // Map de formateadores por tipo de columna
  private readonly formatters = new Map<string, (value: any) => string>([
    ['currency', (value: any) => this.formatCurrency(value)],
    ['date', (value: any) => this.formatDate(value)],
    ['status', (value: any) => this.formatStatus(value)],
    ['email', (value: any) => this.formatEmail(value)],
    ['number', (value: any) => this.formatNumber(value)]
  ]);

  ngOnInit(): void {
    this.initializeConfig();
    this.loadInitialData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeConfig(): void {
    this.currentPageSize.set(this.config.pageSize || 10);
    
    if (this.config.defaultSortField) {
      this.currentSort.set({
        field: this.config.defaultSortField,
        direction: this.config.defaultSortDirection || 'asc'
      });
    }
  }

  private loadInitialData(): void {
    console.log('🔄 PageableGrid: Cargando datos iniciales...');
    this.setLoading(true);
    
    this.adapter.loadData()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.handleDataResult(result);
        },
        error: (error) => {
          console.error('❌ PageableGrid: Error cargando datos:', error);
          this.handleError(error);
        },
        complete: () => this.setLoading(false)
      });
  }

  private handleDataResult(result: PageableResult<T>): void {    
    // Limpiar error anterior
    this.loadError.set(null);
    
    this.data.set(result.data);
    this.totalElements.set(result.totalElements);
    this.totalPages.set(result.totalPages);
    this.currentPage.set(result.currentPage);
    this.hasNext.set(result.hasNext);
    this.hasPrevious.set(result.hasPrevious);
    
    if (result.sortField && result.sortDirection) {
      this.currentSort.set({
        field: result.sortField,
        direction: result.sortDirection
      });
    }
    
    this.dataLoaded.emit(result);
  }

  private handleError(error: any): void {
    console.error('Error loading data:', error);
    this.loadError.set('Error al cargar los datos. Inténtalo de nuevo.');
    this.setLoading(false);
  }

  private setLoading(loading: boolean): void {
    this.isLoading.set(loading);
    this.loadingChanged.emit(loading);
  }

  // Public method to retry loading data
  retryLoad(): void {
    console.log('🔄 PageableGrid: Reintentando carga de datos...');
    this.loadError.set(null);
    this.loadInitialData();
  }

  // Event Handlers
  onRowClick(row: T): void {
    this.rowClick.emit(row);
  }

  onSort(column: GridColumn): void {
    if (!column.sortable) return;

    const currentSortField = this.currentSort().field;
    const currentDirection = this.currentSort().direction;
    
    let newDirection: SortDirection = 'asc';
    
    if (currentSortField === column.key) {
      newDirection = currentDirection === 'asc' ? 'desc' : 'asc';
    }

    this.currentSort.set({
      field: column.key,
      direction: newDirection
    });

    this.loadDataWithSort(column.key, newDirection);
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const newPageSize = parseInt(target.value, 10);
    
    this.currentPageSize.set(newPageSize);
    this.currentPage.set(0); // Reset to first page
    
    this.loadDataWithPageChange(0, newPageSize);
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages() || page === this.currentPage()) {
      return;
    }
    
    this.loadDataWithPageChange(page, this.currentPageSize());
  }

  private loadDataWithPageChange(page: number, pageSize: number): void {
    this.setLoading(true);
    
    const sort = this.currentSort();
    
    if (sort.field) {
      this.adapter.changePageAndSort(page, pageSize, sort.field, sort.direction)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (result) => this.handleDataResult(result),
          error: (error) => this.handleError(error),
          complete: () => this.setLoading(false)
        });
    } else {
      this.adapter.changePage(page, pageSize)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (result) => this.handleDataResult(result),
          error: (error) => this.handleError(error),
          complete: () => this.setLoading(false)
        });
    }
  }

  private loadDataWithSort(sortField: string, sortDirection: SortDirection): void {
    this.setLoading(true);
    
    this.adapter.changePageAndSort(
      this.currentPage(), 
      this.currentPageSize(), 
      sortField, 
      sortDirection
    )
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (result) => this.handleDataResult(result),
      error: (error) => this.handleError(error),
      complete: () => this.setLoading(false)
    });
  }

  // UI Helper Methods
  getHeaderClasses(column: GridColumn): string {
    const baseClasses = 'text-left';
    const sortableClasses = column.sortable ? 'cursor-pointer hover:bg-emerald-600 transition-colors duration-200' : '';
    const sortedClasses = this.currentSort()?.field === column.key ? 'bg-emerald-600' : '';
    
    return `${baseClasses} ${sortableClasses} ${sortedClasses}`.trim();
  }

  getRowClasses(isEven: boolean, rowIndex?: number): string {
    const baseClasses = 'transition-all duration-200 cursor-pointer border-b border-gray-100/50 backdrop-blur-sm';
    
    // Fila seleccionada: estilo muy distintivo
    const isSelected = (rowIndex === this.selectedRowIndex && this.selectedRowIndex !== null);
    
    if (isSelected) {
      // Fila seleccionada: azul intenso con borde, hover solo cambia ligeramente el tono
      const selectedClasses = 'bg-blue-500/20 border-l-4 border-blue-500 hover:bg-blue-500/25';
      console.log('🎯 Aplicando estilo selected a fila:', rowIndex);
      return `${baseClasses} ${selectedClasses}`.trim();
    }
    
    // Filas normales: hover verde suave, zebra pattern normal
    const zebraClasses = isEven ? 'bg-white/60' : 'bg-white/40';
    const hoverClasses = 'hover:bg-emerald-50/70 hover:shadow-md';
    return `${baseClasses} ${zebraClasses} ${hoverClasses}`.trim();
  }

  getCellClasses(column: GridColumn): string {
    const baseClasses = 'px-6 py-4 text-sm leading-relaxed';
    const alignClasses = column.align === 'center' ? 'text-center' : 
                        column.align === 'right' ? 'text-right' : 'text-left';
    const typeClasses = column.type === 'currency' ? '' : // El formato ya incluye las clases de color
                       column.type === 'date' ? 'text-gray-600 font-medium' :
                       'text-gray-800';
    
    return `${baseClasses} ${alignClasses} ${typeClasses}`.trim();
  }

  getCellContentClasses(column: GridColumn): string {
    // Si la columna permite saltos de línea, usar clase multiline
    // Por defecto usar truncate para mantener compatibilidad
    const contentClasses = (column as any).multiline ? 'multiline w-full' : 'truncate w-full';
    return contentClasses;
  }

  getCellTooltip(value: any, column: GridColumn): string {
    if (value === null || value === undefined) {
      return '';
    }

    // Si hay formateador personalizado, intentar obtener valor plano
    if (column.format) {
      const formatted = column.format(value);
      // Remover tags HTML para el tooltip
      return this.stripHtmlTags(formatted);
    }

    // Para tipos específicos, generar tooltip apropiado
    if (column.type === 'status' || column.type === 'email') {
      return this.stripHtmlTags(String(value));
    }

    // Para currency y date, usar el formateador pero sin HTML
    if (column.type === 'currency') {
      return this.formatCurrency(value);
    }

    if (column.type === 'date') {
      return this.formatDate(value);
    }

    // Por defecto retornar el valor como string
    return String(value);
  }

  private stripHtmlTags(html: string): string {
    const div = document.createElement('div');
    div.innerHTML = html;
    return div.textContent || div.innerText || '';
  }

  formatCellValue(value: any, column: GridColumn, row?: T): string {
    if (value === null || value === undefined) {
      return '';
    }

    // Usar formateador personalizado si está definido
    if (column.format) {
      return column.format(value, row);
    }

    // Usar formateador del Map según el tipo de columna
    const formatter = this.formatters.get(column.type || '');
    return formatter ? formatter(value) : String(value);
  }

  getColumnValue(row: T, columnKey: string): any {
    // Para columnas especiales como 'actions', retornar un valor por defecto
    if (columnKey === 'actions') {
      return 'actions'; // Valor dummy para que el formateador reciba algo
    }
    
    return (row as any)[columnKey];
  }

  private formatCurrency(value: number): string {
    if (typeof value !== 'number') return String(value);
    const locale = environment.localization?.locale || 'es-PE';
    const currency = environment.localization?.currency || 'PEN';
    const formatted = new Intl.NumberFormat(locale, {
      style: 'currency',
      currency: currency
    }).format(value);
    
    // Agregar clases CSS para distinguir ingresos de gastos
    const colorClass = value >= 0 ? 'text-emerald-600 font-semibold' : 'text-red-600 font-semibold';
    const icon = value >= 0 ? '↗' : '↘';
    
    return `<span class="${colorClass}"><span class="text-xs opacity-70">${icon}</span> ${formatted}</span>`;
  }

  private formatDate(value: string | Date): string {
    if (!value) return '';
    const date = new Date(value);
    const locale = environment.localization?.locale || 'es-PE';
    return new Intl.DateTimeFormat(locale, {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    }).format(date);
  }

  private formatStatus(value: string): string {
    if (!value) return '';
    return `<span class="status-badge status-${value.toLowerCase()}">${value}</span>`;
  }

  private formatEmail(value: string): string {
    if (!value) return '';
    return `<a href="mailto:${value}" class="text-emerald-600 hover:text-emerald-800 underline">${value}</a>`;
  }

  private formatNumber(value: number): string {
    if (typeof value !== 'number') return String(value);
    return new Intl.NumberFormat('es-MX').format(value);
  }

  getStartRecord(): number {
    return this.startRecord();
  }

  getEndRecord(): number {
    return this.endRecord();
  }

  hasActions(): boolean {
    // Verificar si hay columnas de acción con contenido
    return this.columns.some(col => col.key === 'actions');
  }
}