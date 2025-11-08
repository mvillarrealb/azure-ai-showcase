import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidenavNavigation, NavigationLink } from '../../../components/sidenav-navigation/sidenav-navigation';

@Component({
  selector: 'app-claim-management',
  imports: [SidenavNavigation, RouterOutlet],
  templateUrl: './claim-management.html',
  styleUrl: './claim-management.scss',
})
export class ClaimManagement {
  title = 'Gestión de Reclamos';
  
  navigationLinks: NavigationLink[] = [
    {
      label: 'Dashboard',
      route: '/claim-management/dashboard',
      icon: 'fas fa-home'
    },
    {
      label: 'Nuevo Reclamo',
      route: '/claim-management/new-claim',
      icon: 'fas fa-plus-circle'
    },
    {
      label: 'Mis Reclamos',
      route: '/claim-management/my-claims',
      icon: 'fas fa-list'
    },
    {
      label: 'En Proceso',
      route: '/claim-management/in-progress',
      icon: 'fas fa-clock'
    },
    {
      label: 'Resueltos',
      route: '/claim-management/resolved',
      icon: 'fas fa-check-circle'
    },
    {
      label: 'Documentación',
      route: '/claim-management/documentation',
      icon: 'fas fa-file-pdf'
    }
  ];
}
