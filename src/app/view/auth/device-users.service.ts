import { Injectable } from "@angular/core";
import { DeviceIdService } from "./device-id.service";
import { HttpClient } from "@angular/common/http";
import { User } from "./user";
import { tap } from "rxjs/operators";
import { Family } from "./family";

@Injectable({
  providedIn: "root"
})
export class DeviceUsersService {
  private deviceUsers?: Array<User>;
  private currentUser?: User;

  constructor(
    private readonly deviceId: DeviceIdService,
    private readonly http: HttpClient
  ) {}

  fetchUsers() {
    return this.http
      .get<Array<User>>("/api/auth/users", {
        params: { deviceId: this.deviceId.get() }
      })
      .pipe(tap(user => (this.deviceUsers = user)))
      .toPromise();
  }

  createUser() {
    // Should only be run if not logged in at all
    return this.http
      .post<User>("/api/auth/", {
        deviceId: this.deviceId.get()
      })
      .pipe(tap(user => (this.currentUser = user)))
      .toPromise();
  }

  loginUser(user: User) {
    // Can be used to switch users
    // Should put a server cookie and create a sesssion
    return this.http
      .post<User>("/api/auth/login", {
        userId: user.id,
        deviceId: this.deviceId.get()
      })
      .pipe(tap(user => (this.currentUser = user)))
      .toPromise();
  }

  updateUser(updates: { name?: string; family?: string }) {
    // Requires Logged in
    // Undecided? Do we allow players to change families
    return this.http
      .patch<User>("/api/auth/", updates)
      .pipe(tap(user => (this.currentUser = user)))
      .toPromise();
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
      .pipe(tap(user => (this.currentUser = user)))
      .toPromise();
  }

  getUsers() {
    return this.deviceUsers;
  }

  getCurrentUser() {
    return this.currentUser;
  }
}
