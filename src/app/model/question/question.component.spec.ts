import { QuestionComponent } from "./question.component";
import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { AnswerBusService } from "../messaging/answer-bus.service";
import { AuthService } from "src/app/view/auth/auth.service";
import { NgbTypeaheadModule } from "@ng-bootstrap/ng-bootstrap";
import { FormsModule } from "@angular/forms";

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
            fetchQuestion: () => Promise.resolve()
          }
        },
        { provide: AuthService, useValue: { getLoggedInUser: () => ({}) } }
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
