import { Toast, ToastService } from "./toast/toast.service";
import { Component, OnDestroy } from "@angular/core";
import { Subscription } from "rxjs";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"]
})
export class AppComponent implements OnDestroy {
  toasts: Array<Toast> = [];
  subscription: Subscription;

  constructor(toastService: ToastService) {
    this.subscription = toastService.subscribe((value: Toast) => {
      this.toasts.push(value);
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
