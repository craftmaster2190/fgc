import { Injectable } from "@angular/core";
import { v4 as uuidv4 } from "uuid";
import * as Fingerprint2 from "fingerprintjs2";

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

  getFingerprint(): Promise<string> {
    return Fingerprint2.getPromise({
      preprocessor: null,

      screen: {
        /**
         * To ensure consistent fingerprints when users rotate their mobile devices
         */
        detectScreenOrientation: true
      },

      excludes: {
        language: false,
        colorDepth: false,
        pixelRatio: false,
        screenResolution: false,
        availableScreenResolution: false,
        timezoneOffset: false,
        timezone: false,
        cpuClass: false,
        hasLiedLanguages: false,
        hasLiedResolution: false,
        hasLiedOs: false,
        hasLiedBrowser: false,
        touchSupport: false,

        deviceMemory: true,
        platform: true,
        webglVendorAndRenderer: true,

        userAgent: true,
        hardwareConcurrency: true,
        sessionStorage: true,
        localStorage: true,
        indexedDb: true,
        addBehavior: true,
        openDatabase: true,
        doNotTrack: true,
        plugins: true,
        canvas: true,
        webgl: true,
        adBlock: true,
        fonts: true,
        fontsFlash: true,
        audio: true,
        enumerateDevices: true
      }
    }).then(components => {
      const skip = ["webdriver"];
      const values = components.map(({ key, value }) => {
        if (skip.indexOf(key) > -1) {
          return "";
        }
        return value;
      });
      return Fingerprint2.x64hash128(values.join(""), 31);
    });
  }

  getBrowserFingerprint(): Promise<string> {
    return Fingerprint2.getPromise({}).then(components => {
      const values = components.map(({ value }) => value);
      return Fingerprint2.x64hash128(values.join(""), 31);
    });
  }
}
