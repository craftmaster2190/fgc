import { Component, OnInit } from "@angular/core";
import { DeviceUsersService } from "../auth/device-users.service";
import timeout from "../util/timeout";

@Component({
  selector: "app-logout",
  templateUrl: "./logout.component.html",
  styleUrls: ["./logout.component.scss"]
})
export class LogoutComponent implements OnInit {
  showHelper = false;
  ngOnInit(): void {
    timeout(5000).then(() => (this.showHelper = true));
  }
}
