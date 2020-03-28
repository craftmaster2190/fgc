import { Injectable } from "@angular/core";
import { CanActivate, Router } from "@angular/router";
import { DeviceUsersService } from "./device-users.service";
@Injectable({
  providedIn: "root"
})
export class AdminGuard implements CanActivate {
  constructor(
    private readonly authService: DeviceUsersService,
    private readonly router: Router
  ) {}
  canActivate(): boolean {
    if (this.authService.getCurrentUser()?.isAdmin) {
      return true;
    }

    this.router.navigate(["game"]);
    return false;
  }
}
