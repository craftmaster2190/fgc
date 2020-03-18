import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot
} from "@angular/router";

export class AdminGuard implements CanActivate {
  constructor() {}
  canActivate(/*next: ActivatedRouteSnapshot, state: RouterStateSnapshot*/): boolean {
    console.log("admin guard can activate");
    return true;
  }
}
