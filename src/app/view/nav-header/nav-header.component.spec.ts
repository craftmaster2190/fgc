import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { AuthService } from "../auth/auth.service";
import { NavHeaderComponent } from "./nav-header.component";
import { RouterTestingModule } from "@angular/router/testing";

describe("NavHeaderComponent", () => {
  let component: NavHeaderComponent;
  let fixture: ComponentFixture<NavHeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NavHeaderComponent],
      providers: [
        {
          provide: AuthService,
          useValue: {
            getLoggedInUser: () => {
              return {
                isAdmin: false,
                family: {
                  name: "someName"
                }
              };
            }
          }
        }
      ],
      imports: [RouterTestingModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
