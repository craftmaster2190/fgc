import { AuthService } from '../auth/auth.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.sass"]
})
export class LoginComponent implements OnInit {
  username: string;
  password: string;
  loading: boolean = false;
  loginFailed: boolean = false;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit() {
    if (this.authService.getBasicAuth()) {
      this.loading = true;
      this.authService
        .fetchMe()
        .then(() => this.router.navigate(["game"]))
        .catch(() => {
          this.loading = false;
          this.authService.logOut();
        });
    }
  }

  login() {
    this.loading = true;
    this.authService.applyCredentials(this.username, this.password);
    this.authService
      .fetchMe()
      .then(() => this.router.navigate(["game"]))
      .catch(() => (this.loginFailed = true))
      .then(() => (this.loading = false));
  }
}
