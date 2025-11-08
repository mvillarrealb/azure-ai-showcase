import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreditManagementRoutingModule } from './credit-management-routing-module';
import { CreditManagement } from './credit-management/credit-management';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    CreditManagementRoutingModule,
    CreditManagement
  ]
})
export class CreditManagementModule { }
