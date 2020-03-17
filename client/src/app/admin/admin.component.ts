import { Component, OnInit } from "@angular/core";
import { DeviceUsersService } from "../auth/device-users.service";

@Component({
  selector: "app-admin",
  templateUrl: "./admin.component.html",
  styleUrls: ["./admin.component.scss"]
})
export class AdminComponent implements OnInit {
  isFamilyChangeEnabled: Boolean;
  constructor(public readonly authService: DeviceUsersService) {}
  ngOnInit(): void {
    this.authService
      .getFamilyChangeEnabled()
      .subscribe(data => (this.isFamilyChangeEnabled = data));
  }
  toggleFamilyChangeEnabled(): void {
    this.authService
      .toggleFamilyChangeEnabled()
      .subscribe(data => (this.isFamilyChangeEnabled = data));
  }
}
