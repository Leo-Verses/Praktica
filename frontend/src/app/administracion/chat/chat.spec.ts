import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatAtencionComponent } from './chat';

describe('ChatAtencionComponent', () => {
  let component: ChatAtencionComponent;
  let fixture: ComponentFixture<ChatAtencionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatAtencionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChatAtencionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
