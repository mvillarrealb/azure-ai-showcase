import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PersonalFinance } from './personal-finance/personal-finance';

const routes: Routes = [
  {
    path: '',
    component: PersonalFinance
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PersonalFinanceRoutingModule { }
