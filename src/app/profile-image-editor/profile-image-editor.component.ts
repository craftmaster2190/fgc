import { Component, OnInit, ViewChild, NgZone, OnDestroy } from "@angular/core";
import ResizeImage from "image-resize";
import Cropper from "cropperjs";
import { HttpClient } from "@angular/common/http";
import { DeviceUsersService } from "../auth/device-users.service";
@Component({
  selector: "app-profile-image-editor",
  templateUrl: "./profile-image-editor.component.html",
  styleUrls: ["./profile-image-editor.component.scss"]
})
export class ProfileImageEditorComponent implements OnInit, OnDestroy {
  config = {
    aspectRatio: 1
  };

  constructor(
    public readonly authService: DeviceUsersService,
    private readonly http: HttpClient
  ) {}

  ngOnInit(): void {}

  ngOnDestroy() {
    this.destroyCropper();
  }

  loading: boolean;
  imageUrl: string;
  rotation: 0 | 90 | 180 | 270 = 0;
  cropper: Cropper;

  finalImage;

  onFileSelected(event) {
    this.loading = true;
    this.rotation = 0;
    const fileInputNode: HTMLInputElement = event.target;
    const resizeImage = new ResizeImage({
      format: "png",
      width: 1000
    });
    return resizeImage
      .play(fileInputNode)
      .then(imageToUpload => {
        this.imageUrl = imageToUpload;
        this.showCropper();
      })
      .then(
        () => (this.loading = false),
        err => {
          this.loading = false;
          return Promise.reject(err);
        }
      );
  }

  showCropper() {
    this.destroyCropper();
    const cropperJsHolder = document.querySelector("#cropperjs-holder");
    cropperJsHolder.innerHTML = `<img src="${this.imageUrl}" style="width: 100%;">`;
    const img = cropperJsHolder.querySelector("img");
    const max = img.width > img.height ? img.width : img.height;
    img.style.width = `${max}px`;
    img.style.height = `${max}px`;

    this.cropper = new Cropper(img, {
      zoomable: false,
      scalable: false,
      autoCropArea: 1,
      aspectRatio: 1,
      crop: () => {
        const canvas = this.cropper.getCroppedCanvas();
        this.finalImage = canvas.toDataURL("image/png");
      }
    });
  }

  destroyCropper() {
    this.cropper?.destroy();
  }

  rotate() {
    this.cropper.rotate(90);
  }

  upload() {
    this.loading = true;
    this.cropper.disable();
    const resizeImage = new ResizeImage({
      format: "png",
      width: 100
    });
    return resizeImage
      .play(this.finalImage)
      .then(imageToUpload =>
        this.http.post("/api/user/profile", imageToUpload).toPromise()
      )
      .then(
        () => {
          this.authService.getCurrentUser().hasProfileImage = true;
          this.loading = false;
          this.cancel();
        },
        err => {
          this.loading = false;
          this.cropper.enable();
          return Promise.reject(err);
        }
      );
  }

  cancel() {
    this.destroyCropper();
    document.querySelector("#cropperjs-holder").innerHTML = "";
    this.finalImage = null;
  }
}
