import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

export interface NavigationLink {
  label: string;
  route: string;
  icon?: string;
}

@Component({
  selector: 'app-sidenav-navigation',
  imports: [CommonModule],
  templateUrl: './sidenav-navigation.html',
  styleUrl: './sidenav-navigation.scss',
})
export class SidenavNavigation {
  @Input() title: string = 'Mi Aplicación';
  @Input() navigationLinks: NavigationLink[] = [];
  @Input() headerClasses: string = 'bg-white border-gray-200';
  @Input() showBackButton: boolean = false;
  @Input() backButtonIcon: string = 'fas fa-arrow-left';
  @Input() sidebarIcon: string = 'fas fa-wallet';
  @Input() sidebarIconClasses: string = 'bg-gradient-to-br from-blue-500 to-purple-600';

  isSidenavOpen = false;

  constructor(private router: Router) {}

  toggleSidenav(): void {
    this.isSidenavOpen = !this.isSidenavOpen;
  }

  closeSidenav(): void {
    this.isSidenavOpen = false;
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
    this.closeSidenav();
  }

  goBack(): void {
    window.history.back();
  }
}
