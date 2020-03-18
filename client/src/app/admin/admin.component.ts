import { Component, OnInit, OnChanges } from "@angular/core";
import { DeviceUsersService } from "../auth/device-users.service";
import { Family } from "../family/family";

@Component({
  selector: "app-admin",
  templateUrl: "./admin.component.html",
  styleUrls: ["./admin.component.scss"]
})
export class AdminComponent implements OnInit, OnChanges {
  isFamilyChangeEnabled: Boolean;
  families: [Family];
  constructor(public readonly authService: DeviceUsersService) {}
  ngOnInit(): void {
    this.authService
      .getFamilyChangeEnabled()
      .subscribe(data => (this.isFamilyChangeEnabled = data));
    this.authService.getFamilies().subscribe(data => (this.families = data));
  }
  toggleFamilyChangeEnabled(): void {
    this.authService
      .toggleFamilyChangeEnabled()
      .subscribe(data => (this.isFamilyChangeEnabled = data));
  }
  updateFamilyName(newName, family: Family): void {
    this.authService.updateFamilyName(family.id, newName);
  }
}
