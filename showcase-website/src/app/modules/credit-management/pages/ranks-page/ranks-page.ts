import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RanksCrudComponent } from '../../components/ranks-crud/ranks-crud';
import { RankFormModalComponent } from '../../components/rank-form-modal/rank-form-modal';

@Component({
  selector: 'app-ranks-page',
  standalone: true,
  imports: [CommonModule, RanksCrudComponent, RankFormModalComponent],
  templateUrl: './ranks-page.html'
})
export class RanksPageComponent {
  showModal = signal(false);

  /**
   * Manejar cuando se crea un nuevo rango
   */
  onRankCreated(): void {
    // El modal se cierra automáticamente, aquí podríamos recargar los datos si fuera necesario
    console.log('Rango creado exitosamente');
  }
}