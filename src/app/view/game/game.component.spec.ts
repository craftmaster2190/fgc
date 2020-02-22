import { GameComponent } from "./game.component";
import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { AnswersService } from "src/app/model/answers/answers.service";
import { AnswerBusService } from "src/app/model/messaging/answer-bus.service";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";

describe("GameComponent", () => {
  let component: GameComponent;
  let fixture: ComponentFixture<GameComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GameComponent],
      providers: [
        {
          provide: AnswersService,
          useValue: { getAll: () => Promise.resolve() }
        },
        {
          provide: AnswerBusService,
          useValue: {
            getAll: () => Promise.resolve(),
            getAnswer: () => void 0
          }
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
