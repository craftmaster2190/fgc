import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root"
})
export class VibrateService {
  private readonly vibrate;
  constructor() {
    const vibrate =
      window.navigator.vibrate || (window.navigator as any).mozVibrate;
    if (vibrate) {
      this.vibrate = vibrate.bind(window.navigator);
    }
  }

  short() {
    if (this.vibrate) {
      this.vibrate([300]);
    }
  }

  starWars() {
    // Star Wars shamelessly taken from the awesome Peter Beverloo
    // https://tests.peter.sh/notification-generator/
    if (this.vibrate) {
      this.vibrate([
        500,
        110,
        500,
        110,
        450,
        110,
        200,
        110,
        170,
        40,
        450,
        110,
        200,
        110,
        170,
        40,
        500
      ]);
    }
  }
}
