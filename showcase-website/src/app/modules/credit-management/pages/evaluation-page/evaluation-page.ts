import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreditEvaluationWizardComponent } from '../../components/credit-evaluation-wizard/credit-evaluation-wizard';

@Component({
  selector: 'app-evaluation-page',
  standalone: true,
  imports: [CommonModule, CreditEvaluationWizardComponent],
  templateUrl: './evaluation-page.html'
})
export class EvaluationPageComponent {
  // Esta página es simple, solo contiene el wizard
}