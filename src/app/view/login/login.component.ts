import { AuthService } from '../auth/auth.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.sass"]
})
export class LoginComponent implements OnInit {
  username: string = "";
  password: string = "";
  loading: boolean = true;
  loginFailed: boolean = false;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit() {
    if (this.authService.getBasicAuth()) {
      this.authService
        .fetchMe()
        .then(() => this.router.navigate(["game"]))
        .catch(() => {
          this.loading = false;
          this.authService.logout();
        });
    } else {
      this.loading = false;
    }
  }

  validCredentials() {
    return (
      !!this.username &&
      this.username.length >= 4 &&
      !!this.password &&
      this.password.length >= 4
    );
  }

  login() {
    this.loading = true;
    this.authService.applyCredentials(this.username, this.password);
    this.authService
      .fetchMe()
      .then(() => this.router.navigate(["game"]))
      .catch(() => {
        this.loginFailed = true;
        this.authService.logout();
      })
      .then(() => (this.loading = false));
  }
}
