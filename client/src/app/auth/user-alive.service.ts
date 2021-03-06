import { Injectable, OnDestroy } from "@angular/core";
import * as moment from "moment";
import { fromEvent, interval, merge, Subscription } from "rxjs";
import { filter, throttle } from "rxjs/operators";

@Injectable({
  providedIn: "root"
})
export class UserAliveService implements OnDestroy {
  private lastActive = moment();
  private readonly subscription: Subscription;

  constructor() {
    this.subscription = merge(
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

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  isActive() {
    return this.lastActive.isAfter(moment().subtract(15, "seconds"));
  }
}
