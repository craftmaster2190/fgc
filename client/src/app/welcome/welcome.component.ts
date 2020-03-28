import { Component, ErrorHandler, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { Observable, throwError } from "rxjs";
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  switchMap,
  tap
} from "rxjs/operators";
import { DeviceUsersService } from "../auth/device-users.service";
import { User } from "../auth/user";
import { UserGroup } from "../auth/user-group";
import { Optional } from "../util/optional";
import timeout from "../util/timeout";
import { BrowserDetectService } from "./browser-detect.service";
import { RecoverService } from "./recover.service";
import { LocationStrategy } from "@angular/common";

@Component({
  selector: "app-welcome",
  templateUrl: "./welcome.component.html",
  styleUrls: ["./welcome.component.scss"]
})
export class WelcomeComponent implements OnInit {
  loading = true;
  showRegisterUser: boolean;
  serverError: boolean;
  name: string;
  family: string;
  recoveryCode: string;
  searchingFamilies: boolean;
  userGroups: Array<UserGroup>;
  isAppBrowser: boolean;
  isPrivateMode: boolean;
  warning: any;
  expandThisIsMe: string;
  showNoRecoveryCode: boolean;

  constructor(
    public readonly authService: DeviceUsersService,
    public readonly browsersService: BrowserDetectService,
    private readonly router: Router,
    private readonly sentry: ErrorHandler,
    private readonly recoverService: RecoverService
  ) {}

  ngOnInit(): void {
    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });

    this.isAppBrowser =
      this.browsersService.isFacebookApp() ||
      this.browsersService.isInstagramApp() ||
      this.browsersService.isWebView();

    this.browsersService
      .isPrivateMode()
      .then(isPrivateMode => (this.isPrivateMode = isPrivateMode));

    Promise.all([
      Optional.of(this.authService.getCurrentUser())
        .map(currentUser => Promise.resolve(currentUser))
        .orElseGet(() => this.authService.fetchMe().catch(() => void 0)),
      this.authService.fetchUsers().then((users: Array<User>) => {
        users = users || [];
        const familyGroups = {};
        users.forEach(user => {
          const familyName = user?.family?.name || "";

          if (!familyGroups[familyName]) {
            familyGroups[familyName] = [];
          }
          familyGroups[familyName].push(user);
        });
        this.userGroups = Object.keys(familyGroups)
          .sort()
          .map(familyName => ({
            familyName,
            users: familyGroups[familyName]
          }));
      })
    ])
      .then(
        () => (this.loading = false),
        err => {
          this.loading = false;
          this.serverError = true;
          return Promise.reject(err);
        }
      )
      .then(() => {
        if (this.isStateUnknown()) {
          this.showRegisterUser = true;
        } else if (this.isStateKnown()) {
          timeout(2000).then(() => this.router.navigate(["game"]));
        }
      });
  }

  isStateUnknown() {
    return (
      !this.authService.getCurrentUser() && !this.authService.getUsers()?.length
    );
  }

  isStateMetButUnauthenticated() {
    return (
      !this.authService.getCurrentUser() &&
      !!this.authService.getUsers()?.length
    );
  }

  isStateKnown() {
    return !!this.authService.getCurrentUser();
  }

  registerUser() {
    this.loading = true;
    this.clearWarning();

    const userAlreadyRegisteredOnThisDevice = this.authService
      .getUsers()
      .find(
        user => user.name === this.name && user.family?.name === this.family
      );
    if (userAlreadyRegisteredOnThisDevice) {
      this.login(userAlreadyRegisteredOnThisDevice);
      return;
    }

    this.authService
      .createUser({ name: this.name, family: this.family })
      .then(() => this.router.navigate(["game"]))
      .then(
        () => (this.loading = false),
        err => {
          this.loading = false;
          this.assignWarning(err);
        }
      );
  }

  login(user: User) {
    this.loading = true;
    this.clearWarning();
    this.authService
      .loginUser(user)
      .then(() => this.router.navigate(["game"]))
      .then(
        () => (this.loading = false),
        err => {
          this.loading = false;
          this.assignWarning(err);
          this.serverError = true;
        }
      );
  }

  assignWarning(err) {
    this.sentry.handleError(err);
    this.warning = err?.error?.message;
  }

  clearWarning() {
    this.warning = null;
    this.recoveryCodeFailed = null;
    this.expandThisIsMe = null;
    this.showNoRecoveryCode = null;
    this.recoveryComment = null;
    this.recoveryCommentSent = null;
    this.recoveryCommentLoading = null;
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
      tap(() => (this.searchingFamilies = false)),
      catchError(err => {
        this.serverError = true;
        return throwError(err);
      })
    );
  };

  nameValid() {
    return (this.name?.trim()?.length || 0) >= 2;
  }

  familyValid() {
    return (this.family?.trim()?.length || 0) >= 4;
  }

  formValid() {
    return this.nameValid() && this.familyValid();
  }

  yesThisIsMe() {
    this.expandThisIsMe = "loading";
    this.recoverService
      .tryRecoverMe(this.name, this.family)
      .subscribe(status => {
        if (status.recoverySuccess) {
          this.loading = true;
          location.reload();
        } else {
          this.expandThisIsMe = "ready";
        }
      });
  }

  recoveryCodeFailed: boolean;
  recoveryCodeLoading: boolean;
  recoveryCodeValid() {
    return this.recoveryCode?.trim()?.length === 6;
  }

  recoverWithCode() {
    this.recoveryCodeLoading = true;
    this.recoveryCodeFailed = false;
    if (this.recoveryCodeValid()) {
      this.recoverService
        .recoverViaCode(this.recoveryCode, this.name, this.family)

        .subscribe(
          () => location.reload(),
          () => {
            this.recoveryCodeFailed = true;
            this.recoveryCodeLoading = false;
          }
        );
    }
  }

  recoveryComment: string;
  recoveryCommentLoading: boolean;
  recoveryCommentSent: boolean;

  addRecoveryComment() {
    this.recoveryCommentLoading = true;
    this.recoverService
      .applyUserCommentToRecoveryRequest(
        this.name,
        this.family,
        this.recoveryComment
      )
      .subscribe(() => {
        this.recoveryCommentSent = true;
        this.recoveryCommentLoading = false;
      });
  }
}
