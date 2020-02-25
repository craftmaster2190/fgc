import { AnswerBusService } from "../messaging/answer-bus.service";
import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { AuthService } from "src/app/view/auth/auth.service";
import { FormsModule } from "@angular/forms";
import { NgbTypeaheadModule } from "@ng-bootstrap/ng-bootstrap";
import { QuestionComponent } from "./question.component";

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
            fetchQuestion: () => Promise.resolve({})
          }
        },
        {
          provide: AuthService,
          useValue: {
            getLoggedInUser: () => {
              return {
                isAdmin: false
              };
            }
          }
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
