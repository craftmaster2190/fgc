import { Injectable } from "@angular/core";
import { DeviceIdService } from "./device-id.service";
import { HttpClient } from "@angular/common/http";
import { User } from "./user";
import { tap } from "rxjs/operators";
import { Family } from "./family";
import { Router } from "@angular/router";

@Injectable({
  providedIn: "root"
})
export class DeviceUsersService {
  private deviceUsers?: Array<User>;
  private currentUser?: User;

  constructor(
    private readonly deviceId: DeviceIdService,
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  fetchUsers() {
    return this.http
      .get<Array<User>>("/api/auth/users", {
        params: { deviceId: this.deviceId.get() }
      })
      .toPromise()
      .then(users => (this.deviceUsers = users));
  }

  createUser() {
    // Should only be run if not logged in at all
    return this.http
      .post<User>("/api/auth/", {
        deviceId: this.deviceId.get()
      })
      .toPromise()
      .then(user => (this.currentUser = user));
  }

  loginUser(user: User) {
    // Can be used to switch users
    // Should put a server cookie and create a sesssion
    return this.http
      .post<User>("/api/auth/login", {
        userId: user.id,
        deviceId: this.deviceId.get()
      })
      .toPromise()
      .then(user => (this.currentUser = user));
  }

  logoutUser() {
    this.currentUser = null;
    // Can be used to switch users
    // Should put a server cookie and create a sesssion
    return Promise.all([
      this.router.navigate(["welcome"]),
      this.http.post<User>("/api/auth/logout", null).toPromise()
    ]);
  }

  updateUser(updates: { name?: string; family?: string }) {
    // Requires Logged in
    // Undecided? Do we allow players to change families
    return this.http
      .patch<User>("/api/auth/", updates)
      .toPromise()
      .then(user => (this.currentUser = user));
  }

  searchFamilies(partialFamilyName: string) {
    return this.http.get<Array<Family>>("/api/auth/family", {
      params: { search: partialFamilyName }
    });
  }

  fetchMe() {
    // Requires Logged in
    // If there is a session, this will return a user
    return this.http
      .get<User>("/api/auth/me")
      .toPromise()
      .then(user => (this.currentUser = user));
  }

  getUsers() {
    return this.deviceUsers;
  }

  getCurrentUser() {
    return this.currentUser;
  }
}
