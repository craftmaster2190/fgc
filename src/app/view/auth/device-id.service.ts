import { Injectable } from "@angular/core";
import { v4 as uuidv4 } from "uuid";

@Injectable({
  providedIn: "root"
})
export class DeviceIdService {
  private static readonly DEVICE_ID = "DEVICE_ID";
  public readonly deviceId: string;

  constructor() {
    this.deviceId = localStorage.getItem(DeviceIdService.DEVICE_ID);
    if (!this.deviceId) {
      this.deviceId = uuidv4();
      localStorage.setItem(DeviceIdService.DEVICE_ID, this.deviceId);
    }
  }

  get() {
    return this.deviceId;
  }
}
