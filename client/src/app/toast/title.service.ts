import { Injectable } from "@angular/core";
import { Title } from "@angular/platform-browser";
import { interval } from "rxjs";
import { UserAliveService } from "../auth/user-alive.service";

@Injectable({
  providedIn: "root"
})
export class TitleService {
  private readonly originalTitle = "Fantasy General Conference";
  private flash1;
  private flash2;

  constructor(title: Title, userAliveService: UserAliveService) {
    interval(1800).subscribe(() => {
      const userIsActive = userAliveService.isActive();
      if (!userIsActive && this.flash1 && this.flash2) {
        if (title.getTitle() === this.flash2) {
          title.setTitle(this.flash1);
        } else {
          title.setTitle(this.flash2);
        }
      } else {
        title.setTitle(this.originalTitle);
        this.flash1 = null;
        this.flash2 = null;
      }
    });
  }

  setFlashing(flash1: string, flash2: string) {
    this.flash1 = flash1;
    this.flash2 = flash2;
  }
}
