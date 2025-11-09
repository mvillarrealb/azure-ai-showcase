import { Component, signal, inject, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PersonalFinanceService } from '../../services/personal-finance.service';
import { MonthlyReport, CategoryBreakdown } from '../../interfaces/personal-finance.interface';
import { format } from 'date-fns';
import { 
  Chart, 
  ChartData, 
  ChartOptions,
  registerables 
} from 'chart.js';

// Register Chart.js components
Chart.register(...registerables);

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
  selectedMonth = signal<string>(format(new Date(), 'yyyy-MM'));

  // Chart instances
  private expenseChart?: Chart;
  private incomeExpenseChart?: Chart;

  constructor() {
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

  loadReport() {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.personalFinanceService.getMonthlyReport(this.selectedMonth()).subscribe({
      next: (report) => {
        this.isLoading.set(false);
        
        // Debug: Log the data received
        console.log('Monthly Report Data:', report);
        console.log('Category Breakdown:', report.categoryBreakdown);
        
        this.monthlyReport.set(report);
        
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

  private getExpenseBreakdown(): CategoryBreakdown[] {
    const report = this.monthlyReport();
    if (!report) return [];
    
    // Debug log to see all data
    console.log('All category breakdown:', report.categoryBreakdown);
    
    // Filter for expense categories using categoryType
    const expenses = report.categoryBreakdown.filter(item => item.categoryType === 'expense');
    
    console.log('Filtered expenses:', expenses);
    return expenses;
  }

  private createCharts() {
    if (!this.monthlyReport()) return;

    this.createExpenseChart();
    this.createIncomeExpenseChart();
  }

  private createExpenseChart() {
    const expenseBreakdown = this.getExpenseBreakdown();

    if (!expenseBreakdown.length || !this.expenseChartRef) {
      // Si no hay datos de gastos, mostrar un mensaje en el canvas
      if (this.expenseChartRef) {
        const ctx = this.expenseChartRef.nativeElement.getContext('2d');
        if (ctx) {
          ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
          ctx.font = '16px Arial';
          ctx.fillStyle = '#6B7280';
          ctx.textAlign = 'center';
          ctx.fillText('No hay gastos para mostrar', ctx.canvas.width / 2, ctx.canvas.height / 2);
        }
      }
      return;
    }

    const ctx = this.expenseChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    const data: ChartData<'pie'> = {
      labels: expenseBreakdown.map(item => item.categoryName),
      datasets: [{
        data: expenseBreakdown.map(item => item.totalAmount), // Los montos ya son positivos
        backgroundColor: expenseBreakdown.map((item, index) => this.getColorForCategory(index)),
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
              const total = expenseBreakdown.reduce((sum, item) => sum + item.totalAmount, 0);
              const percentage = ((value / total) * 100).toFixed(1);
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

  Math = Math;

  isPositiveSavings(): boolean {
    return (this.monthlyReport()?.netSavings ?? 0) >= 0;
  }

  isNegativeSavings(): boolean {
    return (this.monthlyReport()?.netSavings ?? 0) < 0;
  }

  hasExpenseData(): boolean {
    return this.getExpenseBreakdown().length > 0;
  }

  getColorForCategory(index: number): string {
    const colors = [
      '#EF4444', '#F97316', '#EAB308', '#22C55E', 
      '#3B82F6', '#8B5CF6', '#EC4899', '#6B7280',
      '#F59E0B', '#10B981', '#6366F1', '#84CC16'
    ];
    return colors[index % colors.length];
  }

  getPercentage(amount: number): number {
    const report = this.monthlyReport();
    if (!report) return 0;
    
    const totalAmount = report.totalIncome + report.totalExpense;
    if (totalAmount === 0) return 0;
    
    return (amount / totalAmount) * 100;
  }
}