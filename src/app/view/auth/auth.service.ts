import { User } from './user';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class AuthService {
  private static readonly LOCAL_STORAGE_KEY = "basicAuthToken";

  private user: User;
  private basicAuth: string = localStorage.getItem(
    AuthService.LOCAL_STORAGE_KEY
  );

  constructor(private readonly http: HttpClient) {}

  fetchMe() {
    return this.http
      .get<User>("/api/user/me")
      .toPromise()
      .then(user => (this.user = user));
  }

  applyCredentials(username: string, password: string) {
    this.basicAuth = window.btoa(username + ":" + password);
    localStorage.setItem(AuthService.LOCAL_STORAGE_KEY, this.basicAuth);
  }

  getBasicAuth() {
    return this.basicAuth;
  }

  getLoggedInUser() {
    return this.user;
  }

  logout() {
    delete this.user;
    delete this.basicAuth;
    localStorage.removeItem(AuthService.LOCAL_STORAGE_KEY);
    this.http.get("/logout").toPromise();
  }

  registerUser(user: User) {
    return this.http.post<never>("/register", user).toPromise();
  }
}
