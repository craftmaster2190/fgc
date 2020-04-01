import {
  Component,
  ErrorHandler,
  NgZone,
  OnDestroy,
  OnInit
} from "@angular/core";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import {
  interval,
  Observable,
  Subject,
  Subscription,
  of,
  throwError
} from "rxjs";
import {
  debounce,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  switchMap,
  tap,
  catchError,
  retry
} from "rxjs/operators";
import { DeviceUsersService } from "../auth/device-users.service";
import { RecoverService } from "../welcome/recover.service";

@Component({
  selector: "app-nav-header",
  templateUrl: "./nav-header.component.html",
  styleUrls: ["./nav-header.component.scss"]
})
export class NavHeaderComponent implements OnInit, OnDestroy {
  constructor(
    public readonly authService: DeviceUsersService,
    private readonly modalService: NgbModal,
    private readonly ngZone: NgZone,
    private readonly sentry: ErrorHandler,
    private readonly recoverService: RecoverService
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

  warning: any;
  recoveryCode: string;

  ngOnInit() {
    this.subscription = this.updateUserSubject
      .pipe(
        tap(() => (this.warning = null)),
        debounce(() => interval(300)),
        switchMap(() =>
          this.authService.updateUser({ name: this.name, family: this.family })
        ),
        catchError(err => {
          this.assignWarning(err);
          this.name = this.authService.getCurrentUser().name;
          this.family = this.authService.getCurrentUser()?.family?.name;
          return of();
        })
      )
      .subscribe();

    this.authService.canChangeFamily().subscribe(data => {
      this.canChangeFamily = data;
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  getWarning() {
    return this.warning;
  }

  assignWarning(err) {
    this.sentry.handleError(err);
    this.warning = err?.error?.message;
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
      tap(() => (this.warning = null)),
      debounceTime(200),
      distinctUntilChanged(),
      filter(term => term && term.length >= 2),
      tap(() => (this.searchingFamilies = true)),
      switchMap(term =>
        this.authService.searchFamilies(term).pipe(
          map(family => family.map(f => f.name)),
          catchError(err => {
            this.assignWarning(err);
            return throwError(err);
          })
        )
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

  getRecoveryCode() {
    this.recoverService
      .generateRecoveryCode()
      .subscribe(recoveryCode => (this.recoveryCode = recoveryCode));
  }
}
