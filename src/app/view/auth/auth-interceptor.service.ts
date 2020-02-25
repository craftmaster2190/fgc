import { AuthService } from "./auth.service";
import { catchError } from "rxjs/operators";
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, throwError } from "rxjs";
import { Router } from "@angular/router";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    if (this.authService.getBasicAuth()) {
      request = request.clone({
        setHeaders: {
          "X-Requested-With": "XMLHttpRequest",
          Authorization: `Basic ${this.authService.getBasicAuth()}`
        }
      });
    }
    return next.handle(request).pipe(
      catchError(err => {
        if (err.status === 401) {
          this.authService.logout();
          this.router.navigate(["login"]);
        }
        return throwError(err);
      })
    );
  }
}
