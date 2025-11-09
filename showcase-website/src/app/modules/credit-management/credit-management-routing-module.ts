import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreditManagement } from './credit-management/credit-management';

const routes: Routes = [
  {
    path: '',
    component: CreditManagement,
    children: [
      {
        path: '',
        redirectTo: 'products',
        pathMatch: 'full'
      },
      {
        path: 'products',
        loadComponent: () => import('./pages/products-page/products-page').then(m => m.ProductsPageComponent)
      },
      {
        path: 'ranks',
        loadComponent: () => import('./pages/ranks-page/ranks-page').then(m => m.RanksPageComponent)
      },
      {
        path: 'evaluation',
        loadComponent: () => import('./pages/evaluation-page/evaluation-page').then(m => m.EvaluationPageComponent)
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CreditManagementRoutingModule { }
