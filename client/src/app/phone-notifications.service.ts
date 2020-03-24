import { Injectable } from "@angular/core";
import { User } from "./auth/user";
import { Chat } from "./chat/chat";
import { ImagesCacheService } from "./image/images-cache.service";
import { UserUpdatesService } from "./auth/user-updates.service";

@Injectable({
  providedIn: "root"
})
export class PhoneNotificationsService {
  private canSendNotifications: boolean;
  private serviceWorkerRegistration;

  constructor(
    private readonly imageCache: ImagesCacheService,
    private readonly userCache: UserUpdatesService
  ) {
    if (this.isPushNotificationSupported()) {
      Notification.requestPermission(result => {
        this.canSendNotifications = result === "granted";
        console.log("this.canSendNotifications", this.canSendNotifications);

        navigator.serviceWorker
          .register("/assets/service-worker.js")
          .then(serviceWorkerRegistration => {
            console.log(
              "Service Worker is registered",
              serviceWorkerRegistration
            );

            this.serviceWorkerRegistration = serviceWorkerRegistration;
          });
      });
    }
  }

  /**
   * checks if Push notification and service workers are supported by your browser
   */
  isPushNotificationSupported() {
    return "Notification" in window && "serviceWorker" in navigator;
  }

  /**
   * shows a notification
   */
  sendNotification(chat: Chat) {
    console.log("Send chat", chat, this.canSendNotifications);
    if (!this.canSendNotifications || !this.serviceWorkerRegistration) {
      console.log("Not sending...");
      return;
    }

    const userImageLink = "/api/user/profile/" + chat.userId;

    const image = this.imageCache.has(userImageLink)
      ? userImageLink
      : "/android-chrome-192x192.png";

    const title = "FantasyGC Chat";
    const options = {
      body:
        this.userCache
          .getIfPresent(chat.userId)
          .map(user => user.name + ": ")
          .orElse("") + chat.value,
      icon: image,
      badge: image
    };
    console.log("Sending 2", title, options);
    navigator.serviceWorker.ready.then(() => {
      this.serviceWorkerRegistration.showNotification(title, options);
    });
  }
}
