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
  searchingFamilies: boolean;
  userGroups: Array<UserGroup>;
  isAppBrowser: boolean;
  isPrivateMode: boolean;
  warning: any;

  constructor(
    public readonly authService: DeviceUsersService,
    public readonly browsersService: BrowserDetectService,
    private readonly router: Router,
    private readonly sentry: ErrorHandler
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
    this.warning = null;
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
    this.warning = null;
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
}
