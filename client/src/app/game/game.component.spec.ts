import { AnswerBusService } from "src/app/messaging/answer-bus.service";
import { AnswersService } from "src/app/answers/answers.service";
import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { GameComponent } from "./game.component";
import { of } from "rxjs";
import { UserUpdatesService } from "../auth/user-updates.service";
import { DeviceUsersService } from "../auth/device-users.service";
import { ServerMessagesService } from "./server-messages.service";

describe("GameComponent", () => {
  let component: GameComponent;
  let fixture: ComponentFixture<GameComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GameComponent],
      providers: [
        AnswersService,
        {
          provide: DeviceUsersService,
          useValue: { getCurrentUser: () => ({}) }
        },
        {
          provide: AnswerBusService,
          useValue: {
            getAnswer: () => void 0,
            listenForQuestionsAndAnswers: () => of().subscribe(),
            getLoadedFirstAnswer: () => true
          }
        },
        {
          provide: UserUpdatesService,
          useValue: { startListener: () => of().subscribe() }
        },
        {
          provide: ServerMessagesService,
          useValue: { subscribe: () => of().subscribe() }
        }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
