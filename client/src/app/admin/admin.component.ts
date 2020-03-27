import { Component, OnInit } from "@angular/core";
import { merge } from "rxjs";
import { tap } from "rxjs/operators";
import { DeviceUsersService } from "../auth/device-users.service";
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

  constructor(public readonly authService: DeviceUsersService) {}

  ngOnInit(): void {
    merge(
      this.authService
        .canChangeFamily()
        .pipe(tap(data => (this.canChangeFamily = data))),
      this.authService.getFamilies().pipe(tap(data => (this.families = data)))
    ).subscribe(() => (this.loading = false));
  }

  toggleCanChangeFamily(): void {
    this.authService
      .toggleCanChangeFamily()
      .subscribe(data => (this.canChangeFamily = data));
  }

  updateFamilyName(newName, family: Family): void {
    this.authService.updateFamilyName(family.id, newName);
  }

  returnToGame() {
    location.href = "/game"; // Don't use angular routing because /game is not destroy proof yet.
  }
}
