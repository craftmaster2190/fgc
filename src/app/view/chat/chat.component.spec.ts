import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { ChatComponent } from "./chat.component";
import { ChatBusService } from "./chat-bus.service";
import { ToastService } from "../util/toast/toast.service";
import { AuthService } from "../auth/auth.service";

describe("ChatComponent", () => {
  let component: ChatComponent;
  let fixture: ComponentFixture<ChatComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ChatComponent],
      providers: [
        { provide: AuthService, useValue: {} },
        { provide: ChatBusService, useValue: { listen: () => {} } },
        { provide: ToastService, useValue: {} }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
