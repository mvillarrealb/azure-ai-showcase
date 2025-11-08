import { Component, signal, inject, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PersonalFinanceService } from '../../services/personal-finance.service';
import { MonthlyReport, Category } from '../../interfaces/personal-finance.interface';
import { format } from 'date-fns';
import { 
  Chart, 
  ChartConfiguration, 
  ChartData, 
  ChartOptions,
  registerables 
} from 'chart.js';

// Register Chart.js components
Chart.register(...registerables);

interface CategoryBreakdownWithNames {
  categoryId: string;
  categoryName: string;
  type: 'income' | 'expense';
  totalAmount: number;
  percentage: number;
  color: string;
}

@Component({
  selector: 'app-monthly-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './monthly-reports.html',
  styleUrl: './monthly-reports.scss'
})
export class MonthlyReportsComponent implements AfterViewInit, OnDestroy {
  private personalFinanceService = inject(PersonalFinanceService);

  @ViewChild('expenseChart', { static: false }) expenseChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('incomeExpenseChart', { static: false }) incomeExpenseChartRef!: ElementRef<HTMLCanvasElement>;

  // State signals
  isLoading = signal<boolean>(false);
  monthlyReport = signal<MonthlyReport | null>(null);
  errorMessage = signal<string>('');
  categories = signal<Category[]>([]);
  selectedMonth = signal<string>(format(new Date(), 'yyyy-MM'));
  categoryBreakdownWithNames = signal<CategoryBreakdownWithNames[]>([]);

  // Chart instances
  private expenseChart?: Chart;
  private incomeExpenseChart?: Chart;

  // Color palettes
  private expenseColors = [
    '#EF4444', '#F97316', '#EAB308', '#22C55E', 
    '#3B82F6', '#8B5CF6', '#EC4899', '#6B7280',
    '#F59E0B', '#10B981', '#6366F1', '#84CC16'
  ];

  constructor() {
    this.loadCategories();
    this.loadReport();
  }

  ngAfterViewInit() {
    // Charts will be created after data is loaded
    if (this.monthlyReport()) {
      setTimeout(() => this.createCharts(), 100);
    }
  }

  ngOnDestroy() {
    this.destroyCharts();
  }

  private loadCategories() {
    this.personalFinanceService.getCategories().subscribe({
      next: (categories) => {
        this.categories.set(categories);
        this.updateCategoryBreakdown();
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }

  loadReport() {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.personalFinanceService.getMonthlyReport(this.selectedMonth()).subscribe({
      next: (report) => {
        this.isLoading.set(false);
        this.monthlyReport.set(report);
        this.updateCategoryBreakdown();
        
        // Recreate charts with new data
        setTimeout(() => {
          this.destroyCharts();
          this.createCharts();
        }, 100);
      },
      error: (error) => {
        this.isLoading.set(false);
        console.error('Error loading monthly report:', error);
        
        if (error.status === 404) {
          this.errorMessage.set('No se encontraron transacciones para este mes.');
        } else {
          this.errorMessage.set('Error al cargar el reporte mensual. Inténtalo nuevamente.');
        }
      }
    });
  }

  onMonthChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedMonth.set(input.value);
    this.loadReport();
  }

  private updateCategoryBreakdown() {
    const report = this.monthlyReport();
    const categories = this.categories();
    
    if (!report || !categories.length) {
      this.categoryBreakdownWithNames.set([]);
      return;
    }

    const totalAmount = report.totalIncome + report.totalExpense;
    
    const breakdown: CategoryBreakdownWithNames[] = report.categoryBreakdown.map((item, index) => {
      const category = categories.find(cat => cat.id === item.categoryId);
      const percentage = totalAmount > 0 ? (Math.abs(item.totalAmount) / totalAmount) * 100 : 0;
      
      return {
        categoryId: item.categoryId,
        categoryName: category?.name || 'Categoría desconocida',
        type: category?.type || 'expense',
        totalAmount: item.totalAmount,
        percentage: percentage,
        color: this.getColorForCategory(index, category?.type || 'expense')
      };
    });

    // Sort by amount (descending)
    breakdown.sort((a, b) => Math.abs(b.totalAmount) - Math.abs(a.totalAmount));
    
    this.categoryBreakdownWithNames.set(breakdown);
  }

  private getColorForCategory(index: number, type: 'income' | 'expense'): string {
    if (type === 'income') {
      return '#22C55E'; // Green for income
    }
    return this.expenseColors[index % this.expenseColors.length];
  }

  private createCharts() {
    if (!this.monthlyReport()) return;

    this.createExpenseChart();
    this.createIncomeExpenseChart();
  }

  private createExpenseChart() {
    const expenseBreakdown = this.categoryBreakdownWithNames()
      .filter(item => item.type === 'expense' && item.totalAmount < 0);

    if (!expenseBreakdown.length || !this.expenseChartRef) return;

    const ctx = this.expenseChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    const data: ChartData<'pie'> = {
      labels: expenseBreakdown.map(item => item.categoryName),
      datasets: [{
        data: expenseBreakdown.map(item => Math.abs(item.totalAmount)),
        backgroundColor: expenseBreakdown.map(item => item.color),
        borderWidth: 2,
        borderColor: '#ffffff'
      }]
    };

    const options: ChartOptions<'pie'> = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            padding: 20,
            usePointStyle: true,
            font: {
              size: 12
            }
          }
        },
        tooltip: {
          callbacks: {
            label: (context) => {
              const value = context.parsed;
              const percentage = ((value / expenseBreakdown.reduce((sum, item) => sum + Math.abs(item.totalAmount), 0)) * 100).toFixed(1);
              return `${context.label}: S/${value.toFixed(2)} (${percentage}%)`;
            }
          }
        }
      }
    };

    this.expenseChart = new Chart(ctx, {
      type: 'pie',
      data: data,
      options: options
    });
  }

  private createIncomeExpenseChart() {
    const report = this.monthlyReport();
    if (!report || !this.incomeExpenseChartRef) return;

    const ctx = this.incomeExpenseChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    const data: ChartData<'bar'> = {
      labels: ['Ingresos', 'Gastos', 'Ahorro Neto'],
      datasets: [{
        label: 'Monto ($)',
        data: [
          report.totalIncome,
          report.totalExpense,
          report.netSavings
        ],
        backgroundColor: [
          '#22C55E', // Green for income
          '#EF4444', // Red for expenses
          report.netSavings >= 0 ? '#3B82F6' : '#F97316' // Blue for positive savings, orange for negative
        ],
        borderColor: [
          '#16A34A',
          '#DC2626',
          report.netSavings >= 0 ? '#2563EB' : '#EA580C'
        ],
        borderWidth: 2,
        borderRadius: 8,
        borderSkipped: false
      }]
    };

    const options: ChartOptions<'bar'> = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          callbacks: {
            label: (context) => {
              const value = context.parsed.y;
              return `${context.label}: S/${Math.abs(value || 0).toFixed(2)}`;
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: (value) => `S/${Number(value).toFixed(0)}`
          }
        },
        x: {
          ticks: {
            font: {
              weight: 'bold'
            }
          }
        }
      }
    };

    this.incomeExpenseChart = new Chart(ctx, {
      type: 'bar',
      data: data,
      options: options
    });
  }

  private destroyCharts() {
    if (this.expenseChart) {
      this.expenseChart.destroy();
      this.expenseChart = undefined;
    }
    if (this.incomeExpenseChart) {
      this.incomeExpenseChart.destroy();
      this.incomeExpenseChart = undefined;
    }
  }

  // Utility method for template
  Math = Math;

  isPositiveSavings(): boolean {
    return (this.monthlyReport()?.netSavings ?? 0) >= 0;
  }

  isNegativeSavings(): boolean {
    return (this.monthlyReport()?.netSavings ?? 0) < 0;
  }
}