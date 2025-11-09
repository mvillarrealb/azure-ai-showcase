import { Component, inject, signal, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClaimService } from '../../services/claim.service';
import { ImportResponse } from '../../interfaces/claim.interface';

@Component({
  selector: 'app-claims-import',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './claims-import.html',
  styleUrls: ['./claims-import.scss']
})
export class ClaimsImportComponent {
  private claimService = inject(ClaimService);
  private router = inject(Router);

  @Output() importCompleted = new EventEmitter<void>();

  // Estado del componente
  isUploading = signal<boolean>(false);
  uploadProgress = signal<number>(0);
  selectedFile = signal<File | null>(null);
  importResult = signal<ImportResponse | null>(null);
  errorMessage = signal<string>('');

  /**
   * Maneja la selección de archivo
   */
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    
    if (file) {
      // Validar tipo de archivo
      if (!this.isValidExcelFile(file)) {
        this.errorMessage.set('Por favor seleccione un archivo Excel válido (.xlsx, .xls)');
        this.selectedFile.set(null);
        return;
      }
      
      // Validar tamaño (máximo 10MB)
      if (file.size > 10 * 1024 * 1024) {
        this.errorMessage.set('El archivo es demasiado grande. Máximo permitido: 10MB');
        this.selectedFile.set(null);
        return;
      }
      
      this.selectedFile.set(file);
      this.errorMessage.set('');
      this.importResult.set(null);
    }
  }

  /**
   * Valida si es un archivo Excel válido
   */
  private isValidExcelFile(file: File): boolean {
    const validTypes = [
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
      'application/vnd.ms-excel', // .xls
      'application/excel'
    ];
    
    const validExtensions = ['.xlsx', '.xls'];
    const fileName = file.name.toLowerCase();
    
    return validTypes.includes(file.type) || 
           validExtensions.some(ext => fileName.endsWith(ext));
  }

  /**
   * Ejecuta la importación del archivo
   */
  onImport() {
    const file = this.selectedFile();
    
    if (!file) {
      this.errorMessage.set('Por favor seleccione un archivo');
      return;
    }

    this.isUploading.set(true);
    this.uploadProgress.set(0);
    this.errorMessage.set('');

    // Simular progreso de upload (en una implementación real esto vendría del servicio)
    const progressInterval = setInterval(() => {
      const current = this.uploadProgress();
      if (current < 90) {
        this.uploadProgress.set(current + 10);
      }
    }, 200);

    this.claimService.importClaims(file).subscribe({
      next: (result) => {
        clearInterval(progressInterval);
        this.uploadProgress.set(100);
        this.importResult.set(result);
        this.isUploading.set(false);
        
        // Emit completion event
        this.importCompleted.emit();
        
        console.log('Importación completada:', result);
      },
      error: (error) => {
        clearInterval(progressInterval);
        this.uploadProgress.set(0);
        this.isUploading.set(false);
        this.errorMessage.set(
          error.error?.message || 'Error al procesar el archivo. Verifique el formato e intente nuevamente.'
        );
        console.error('Error en importación:', error);
      }
    });
  }

  /**
   * Limpia el formulario para nueva carga
   */
  onReset() {
    this.selectedFile.set(null);
    this.importResult.set(null);
    this.errorMessage.set('');
    this.uploadProgress.set(0);
    this.isUploading.set(false);
    
    // Limpiar input file
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  /**
   * Navega a la lista de reclamos
   */
  onViewClaims() {
    this.router.navigate(['/claim-management/claims']);
  }

  /**
   * Formatea el tamaño del archivo
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  /**
   * Descarga archivo de plantilla Excel
   */
  onDownloadTemplate() {
    console.log('Descargar plantilla Excel');
    
    const templateInfo = `
Formato de archivo Excel requerido:

Columnas obligatorias (en este orden):
- A: date (YYYY-MM-DD HH:MM:SS)
- B: amount (número decimal)
- C: identityDocument (texto)
- D: description (texto, mín 10 caracteres)
- E: reason (texto)
- F: subReason (texto)

Ejemplo:
2024-11-08 10:30:00 | 1500.75 | 12345678 | Cargo no autorizado | Cargo indebido | Transacción no autorizada
    `;
    
    alert(templateInfo);
  }
}