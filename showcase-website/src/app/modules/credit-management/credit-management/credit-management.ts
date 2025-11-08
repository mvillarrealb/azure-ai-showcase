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
      label: 'Dashboard',
      route: '/credit-management/dashboard',
      icon: 'fas fa-tachometer-alt'
    },
    {
      label: 'Solicitudes',
      route: '/credit-management/applications',
      icon: 'fas fa-file-alt'
    },
    {
      label: 'Créditos Activos',
      route: '/credit-management/active-credits',
      icon: 'fas fa-credit-card'
    },
    {
      label: 'Simulador',
      route: '/credit-management/simulator',
      icon: 'fas fa-calculator'
    },
    {
      label: 'Historial',
      route: '/credit-management/history',
      icon: 'fas fa-history'
    },
    {
      label: 'Documentos',
      route: '/credit-management/documents',
      icon: 'fas fa-folder-open'
    }
  ];
}
