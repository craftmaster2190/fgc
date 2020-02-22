import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { AuthService } from "../auth/auth.service";
import { ToastService } from "../util/toast/toast.service";
import { ChatBusService } from "./chat-bus.service";
import { ChatComponent } from "./chat.component";
import { of } from "rxjs";
import { FormsModule } from "@angular/forms";

describe("ChatComponent", () => {
  let component: ChatComponent;
  let fixture: ComponentFixture<ChatComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ChatComponent],
      providers: [
        {
          provide: ChatBusService,
          useValue: { listen: () => of().subscribe() }
        },
        {
          provide: AuthService,
          useValue: {}
        },
        {
          provide: ToastService,
          useValue: {}
        }
      ],
      imports: [FormsModule]
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
