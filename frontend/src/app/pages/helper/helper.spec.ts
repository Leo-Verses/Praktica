import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SoporteComponent } from './helper';

describe('Helper', () => {
  let component: SoporteComponent;
  let fixture: ComponentFixture<SoporteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SoporteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SoporteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
