import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { NgbTypeaheadModule } from "@ng-bootstrap/ng-bootstrap";
import { DeviceUsersService } from "src/app/auth/device-users.service";
import { AnswerBusService } from "../messaging/answer-bus.service";
import { QuestionComponent } from "./question.component";
import { ImagesService } from "../answers/images.service";

describe("QuestionComponent", () => {
  let component: QuestionComponent;
  let fixture: ComponentFixture<QuestionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [QuestionComponent],
      providers: [
        {
          provide: AnswerBusService,
          useValue: {
            getSelectedAnswers: () => new Set(),
            getAnswer: () => void 0,
            getQuestion: () => {}
          }
        },
        {
          provide: DeviceUsersService,
          useValue: {
            getCurrentUser: () => ({})
          }
        },
        {
          provide: ImagesService,
          useValue: {}
        }
      ],
      imports: [NgbTypeaheadModule, FormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
