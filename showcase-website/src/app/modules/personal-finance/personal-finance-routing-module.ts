import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PersonalFinance } from './personal-finance/personal-finance';
import { PersonalFinanceCrudComponent } from './components/personal-finance-crud/personal-finance-crud';

const routes: Routes = [
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
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PersonalFinanceRoutingModule { }
