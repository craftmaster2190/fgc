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
  name: string;
  family: string;
  isFamilyChangeEnabled: boolean;
  readonly updateUserSubject = new Subject();
  private subscription: Subscription;

  constructor(
    public readonly authService: DeviceUsersService,
    private readonly modalService: NgbModal,
    private readonly ngZone: NgZone
  ) {}

  ngOnInit() {
    this.subscription = this.updateUserSubject
      .pipe(debounce(() => interval(300)))
      .subscribe(() =>
        this.authService.updateUser({ name: this.name, family: this.family })
      );
    this.authService.getFamilyChangeEnabled().subscribe(data => {
      this.isFamilyChangeEnabled = data;
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

  disableFamily() {
    // allow an admin to override during game play
    if (this.isFamilyChangeEnabled) {
      // remember to NOT disable
      return false;
    }
    // Date(year, month, day, hours, minutes, seconds, milliseconds)
    // month is 0 based, jan = 0
    // so just before midnight april 4th, we disable.
    return new Date() > new Date(2020, 3, 4, 23, 59, 59, 0);
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

  searchingFamilies: boolean;

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
    return (this.name?.length || 0) >= 2;
  }

  familyValid() {
    return (this.family?.length || 0) >= 4;
  }

  loadingImage: boolean;
  canvasImage: HTMLImageElement;
  rotation = 0;

  onFileSelected(event) {
    this.loadingImage = true;
    this.rotation = 0;
    const fileInputNode: HTMLInputElement = event.target;
    new Promise<string>((resolve, reject) => {
      var fileReader = new FileReader();
      fileReader.onload = () => resolve(fileReader.result as string);
      fileReader.onerror = err => reject(err);
      fileReader.readAsDataURL(fileInputNode.files[0]);
    })
      .then(
        imageToUpload =>
          new Promise((resolve, reject) => {
            var imageObj = new Image();
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

      // save the unrotated context of the canvas so we can restore it later
      // the alternative is to untranslate & unrotate after drawing
      context.save();

      context.translate(canvas.width / 2, canvas.height / 2);
      context.rotate((this.rotation * Math.PI) / 180);
      var wrh = this.canvasImage.width / this.canvasImage.height;
      var newWidth = canvas.width;
      var newHeight = newWidth / wrh;
      if (newHeight > canvas.height) {
        newHeight = canvas.height;
        newWidth = newHeight * wrh;
      }
      context.drawImage(
        this.canvasImage,
        -canvas.width / 2,
        -canvas.height / 2,
        newWidth,
        newHeight
      );

      // we’re done with the rotating so restore the unrotated context
      context.restore();
    });
  }
}
