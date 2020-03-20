import { ErrorHandler, Injectable } from "@angular/core";
import * as Sentry from "@sentry/browser";
import { Optional } from "../util/optional";

const sentryIsRunning = Optional.of((window as any)._sentryDsn)
  .filter(dsn => dsn && !dsn.startsWith("XXX"))
  .map(dsn => {
    Sentry.init({ dsn });
    return true;
  })
  .orElse(false);

@Injectable()
export class SentryErrorHandler implements ErrorHandler {
  constructor() {}
  handleError(error) {
    if (sentryIsRunning) {
      const eventId = Sentry.captureException(error.originalError || error);
      // Sentry.showReportDialog({ eventId });
    }
  }
}
