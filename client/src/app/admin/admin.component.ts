import { Component, OnInit } from "@angular/core";
import { merge } from "rxjs";
import { switchMap, tap } from "rxjs/operators";
import { DeviceUsersService } from "../auth/device-users.service";
import { User } from "../auth/user";
import { Family } from "../family/family";

@Component({
  selector: "app-admin",
  templateUrl: "./admin.component.html",
  styleUrls: ["./admin.component.scss"]
})
export class AdminComponent implements OnInit {
  loading = true;
  canChangeFamily: boolean;
  families: Array<Family>;
  models = {};

  constructor(public readonly authService: DeviceUsersService) {}

  ngOnInit(): void {
    this.refresh().subscribe(() => (this.loading = false));
  }

  refresh() {
    return merge(
      this.authService
        .canChangeFamily()
        .pipe(tap(data => (this.canChangeFamily = data))),
      this.authService.getFamilies().pipe(
        tap(data => {
          (this.families = data)?.forEach(family => {
            this.models[family.id] = family.name;
            family?.users?.forEach(user => {
              this.models[user.id] = user.name;
            });
          });
        })
      )
    );
  }

  toggleCanChangeFamily(): void {
    this.authService
      .toggleCanChangeFamily()
      .subscribe(data => (this.canChangeFamily = data));
  }

  updateFamilyName(newName, family: Family): void {
    this.authService
      .updateFamilyName(family.id, newName)
      .pipe(switchMap(() => this.refresh()))
      .subscribe();
  }

  updateUserName(newName, user: User): void {
    this.authService
      .updateUserName(user.id, newName)
      .pipe(switchMap(() => this.refresh()))
      .subscribe();
  }

  returnToGame() {
    location.href = "/game"; // Don't use angular routing because /game is not destroy proof yet.
  }
}
