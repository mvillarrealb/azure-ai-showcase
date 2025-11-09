import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClaimManagement } from './claim-management/claim-management';
import { ClaimsCrudComponent } from './components/claims-crud/claims-crud';
import { ClaimsImportComponent } from './components/claims-import/claims-import';

const routes: Routes = [
  {
    path: '',
    component: ClaimManagement,
    children: [
      {
        path: '',
        redirectTo: 'claims',
        pathMatch: 'full'
      },
      {
        path: 'claims',
        component: ClaimsCrudComponent
      },
      {
        path: 'import',
        component: ClaimsImportComponent
      },
      {
        path: 'reports',
        component: ClaimsImportComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClaimManagementRoutingModule { }
