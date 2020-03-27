import { HttpClient } from "@angular/common/http";
import { Component, OnDestroy, OnInit } from "@angular/core";
import Cropper from "cropperjs";
import { DeviceUsersService } from "../auth/device-users.service";
import { ImagesCacheService } from "../image/images-cache.service";
import { readAndCompressImage } from "browser-image-resizer";

function dataURIToBlob(dataURI) {
  // convert base64 to raw binary data held in a string
  // doesn't handle URLEncoded DataURIs - see SO answer #6850276 for code that does this
  const byteString = atob(dataURI.split(",")[1]);

  // separate out the mime component
  const mimeString = dataURI
    .split(",")[0]
    .split(":")[1]
    .split(";")[0];

  // write the bytes of the string to an ArrayBuffer
  const ab = new ArrayBuffer(byteString.length);

  // create a view into the buffer
  const ia = new Uint8Array(ab);

  // set the bytes of the buffer to the correct values
  for (let i = 0; i < byteString.length; i++) {
    ia[i] = byteString.charCodeAt(i);
  }

  // write the ArrayBuffer to a blob, and you're done
  const blob = new Blob([ab], { type: mimeString });
  return blob;
}

function blobToDataURL(blob) {
  return new Promise((resolve, reject) => {
    const fileReader = new FileReader();
    fileReader.onload = event => resolve(event.target.result);
    fileReader.onerror = err => reject(err);
    fileReader.readAsDataURL(blob);
  });
}

@Component({
  selector: "app-profile-image-editor",
  templateUrl: "./profile-image-editor.component.html",
  styleUrls: ["./profile-image-editor.component.scss"]
})
export class ProfileImageEditorComponent implements OnInit, OnDestroy {
  constructor(
    public readonly authService: DeviceUsersService,
    private readonly imagesCache: ImagesCacheService,
    private readonly http: HttpClient
  ) {}
  config = {
    aspectRatio: 1
  };

  loading: boolean;
  imageUrl: string;
  rotation: 0 | 90 | 180 | 270 = 0;
  cropper: Cropper;

  finalImage;

  ngOnInit(): void {}

  ngOnDestroy() {
    this.destroyCropper();
  }

  onFileSelected(event) {
    this.loading = true;
    this.rotation = 0;
    const fileInputNode: HTMLInputElement = event.target;
    const config = {
      maxWidth: 600,
      maxHeight: 600,
      autoRotate: true,
      debug: true,
      mimeType: "image/png"
    };

    return readAndCompressImage(fileInputNode.files[0], config)
      .then(resizedImage => blobToDataURL(resizedImage))
      .then(imageToUpload => {
        this.imageUrl = imageToUpload;
        this.showCropper();
      })
      .then(
        () => {
          this.loading = false;
          fileInputNode.value = "";
        },
        err => {
          this.loading = false;
          fileInputNode.value = "";
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

    const config = {
      maxWidth: 100,
      maxHeight: 100,
      debug: true,
      mimeType: "image/png"
    };

    return readAndCompressImage(dataURIToBlob(this.finalImage), config)
      .then(resizedImage => blobToDataURL(resizedImage))
      .then(imageToUpload2 => {
        return this.http.post("/api/user/profile", imageToUpload2).toPromise();
      })
      .then(
        () => {
          this.imagesCache.invalidate(
            "/api/user/profile/" + this.authService.getCurrentUser().id
          );
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
