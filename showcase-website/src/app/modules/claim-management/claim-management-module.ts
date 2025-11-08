import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClaimManagementRoutingModule } from './claim-management-routing-module';
import { ClaimManagement } from './claim-management/claim-management';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ClaimManagementRoutingModule,
    ClaimManagement
  ]
})
export class ClaimManagementModule { }
