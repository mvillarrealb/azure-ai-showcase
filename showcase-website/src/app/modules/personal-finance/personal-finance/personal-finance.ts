import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidenavNavigation, NavigationLink } from '../../../components/sidenav-navigation/sidenav-navigation';
import { PersonalFinanceFormComponent } from '../components/personal-finance-form/personal-finance-form';

@Component({
  selector: 'app-personal-finance',
  standalone: true,
  imports: [SidenavNavigation, RouterOutlet, PersonalFinanceFormComponent],
  templateUrl: './personal-finance.html',
  styleUrl: './personal-finance.scss',
})
export class PersonalFinance {
  title = 'Gestión de Finanzas Personales';
  
  // Modal state
  showModal = signal<boolean>(false);
  
  // Links simplificados - Nueva Transacción es modal ahora
  navigationLinks: NavigationLink[] = [
    {
      label: 'Transacciones',
      route: '/personal-finance/transacciones',
      icon: 'fas fa-list'
    },
    {
      label: 'Reportes',
      route: '/personal-finance/reportes',
      icon: 'fas fa-chart-bar'
    },
    {
      label: 'Importar PDF',
      route: '/personal-finance/importar',
      icon: 'fas fa-file-upload'
    }
  ];

  // Modal event handlers
  onShowModal() {
    this.showModal.set(true);
  }

  onHideModal() {
    this.showModal.set(false);
  }

  onCloseModal() {
    this.showModal.set(false);
  }

  onTransactionCreated() {
    this.showModal.set(false);
    // Aquí se podría emitir un evento para refrescar la lista
  }

  onRouteActivated(component: any) {
    // Suscribirse al evento showTransactionForm si el componente lo tiene
    if (component && component.showTransactionForm) {
      component.showTransactionForm.subscribe(() => {
        this.onShowModal();
      });
    }
  }
}
