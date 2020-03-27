import { Component, NgZone, OnDestroy, OnInit } from "@angular/core";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { interval, Observable, Subject, Subscription } from "rxjs";
import {
  debounce,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  switchMap,
  tap
} from "rxjs/operators";
import { DeviceUsersService } from "../auth/device-users.service";

@Component({
  selector: "app-nav-header",
  templateUrl: "./nav-header.component.html",
  styleUrls: ["./nav-header.component.scss"]
})
export class NavHeaderComponent implements OnInit, OnDestroy {
  constructor(
    public readonly authService: DeviceUsersService,
    private readonly modalService: NgbModal,
    private readonly ngZone: NgZone
  ) {}
  name: string;
  family: string;
  canChangeFamily: boolean;
  readonly updateUserSubject = new Subject();
  private subscription: Subscription;

  searchingFamilies: boolean;

  loadingImage: boolean;
  canvasImage: HTMLImageElement;
  rotation = 0;

  ngOnInit() {
    this.subscription = this.updateUserSubject
      .pipe(debounce(() => interval(300)))
      .subscribe(() =>
        this.authService.updateUser({ name: this.name, family: this.family })
      );

    this.authService.canChangeFamily().subscribe(data => {
      this.canChangeFamily = data;
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  openModal(content) {
    if (this.authService.getCurrentUser()) {
      this.name = this.getUsername();
      this.family = this.getFamilyName();
      this.modalService.open(content);
    }
  }

  openAbout(content) {
    this.modalService.open(content);
  }

  getUsername() {
    return this.authService.getCurrentUser()?.name || "";
  }

  getFamilyName() {
    return this.authService.getCurrentUser()?.family?.name || "";
  }

  logout(modal) {
    modal.dismiss("Logout");
    this.authService.logoutUser();
  }

  searchFamilies = (text$: Observable<string>) => {
    return text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      filter(term => term && term.length >= 2),
      tap(() => (this.searchingFamilies = true)),
      switchMap(term =>
        this.authService
          .searchFamilies(term)
          .pipe(map(family => family.map(f => f.name)))
      ),
      tap(() => (this.searchingFamilies = false))
    );
  };

  nameValid() {
    return (this.name?.trim()?.length || 0) >= 2;
  }

  familyValid() {
    return (this.family?.trim()?.length || 0) >= 4;
  }

  onFileSelected(event) {
    this.loadingImage = true;
    this.rotation = 0;
    const fileInputNode: HTMLInputElement = event.target;
    new Promise<string>((resolve, reject) => {
      const fileReader = new FileReader();
      fileReader.onload = () => resolve(fileReader.result as string);
      fileReader.onerror = err => reject(err);
      fileReader.readAsDataURL(fileInputNode.files[0]);
    })
      .then(
        imageToUpload =>
          new Promise((resolve, reject) => {
            const imageObj = new Image();
            imageObj.onload = () => {
              this.canvasImage = imageObj;
              this.drawImage(false);
              resolve();
            };
            imageObj.onerror = err => reject(err);
            imageObj.src = imageToUpload;
          })
      )
      .then(
        () => (this.loadingImage = false),
        err => {
          this.loadingImage = false;
          return Promise.reject(err);
        }
      );
  }

  drawImage(rotate = true) {
    this.ngZone.runOutsideAngular(() => {
      if (rotate) {
        this.rotation += 90;
        this.rotation = this.rotation % 360;
      }

      const canvas = document.querySelector(
        "#uploadCanvas"
      ) as HTMLCanvasElement;
      const context = canvas.getContext("2d");

      context.clearRect(0, 0, canvas.width, canvas.height);

      context.save();

      context.translate(canvas.width / 2, canvas.height / 2);
      context.rotate((this.rotation * Math.PI) / 180);
      const widthByHeightRatio =
        this.canvasImage.width / this.canvasImage.height;
      let newWidth = canvas.width;
      let newHeight = newWidth / widthByHeightRatio;
      if (newHeight > canvas.height) {
        newHeight = canvas.height;
        newWidth = newHeight * widthByHeightRatio;
      }
      context.drawImage(
        this.canvasImage,
        -canvas.width / 2,
        -canvas.height / 2,
        newWidth,
        newHeight
      );

      context.restore();
    });
  }
}
