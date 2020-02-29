import { Injectable } from "@angular/core";
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  Router
} from "@angular/router";
import { Observable } from "rxjs";
import { DeviceUsersService } from "src/app/view/auth/device-users.service";

@Injectable({
  providedIn: "root"
})
export class KnownGuard implements CanActivate {
  constructor(
    private readonly authService: DeviceUsersService,
    private readonly router: Router
  ) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    const isCurrentlyLoggedIn = !!this.authService.getCurrentUser();
    if (isCurrentlyLoggedIn) {
      return true;
    }

    return this.authService
      .fetchMe()
      .then(user => !!user)
      .catch(() => false)
      .then(isNowLoggedIn => {
        if (!isNowLoggedIn) {
          this.router.navigate(["welcome"]);
        }
        return isNowLoggedIn;
      });
  }
}
