import { ErrorHandler, Injectable } from "@angular/core";
import * as Sentry from "@sentry/browser";
import { Optional } from "../util/optional";
import { DeviceUsersService } from "../auth/device-users.service";
import { DeviceIdService } from "../auth/device-id.service";

const sentryIsRunning = Optional.of((window as any)._sentryDsn)
  .filter(dsn => dsn && !dsn.startsWith("XXX"))
  .map(dsn => {
    Sentry.init({ dsn });
    return true;
  })
  .orElse(false);

@Injectable()
export class SentryErrorHandler implements ErrorHandler {
  constructor(
    private readonly authService: DeviceUsersService,
    private readonly deviceService: DeviceIdService
  ) {}
  handleError(error) {
    if (sentryIsRunning) {
      try {
        const sentryUser: any = {};
        Optional.of(this.authService.getCurrentUser())
          .map(user => {
            sentryUser.id = user.id;
            sentryUser.username = user.name;
            Sentry.setUser(sentryUser);
            return user.family?.name;
          })
          .map(familyName => {
            Sentry.setExtra("familyName", familyName);
          });
        Sentry.setExtra("deviceId", this.deviceService.get());
      } catch (unexpectedError) {
        Sentry.captureException(unexpectedError);
      }
      const eventId = Sentry.captureException(error.originalError || error);
    }

    console.trace("sentry-error-handler.service", error);
  }
}
