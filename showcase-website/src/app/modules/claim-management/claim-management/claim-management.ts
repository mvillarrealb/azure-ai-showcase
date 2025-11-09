import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidenavNavigation, NavigationLink } from '../../../components/sidenav-navigation/sidenav-navigation';
import { ClaimFormComponent } from '../components/claim-form/claim-form';
import { ResolveClaimComponent } from '../components/resolve-claim/resolve-claim';
import { Claim } from '../interfaces/claim.interface';

@Component({
  selector: 'app-claim-management',
  standalone: true,
  imports: [SidenavNavigation, RouterOutlet, ClaimFormComponent, ResolveClaimComponent],
  templateUrl: './claim-management.html',
  styleUrl: './claim-management.scss',
})
export class ClaimManagement {
  title = 'Gestión de Reclamos';
  
  // Modal states siguiendo patrón de personal-finance
  showCreateModal = signal<boolean>(false);
  showResolveModal = signal<boolean>(false);
  selectedClaim = signal<Claim | null>(null);
  
  // Navigation links actualizados según funcionalidades implementadas
  navigationLinks: NavigationLink[] = [
    {
      label: 'Gestión de Reclamos',
      route: '/claim-management/claims',
      icon: 'fas fa-list'
    },
    {
      label: 'Importación Masiva',
      route: '/claim-management/import',
      icon: 'fas fa-file-upload'
    }
  ];

  // Modal event handlers
  onShowCreateModal() {
    this.showCreateModal.set(true);
  }

  onHideCreateModal() {
    this.showCreateModal.set(false);
  }

  onShowResolveModal(claim: Claim) {
    console.log('onShowResolveModal called with:', claim);
    this.selectedClaim.set(claim);
    this.showResolveModal.set(true);
    console.log('Modal should be visible now, showResolveModal:', this.showResolveModal());
  }

  onHideResolveModal() {
    this.showResolveModal.set(false);
    this.selectedClaim.set(null);
  }

  onClaimCreated() {
    this.showCreateModal.set(false);
    // Refrescar datos en el componente activo
    this.refreshActiveComponent();
  }

  onClaimResolved() {
    this.showResolveModal.set(false);
    this.selectedClaim.set(null);
    // Refrescar datos en el componente activo
    this.refreshActiveComponent();
  }

  private refreshActiveComponent() {
    // Buscar el componente activo y refrescar sus datos si tiene el método
    const activeComponent = this.getActiveComponent();
    if (activeComponent && typeof activeComponent.refreshData === 'function') {
      activeComponent.refreshData();
    }
  }

  private getActiveComponent(): any {
    // En una implementación más robusta, se podría mantener una referencia al componente activo
    // Por simplicidad, haremos un setTimeout para permitir que el modal se cierre primero
    return null; // Se puede mejorar implementando ViewChild en los componentes de ruta
  }

  onRouteActivated(component: any) {
    console.log('Route activated with component:', component);
    
    // Suscribirse a eventos de los componentes hijos
    if (component && component.showCreateForm) {
      console.log('Subscribing to showCreateForm');
      component.showCreateForm.subscribe(() => {
        this.onShowCreateModal();
      });
    }
    
    if (component && component.showResolveForm) {
      console.log('Subscribing to showResolveForm');
      component.showResolveForm.subscribe((claim: Claim) => {
        console.log('showResolveForm event received with claim:', claim);
        this.onShowResolveModal(claim);
      });
    }
  }
}
