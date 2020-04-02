import { Component, OnInit } from "@angular/core";
import { merge } from "rxjs";
import { switchMap, tap } from "rxjs/operators";
import { DeviceUsersService } from "../auth/device-users.service";
import { User } from "../auth/user";
import { Family } from "../family/family";
import { RecoverService } from "../welcome/recover.service";
import RecoveryRequest from "../welcome/recovery-request";
import RecoveryRequestDetails from "../welcome/recovery-request-details";
import { Optional } from "../util/optional";
import * as moment from "moment";
import { Time } from "../chat/time";

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

  recoveryRequests: Array<RecoveryRequest>;

  constructor(
    public readonly authService: DeviceUsersService,
    private readonly recoverService: RecoverService
  ) {}

  ngOnInit(): void {
    this.refreshFamiliesAndUsers().subscribe(() => (this.loading = false));
    this.refreshRecoveryRequests().subscribe();
  }

  refreshFamiliesAndUsers() {
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

  refreshRecoveryRequests() {
    return this.recoverService
      .adminGetRecoveryRequests()
      .pipe(
        tap(recoveryRequests => (this.recoveryRequests = recoveryRequests))
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
      .pipe(switchMap(() => this.refreshFamiliesAndUsers()))
      .subscribe();
  }

  updateUserName(newName, user: User): void {
    this.authService
      .updateUserName(user.id, newName)
      .pipe(switchMap(() => this.refreshFamiliesAndUsers()))
      .subscribe();
  }

  returnToGame() {
    location.href = "/game"; // Don't use angular routing because /game is not destroy proof yet.
  }

  approveRecoveryRequest(recoveryRequest: RecoveryRequest) {
    this.recoverService
      .adminApproveRecoveryRequest(recoveryRequest)
      .pipe(switchMap(() => this.refreshRecoveryRequests()))
      .subscribe();
  }

  stringifyRecoveryRequest(recoveryRequest: RecoveryRequestDetails) {
    const formatLabel = (label: string, value) =>
      Optional.of(value)
        .map(v => label + v)
        .orElse(null);

    const formatTimestamp = (label: string, timestamp?: Time) =>
      formatLabel(
        label,
        Optional.of(timestamp)
          .map(t => t.epochSecond)
          .map(moment.unix)
          .map(mo => mo.format())
          .orElse(null)
      );

    return ([
      "   ######### Requested User #########",
      formatTimestamp("Created At: ", recoveryRequest.createdAt),
      formatLabel("Username: ", recoveryRequest.user?.name),
      formatLabel("Family: ", recoveryRequest.user?.family?.name),
      formatLabel("User Comment: ", recoveryRequest.userComment),
      "   ######### Requesting Device #########",
      ...(recoveryRequest.deviceInfos || []).map(deviceInfo => [
        formatTimestamp("Device-Info Created At: ", deviceInfo?.createdAt),
        formatTimestamp(" Last Log In: ", deviceInfo?.lastLogIn),
        formatLabel("  InetAddress: ", deviceInfo?.inetAddress),
        formatLabel("   Device Fingerprint: ", deviceInfo?.fingerprint),
        formatLabel(
          "    Browser Fingerprint: ",
          deviceInfo?.browserFingerprint
        ),
        formatLabel("     User-Agent: ", deviceInfo?.userAgent)
      ]),
      "   ######### Requested User's Other Devices #########",
      ...(recoveryRequest.usersOtherDeviceInfos || []).map(deviceInfo => [
        formatTimestamp("Device-Info Created At: ", deviceInfo?.createdAt),
        formatTimestamp(" Last Log In: ", deviceInfo?.lastLogIn),
        formatLabel("  InetAddress: ", deviceInfo?.inetAddress),
        formatLabel("   Device Fingerprint: ", deviceInfo?.fingerprint),
        formatLabel(
          "    Browser Fingerprint: ",
          deviceInfo?.browserFingerprint
        ),
        formatLabel("     User-Agent: ", deviceInfo?.userAgent)
      ])
    ].reduce((prev, curr) => {
      if (Array.isArray(prev) && Array.isArray(curr)) {
        return prev.concat(curr);
      } else if (Array.isArray(prev) && !Array.isArray(curr)) {
        prev.push(curr);
        return prev;
      } else if (!Array.isArray(prev) && Array.isArray(curr)) {
        return [prev].concat(curr);
      } else if (!Array.isArray(prev) && !Array.isArray(curr)) {
        return [prev, curr];
      } else {
        return [];
      }
    }) as Array<string>)
      .filter(str => !!str?.trim())
      .join("\n");
  }
}
