import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { DeviceUsersService } from "../auth/device-users.service";

@Component({
  selector: "app-nav-header",
  templateUrl: "./nav-header.component.html",
  styleUrls: ["./nav-header.component.scss"]
})
export class NavHeaderComponent implements OnInit {
  constructor(
    public readonly authService: DeviceUsersService,
    private readonly router: Router
  ) {}

  ngOnInit() {}

  getUsername() {
    return this.authService.getCurrentUser()?.name || "";
  }

  getFamilyName() {
    return this.authService.getCurrentUser()?.family?.name || "";
  }
}
