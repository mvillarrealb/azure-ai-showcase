import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalFinance } from './personal-finance';

describe('PersonalFinance', () => {
  let component: PersonalFinance;
  let fixture: ComponentFixture<PersonalFinance>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonalFinance]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PersonalFinance);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
