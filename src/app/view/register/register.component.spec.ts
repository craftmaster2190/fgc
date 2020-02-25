import { async, ComponentFixture, TestBed } from "@angular/core/testing";
import { AuthService } from "../auth/auth.service";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { FamilySearchService } from "./family-search.service";
import { FormsModule } from "@angular/forms";
import { NgbTypeaheadModule } from "@ng-bootstrap/ng-bootstrap";
import { RegisterComponent } from "./register.component";
import { RouterTestingModule } from "@angular/router/testing";
import { ToastService } from "../util/toast/toast.service";

describe("RegisterComponent", () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: { logout: () => void 0 } },
        { provide: FamilySearchService, useValue: {} },
        { provide: ToastService, useValue: {} }
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
