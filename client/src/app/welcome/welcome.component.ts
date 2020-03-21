import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { Observable, throwError } from "rxjs";
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  switchMap,
  tap,
  catchError
} from "rxjs/operators";
import { DeviceUsersService } from "../auth/device-users.service";
import { User } from "../auth/user";
import { Optional } from "../util/optional";
import timeout from "../util/timeout";
import { UserGroup } from "../auth/user-group";
@Component({
  selector: "app-welcome",
  templateUrl: "./welcome.component.html",
  styleUrls: ["./welcome.component.scss"]
})
export class WelcomeComponent implements OnInit {
  loading: boolean = true;
  showRegisterUser: boolean;
  serverError: boolean;
  name: string;
  family: string;
  searchingFamilies: boolean;
  userGroups: Array<UserGroup>;

  constructor(
    public readonly authService: DeviceUsersService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });

    Promise.all([
      Optional.of(this.authService.getCurrentUser())
        .map(currentUser => Promise.resolve(currentUser))
        .orElseGet(() => this.authService.fetchMe().catch(() => void 0)),
      this.authService.fetchUsers().then((users: Array<User>) => {
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
    this.authService
      .createUser({ name: this.name, family: this.family })
      .then(() => this.router.navigate(["game"]))
      .then(
        () => (this.loading = false),
        () => (this.loading = false)
      );
  }

  login(user: User) {
    this.loading = true;
    this.authService
      .loginUser(user)
      .then(() => this.router.navigate(["game"]))
      .then(
        () => (this.loading = false),
        () => (this.loading = false)
      );
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
