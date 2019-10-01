import { AuthService } from '../auth/auth.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: "app-nav-header",
  templateUrl: "./nav-header.component.html",
  styleUrls: ["./nav-header.component.sass"]
})
export class NavHeaderComponent implements OnInit {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit() {}

  logout() {
    this.authService.logout();
    this.router.navigate(["/login"]);
  }

  getUsername() {
    const user = this.authService.getLoggedInUser();
    if (!user) {
      return "";
    }

    return user.username;
  }

  getFamilyName() {
    const user = this.authService.getLoggedInUser();
    if (!user) {
      return "";
    }

    return user.family.name;
  }
}
