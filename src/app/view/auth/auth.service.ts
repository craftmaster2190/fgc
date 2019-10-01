import { User } from './user';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable()
export class AuthService {
  private static readonly LOCAL_STORAGE_KEY = "basicAuthToken"

  private user: User;
  private basicAuth: string = localStorage.getItem(AuthService.LOCAL_STORAGE_KEY);
  private apiUrl: string;
  constructor(private readonly http: HttpClient) {
    this.apiUrl = environment.apiUrl;
  }



  fetchMe() {
    return this.http
      .get<User>("/api/user/me")
      .toPromise()
      .then(user => (this.user = user));
  }

  register(username: string, password: string) {
    return this.http
    .post<User>(this.apiUrl + "/register/player", {"username" : username, "password" : password})
    .subscribe(data => console.log("reg", data));
  }
  applyCredentials(username: string, password: string) {
    this.basicAuth = window.btoa(username + ':' + password);
    localStorage.setItem(AuthService.LOCAL_STORAGE_KEY, this.basicAuth);
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
    localStorage.removeItem(AuthService.LOCAL_STORAGE_KEY);
  }
}
