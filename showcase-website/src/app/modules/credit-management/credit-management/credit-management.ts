import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidenavNavigation, NavigationLink } from '../../../components/sidenav-navigation/sidenav-navigation';

@Component({
  selector: 'app-credit-management',
  imports: [SidenavNavigation, RouterOutlet],
  templateUrl: './credit-management.html',
  styleUrl: './credit-management.scss',
})
export class CreditManagement {
  title = 'Gestión de Créditos';
  
  navigationLinks: NavigationLink[] = [
    {
      label: 'Productos Crediticios',
      route: '/credit-management/products',
      icon: 'fas fa-credit-card'
    },
    {
      label: 'Rangos de Crédito',
      route: '/credit-management/ranks',
      icon: 'fas fa-medal'
    },
    {
      label: 'Evaluación Crediticia',
      route: '/credit-management/evaluation',
      icon: 'fas fa-calculator'
    }
  ];
}
