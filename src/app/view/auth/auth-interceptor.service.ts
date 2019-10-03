import { AuthService } from "./auth.service";
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly authService: AuthService) {}

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
    return next.handle(request);
  }
}
