import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClaimManagement } from './claim-management/claim-management';

const routes: Routes = [
  {
    path: '',
    component: ClaimManagement
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClaimManagementRoutingModule { }
