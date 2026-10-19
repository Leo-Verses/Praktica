import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SolicitarComponent } from './solicitude';

describe('Solicitude', () => {
  let component: SolicitarComponent;
  let fixture: ComponentFixture<SolicitarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SolicitarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SolicitarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
