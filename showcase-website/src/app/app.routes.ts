import { Routes } from '@angular/router';
import { Home } from './home/home';

export const routes: Routes = [
  {
    path: '',
    component: Home
  },
  {
    path: 'personal-finance',
    loadChildren: () => import('./modules/personal-finance/personal-finance-routing-module').then(m => m.personalFinanceRoutes)
  },
  {
    path: 'credit-management',
    loadChildren: () => import('./modules/credit-management/credit-management-module').then(m => m.CreditManagementModule)
  },
  {
    path: 'claim-management',
    loadChildren: () => import('./modules/claim-management/claim-management-module').then(m => m.ClaimManagementModule)
  }
];
