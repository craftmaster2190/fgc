import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { ScoreboardComponent } from "./scoreboard.component";
import { ScoresService } from "./scores.service";
import { AuthService } from "../auth/auth.service";

describe("ScoreboardComponent", () => {
  let component: ScoreboardComponent;
  let fixture: ComponentFixture<ScoreboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ScoreboardComponent],
      providers: [
        {
          provide: ScoresService,
          useValue: {
            getUserCount: () => Promise.resolve(),
            get: () => Promise.resolve({})
          }
        },
        {
          provide: AuthService,
          useValue: {
            getLoggedInUser: () => ({})
          }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScoreboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
