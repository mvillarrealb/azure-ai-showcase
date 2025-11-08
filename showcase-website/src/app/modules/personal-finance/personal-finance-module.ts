import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PersonalFinanceRoutingModule } from './personal-finance-routing-module';
import { PersonalFinance } from './personal-finance/personal-finance';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    PersonalFinanceRoutingModule,
    PersonalFinance
  ]
})
export class PersonalFinanceModule { }
