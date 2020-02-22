import { LoginComponent } from "./login.component";
import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { AuthService } from "../auth/auth.service";
import { RouterTestingModule } from "@angular/router/testing";
import { FormsModule } from "@angular/forms";

describe("LoginComponent", () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        {
          provide: AuthService,
          useValue: { getBasicAuth: () => void 0 }
        }
      ],
      imports: [RouterTestingModule, FormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
