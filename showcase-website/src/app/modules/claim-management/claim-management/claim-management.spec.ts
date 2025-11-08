import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClaimManagement } from './claim-management';

describe('ClaimManagement', () => {
  let component: ClaimManagement;
  let fixture: ComponentFixture<ClaimManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClaimManagement]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClaimManagement);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
