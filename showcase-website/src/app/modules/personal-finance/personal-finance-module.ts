import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PersonalFinanceRoutingModule } from './personal-finance-routing-module';
import { PersonalFinance } from './personal-finance/personal-finance';
import { PersonalFinanceCrudComponent } from './components/personal-finance-crud/personal-finance-crud';
import { PersonalFinanceFormComponent } from './components/personal-finance-form/personal-finance-form';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    PersonalFinanceRoutingModule,
    PersonalFinance,
    PersonalFinanceCrudComponent,
    PersonalFinanceFormComponent
  ]
})
export class PersonalFinanceModule { }
