import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ClaimEventsService {
  private refreshSubject = new Subject<void>();
  
  // Observable para que los componentes se suscriban a eventos de refresh
  refresh$ = this.refreshSubject.asObservable();
  
  // Método para emitir evento de refresh
  emitRefresh() {
    this.refreshSubject.next();
  }
}