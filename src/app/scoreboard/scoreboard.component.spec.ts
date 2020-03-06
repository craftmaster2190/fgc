import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { DeviceUsersService } from "../auth/device-users.service";
import { ScoreboardComponent } from "./scoreboard.component";
import { ScoresService } from "./scores.service";
import { of } from "rxjs";

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
            listenToScores: () => of().subscribe()
          }
        },
        {
          provide: DeviceUsersService,
          useValue: {
            getCurrentUser: () => ({})
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
