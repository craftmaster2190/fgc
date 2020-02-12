import { Injectable } from "@angular/core";
import { Observable, merge, fromEvent } from "rxjs";

import { interval } from "rxjs";
import { throttle, filter } from "rxjs/operators";

import * as moment from "moment";

@Injectable({
  providedIn: "root"
})
export class UserAliveService {
  private lastActive = moment();
  constructor() {
    merge(
      fromEvent(document, "mousemove"),
      fromEvent(document, "click"),
      fromEvent(document, "touchmove"),
      fromEvent(document, "keyup"),
      fromEvent(document, "focus"),
      fromEvent(document, "scroll"),
      fromEvent(document, "visibilitychange").pipe(
        filter(() => document.visibilityState === "visible")
      )
    )
      .pipe(throttle(val => interval(200)))
      .subscribe(() => (this.lastActive = moment()));
  }

  isActive() {
    return this.lastActive.isAfter(moment().subtract(15, "seconds"));
  }
}
