import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreditManagement } from './credit-management/credit-management';

const routes: Routes = [
  {
    path: '',
    component: CreditManagement
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CreditManagementRoutingModule { }
