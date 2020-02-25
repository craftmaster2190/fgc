import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { FormsModule } from "@angular/forms";
import { RouterTestingModule } from "@angular/router/testing";
import { AuthService } from "../auth/auth.service";
import { ToastService } from "../util/toast/toast.service";
import { FamilySearchService } from "./family-search.service";
import { RegisterComponent } from "./register.component";
import { NgbTypeaheadModule } from "@ng-bootstrap/ng-bootstrap";

describe("RegisterComponent", () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: { logout: () => void 0 } },
        { provide: ToastService, useValue: {} },
        { provide: FamilySearchService, useValue: {} }
      ],
      imports: [RouterTestingModule, NgbTypeaheadModule, FormsModule],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
