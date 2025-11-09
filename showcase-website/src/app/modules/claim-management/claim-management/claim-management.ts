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
      label: 'Reclamos',
      route: '/claim-management/claims',
      icon: 'fas fa-list'
    },
    {
      label: 'Carga Masiva',
      route: '/claim-management/import',
      icon: 'fas fa-file-upload'
    },
    {
      label: 'Reportes',
      route: '/claim-management/reports',
      icon: 'fas fa-chart-bar'
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
    this.selectedClaim.set(claim);
    this.showResolveModal.set(true);
  }

  onHideResolveModal() {
    this.showResolveModal.set(false);
    this.selectedClaim.set(null);
  }

  onClaimCreated() {
    this.showCreateModal.set(false);
    // Aquí se podría emitir un evento para refrescar la lista
  }

  onClaimResolved() {
    this.showResolveModal.set(false);
    this.selectedClaim.set(null);
    // Aquí se podría emitir un evento para refrescar la lista
  }

  onRouteActivated(component: any) {
    // Suscribirse a eventos de los componentes hijos
    if (component && component.showCreateForm) {
      component.showCreateForm.subscribe(() => {
        this.onShowCreateModal();
      });
    }
    
    if (component && component.showResolveForm) {
      component.showResolveForm.subscribe((claim: Claim) => {
        this.onShowResolveModal(claim);
      });
    }
  }
}
