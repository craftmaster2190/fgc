import { ValidTextComponent } from "./valid-text.component";
import { async, ComponentFixture, TestBed } from "@angular/core/testing";

describe("ValidTextComponent", () => {
  let component: ValidTextComponent;
  let fixture: ComponentFixture<ValidTextComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ValidTextComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ValidTextComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
