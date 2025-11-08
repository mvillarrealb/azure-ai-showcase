import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidenavNavigation, NavigationLink } from '../../../components/sidenav-navigation/sidenav-navigation';

@Component({
  selector: 'app-personal-finance',
  imports: [SidenavNavigation, RouterOutlet],
  templateUrl: './personal-finance.html',
  styleUrl: './personal-finance.scss',
})
export class PersonalFinance {
  title = 'Gestión de Finanzas Personales';
  
  navigationLinks: NavigationLink[] = [
    {
      label: 'Dashboard',
      route: '/personal-finance/dashboard',
      icon: 'fas fa-chart-pie'
    },
    {
      label: 'Transacciones',
      route: '/personal-finance/transactions',
      icon: 'fas fa-exchange-alt'
    },
    {
      label: 'Presupuestos',
      route: '/personal-finance/budgets',
      icon: 'fas fa-calculator'
    },
    {
      label: 'Categorías',
      route: '/personal-finance/categories',
      icon: 'fas fa-tags'
    },
    {
      label: 'Reportes',
      route: '/personal-finance/reports',
      icon: 'fas fa-chart-bar'
    },
    {
      label: 'Configuración',
      route: '/personal-finance/settings',
      icon: 'fas fa-cog'
    }
  ];
}
