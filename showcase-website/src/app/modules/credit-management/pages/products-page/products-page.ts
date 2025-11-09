import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductsCrudComponent } from '../../components/products-crud/products-crud';
import { ProductFormModalComponent } from '../../components/product-form-modal/product-form-modal';

@Component({
  selector: 'app-products-page',
  standalone: true,
  imports: [CommonModule, ProductsCrudComponent, ProductFormModalComponent],
  templateUrl: './products-page.html'
})
export class ProductsPageComponent {
  showModal = signal(false);

  /**
   * Manejar cuando se crea un nuevo producto
   */
  onProductCreated(): void {
    // El modal se cierra automáticamente, aquí podríamos recargar los datos si fuera necesario
    console.log('Producto creado exitosamente');
  }
}