import { Injectable } from "@angular/core";
import { Subject } from "rxjs";
import { TitleService } from "./title.service";
import { VibrateService } from "./vibrate.service";

export interface Toast {
  message: string;
  header?: string;
  delay?: number;
  classname?: string;
}

@Injectable()
export class ToastService {
  private readonly toasts = new Subject<Toast>();

  constructor(
    private readonly vibrateService: VibrateService,
    private readonly titleService: TitleService
  ) {}

  subscribe(next: (value: Toast) => void) {
    return this.toasts.subscribe(next);
  }

  create(toast: Toast) {
    this.toasts.next(toast);
    this.titleService.setFlashing(toast.header || "Update", toast.message);
    this.vibrateService.short();
  }

  createError(header: string, message: string) {
    this.create({
      header,
      message: "Error: " + message,
      classname: "bg-danger text-light"
    });
  }
}
