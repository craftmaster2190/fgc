import { FamilySearchService } from "./family-search.service";
import { AuthService } from "../auth/auth.service";
import { ToastService } from "../util/toast/toast.service";
import { User } from "../auth/user";
import { Component, OnInit } from "@angular/core";
import {
  debounceTime,
  distinctUntilChanged,
  every,
  filter,
  map,
  switchMap,
  tap
} from "rxjs/operators";
import { Observable } from "rxjs";
import { Router } from "@angular/router";

@Component({
  selector: "app-register",
  templateUrl: "./register.component.html",
  styleUrls: ["./register.component.scss"]
})
export class RegisterComponent implements OnInit {
  username: string;
  family: string;
  password: string;
  confirmPassword: string;
  loading: boolean;
  searchingFamilies: boolean;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly toastService: ToastService,
    private readonly familySearchService: FamilySearchService
  ) {}

  ngOnInit() {
    this.authService.logout();
  }

  usernameValid() {
    return !!this.username && this.username.length >= 4;
  }

  familyValid() {
    return !!this.family && this.family.length >= 4;
  }

  passwordValid() {
    return !!this.password && this.password.length >= 4;
  }

  confirmPasswordValid() {
    return this.passwordValid() && this.confirmPassword === this.password;
  }

  formValid() {
    return (
      this.usernameValid() &&
      this.familyValid() &&
      this.passwordValid() &&
      this.confirmPasswordValid()
    );
  }

  registerUser() {
    const user: User = {
      username: this.username,
      password: this.password,
      family: { name: this.family }
    };
    this.authService
      .registerUser(user)
      .then(() => {
        this.authService.applyCredentials(this.username, this.password);
        this.router.navigate(["login"]);
      })
      .catch(err => {
        this.loading = false;
        let message = err.error && err.error.message;
        message = message || err.message;
        message = message || "Unable to complete request!";

        this.toastService.createError(
          "Unable to complete registration",
          message
        );
        console.error(err);
      });
  }

  searchFamilies = (text$: Observable<string>) => {
    return text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      filter(term => term && term.length >= 2),
      tap(() => (this.searchingFamilies = true)),
      switchMap(term =>
        this.familySearchService
          .search(term)
          .pipe(map(family => family.map(f => f.name)))
      ),
      tap(() => (this.searchingFamilies = false))
    );
  };
}
