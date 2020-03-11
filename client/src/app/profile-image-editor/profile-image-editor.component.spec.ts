import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { ProfileImageEditorComponent } from "./profile-image-editor.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { DeviceUsersService } from "../auth/device-users.service";
import { ImageComponent } from "../image/image.component";

describe("ProfileImageEditorComponent", () => {
  let component: ProfileImageEditorComponent;
  let fixture: ComponentFixture<ProfileImageEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProfileImageEditorComponent, ImageComponent],
      providers: [
        {
          provide: DeviceUsersService,
          useValue: { getCurrentUser: () => ({}) }
        }
      ],
      imports: [HttpClientTestingModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfileImageEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
