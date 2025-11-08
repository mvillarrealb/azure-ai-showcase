import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SidenavNavigation } from './sidenav-navigation';

describe('SidenavNavigation', () => {
  let component: SidenavNavigation;
  let fixture: ComponentFixture<SidenavNavigation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SidenavNavigation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SidenavNavigation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
