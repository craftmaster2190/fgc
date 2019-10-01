import { BehaviorSubject, PartialObserver, Subject } from 'rxjs';
import { Injectable } from '@angular/core';

export interface Toast {
  message: string;
  header?: string;
  delay?: number;
  classname?: string;
}

@Injectable()
export class ToastService {
  private readonly toasts = new Subject<Toast>();

  constructor() {}

  subscribe(next: (value: Toast) => void) {
    return this.toasts.subscribe(next);
  }

  create(toast: Toast) {
    this.toasts.next(toast);
  }
}
