import { AnswerBusService } from "src/app/messaging/answer-bus.service";
import { AnswersService } from "src/app/answers/answers.service";
import {
  Component,
  OnInit,
  OnDestroy,
  NgZone,
  AfterViewInit
} from "@angular/core";
import { Subscription } from "rxjs";
import { ImagesCacheService } from "../image/images-cache.service";
import { UserUpdatesService } from "../auth/user-updates.service";
import { DeviceUsersService } from "../auth/device-users.service";
import { ServerMessagesService } from "./server-messages.service";
import timeout from "../util/timeout";

@Component({
  selector: "app-game",
  templateUrl: "./game.component.html",
  styleUrls: ["./game.component.scss"]
})
export class GameComponent implements OnInit, AfterViewInit, OnDestroy {
  openPanel = "firstPresidency";
  subscription: Subscription;
  serverMessages: Array<string>;
  scripture = (() => {
    const scriptures = [
      // cSpell:disable
      {
        reference: "Deuteronomy 31:12",
        text:
          "Gather the people together, men, and women, and children, and thy stranger that is within thy gates, that they may hear, and that they may learn, and fear the Lord your God, and observe to do all the words of this law:"
      },
      {
        reference: "Proverbs 9:9",
        text:
          "Give instruction to a wise man, and he will be yet wiser: teach a just man, and he will increase in learning."
      },
      {
        reference: "Isaiah 26:9",
        text:
          "With my soul have I desired thee in the night; yea, with my spirit within me will I seek thee early: for when thy judgments are in the earth, the inhabitants of the world will learn righteousness."
      },
      {
        reference: "Isaiah 1:17",
        text:
          "Learn to do well; seek judgment, relieve the oppressed, judge the fatherless, plead for the widow."
      },
      {
        reference: "Romans 10:13",
        text:
          "For whosoever shall call upon the name of the Lord shall be saved."
      },
      {
        reference: "Hebrews 11:1",
        text:
          "Now faith is the substance of things hoped for, the evidence of things not seen."
      },
      {
        reference: "2 Corinthians 9:7",
        text:
          "Every man according as he purposeth in his heart, so let him give; not grudgingly, or of necessity: for God loveth a cheerful giver."
      },
      {
        reference: "Alma 37:35",
        text:
          "O, remember, my son, and learn wisdom in thy youth; yea, learn in thy youth to keep the commandments of God."
      },
      {
        reference: "Alma 38:9",
        text:
          "And now, my son, I have told you this that ye may learn wisdom, that ye may learn of me that there is no other way or means whereby man can be saved, only in and through Christ. Behold, he is the life and the light of the world. Behold, he is the word of truth and righteousness."
      },
      {
        reference: "Mosiah 2:17",
        text:
          "And behold, I tell you these things that ye may learn wisdom; that ye may learn that when ye are in the service of your fellow beings ye are only in the service of your God."
      },
      {
        reference: "2 Nephi 2:14",
        text:
          "And now, my sons, I speak unto you these things for your profit and learning; for there is a God, and he hath created all things, both the heavens and the earth, and all things that in them are, both things to act and things to be acted upon."
      },
      {
        reference: "Doctrine & Covenants 136:32",
        text:
          "Let him that is ignorant learn wisdom by humbling himself and calling upon the Lord his God, that his eyes may be opened that he may see, and his ears opened that he may hear;"
      },
      {
        reference: "Doctrine & Covenants 90:15",
        text:
          "And set in order the churches, and study and learn, and become acquainted with all good books, and with languages, tongues, and people."
      },
      {
        reference: "Doctrine & Covenants 88:118",
        text:
          "And as all have not faith, seek ye diligently and teach one another words of wisdom; yea, seek ye out of the best books words of wisdom; seek learning, even by study and also by faith."
      },
      {
        reference: "Doctrine & Covenants 82:10",
        text:
          "I, the Lord, am bound when ye do what I say; but when ye do not what I say, ye have no promise."
      },
      {
        reference: "Mosiah 16:15",
        text:
          "Teach them that redemption cometh through Christ the Lord, who is the very aEternal Father. Amen."
      },
      {
        reference: "Helaman 14:17",
        text:
          "But behold, the resurrection of Christ aredeemeth mankind, yea, even all mankind, and bringeth them back into the presence of the Lord."
      },
      {
        reference: "2 Nephi 33:6-7",
        text:
          "I glory in plainness; I glory in truth; I glory in my Jesus, for he hath redeemed my soul from hell. I have charity for my people, and great faith in Christ that I shall meet many souls spotless at his judgment-seat."
      },
      {
        reference: "John 3:16",
        text:
          "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life."
      },
      {
        reference: "John 20:31",
        text:
          "But these are written, that ye might believe that Jesus is the Christ, the Son of God; and that believing ye might have life through his name."
      }
      // cSpell:enable
    ];
    return scriptures[Math.floor(Math.random() * scriptures.length)];
  })();

  constructor(
    public readonly authService: DeviceUsersService,
    public readonly answersService: AnswersService,
    public readonly answerBusService: AnswerBusService,
    private readonly userUpdates: UserUpdatesService,
    private readonly serverMessagesService: ServerMessagesService,
    private readonly ngZone: NgZone
  ) {}

  ngOnInit() {
    this.subscription = this.answerBusService.listenForQuestionsAndAnswers();
    this.subscription.add(this.userUpdates.startListener());
    this.subscription.add(
      this.serverMessagesService.subscribe(
        serverMessages => (this.serverMessages = serverMessages)
      )
    );
  }

  ngAfterViewInit(): void {
    timeout(7500).then(() => this.closeScripture());
  }

  closeScripture() {
    this.ngZone.runOutsideAngular(() => {
      const scriptureElement: HTMLDivElement = document.querySelector(
        ".scripture"
      );
      if (scriptureElement) {
        scriptureElement.classList.add("fade-out");
        timeout(2000).then(() =>
          scriptureElement?.parentElement?.removeChild(scriptureElement)
        );
      }
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  toggelOpenPanel($event, name: string) {
    if (this.openPanel === name) {
      this.openPanel = null;
    } else {
      this.openPanel = name;
      setTimeout(() => {
        $event.target.scrollIntoView({ behavior: "smooth" });
      }, 100);
    }
  }

  showShareButton() {
    return !!(navigator as any).share;
  }

  share() {
    (navigator as any)
      .share({
        title: "Let's Play FantasyGC.org",
        url: "https://fantasygc.org"
      })
      .catch(() => void 0);
  }
}
