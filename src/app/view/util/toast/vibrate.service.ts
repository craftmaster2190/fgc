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
}
