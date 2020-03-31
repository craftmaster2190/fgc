import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { of } from "rxjs";
import { AnswerBusService } from "../messaging/answer-bus.service";
import { SectionComponent } from "./section.component";

describe("SectionComponent", () => {
  let component: SectionComponent;
  let fixture: ComponentFixture<SectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SectionComponent],
      providers: [
        {
          provide: AnswerBusService,
          useValue: {
            getAnswer: () => void 0,
            listenForQuestionsAndAnswers: () => of().subscribe(),
            getLoadedFirstAnswer: () => true
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
