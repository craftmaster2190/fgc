import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { ImageComponent } from "./image.component";
import { ImagesCacheService } from "./images-cache.service";
import { of } from "rxjs";

describe("ImageComponent", () => {
  let component: ImageComponent;
  let fixture: ComponentFixture<ImageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ImageComponent],
      providers: [
        { provide: ImagesCacheService, useValue: { get: () => of() } }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
