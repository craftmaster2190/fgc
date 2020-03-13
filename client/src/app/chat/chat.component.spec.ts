import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { of } from "rxjs";
import { DeviceUsersService } from "../auth/device-users.service";
import { ToastService } from "../toast/toast.service";
import { ChatBusService } from "./chat-bus.service";
import { ChatComponent } from "./chat.component";
import { UserUpdatesService } from "../auth/user-updates.service";
import { Optional } from "../util/optional";

describe("ChatComponent", () => {
  let component: ChatComponent;
  let fixture: ComponentFixture<ChatComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ChatComponent],
      providers: [
        {
          provide: UserUpdatesService,
          useValue: { getIfPresent: () => Optional.of() }
        },
        {
          provide: ChatBusService,
          useValue: { listen: () => of().subscribe() }
        },
        {
          provide: DeviceUsersService,
          useValue: { getCurrentUser: () => ({}) }
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
