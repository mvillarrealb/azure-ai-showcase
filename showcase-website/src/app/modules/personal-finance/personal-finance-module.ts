import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PersonalFinance } from './personal-finance/personal-finance';
import { PersonalFinanceCrudComponent } from './components/personal-finance-crud/personal-finance-crud';
import { PersonalFinanceFormComponent } from './components/personal-finance-form/personal-finance-form';
import { InvoiceScannerComponent } from './components/invoice-scanner/invoice-scanner';
import { MonthlyReportsComponent } from './components/monthly-reports/monthly-reports';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    PersonalFinance,
    PersonalFinanceCrudComponent,
    PersonalFinanceFormComponent,
    InvoiceScannerComponent,
    MonthlyReportsComponent
  ]
})
export class PersonalFinanceModule { }
