import { Routes } from '@angular/router';
import { PersonalFinance } from './personal-finance/personal-finance';
import { PersonalFinanceCrudComponent } from './components/personal-finance-crud/personal-finance-crud';
import { InvoiceScannerComponent } from './components/invoice-scanner/invoice-scanner';
import { MonthlyReportsComponent } from './components/monthly-reports/monthly-reports';

export const personalFinanceRoutes: Routes = [
  {
    path: '',
    component: PersonalFinance,
    children: [
      {
        path: '',
        redirectTo: 'transacciones',
        pathMatch: 'full'
      },
      {
        path: 'transacciones',
        component: PersonalFinanceCrudComponent
      },
      {
        path: 'escaneo',
        component: InvoiceScannerComponent
      },
      {
        path: 'reportes',
        component: MonthlyReportsComponent
      }
    ]
  }
];
