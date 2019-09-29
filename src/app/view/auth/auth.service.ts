import { User } from './user';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class AuthService {
  private user: User;
  private basicAuth: string = localStorage.getItem("basicAuthToken");

  constructor(private readonly http: HttpClient) {}



  fetchMe() {
    return this.http
      .get<User>("/api/user/me")
      .toPromise()
      .then(user => (this.user = user));
  }

  applyCredentials(username: string, password: string) {
    this.basicAuth = window.btoa(username + ':' + password);
    localStorage.setItem("basicAuthToken", this.basicAuth);
  }

  getBasicAuth() {
    return this.basicAuth;
  }

  getLoggedInUser() {
    return this.user;
  }

  logOut() {
    delete this.user;
    delete this.basicAuth;
  }
}
