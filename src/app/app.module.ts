import { HttpClientModule } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import {
  InjectableRxStompConfig,
  RxStompService,
  rxStompServiceFactory
} from "@stomp/ng2-stompjs";
import { AnswersService } from "./answers/answers.service";
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { ChatBusService } from "./chat/chat-bus.service";
import { ChatComponent } from "./chat/chat.component";
import { customRxStompConfig } from "./config/custom-rx-stomp.config";
import { FamilySearchService } from "./family/family-search.service";
import { GameComponent } from "./game/game.component";
import { AnswerBusService } from "./messaging/answer-bus.service";
import { MessageBusService } from "./messaging/message-bus.service";
import { NavHeaderComponent } from "./nav-header/nav-header.component";
import { QuestionComponent } from "./question/question.component";
import { ScoreboardComponent } from "./scoreboard/scoreboard.component";
import { ScoresService } from "./scoreboard/scores.service";
import { SectionComponent } from "./section/section.component";
import { ToastService } from "./toast/toast.service";
import { ValidTextComponent } from "./valid-text/valid-text.component";
import { WelcomeComponent } from "./welcome/welcome.component";
import { LogoutComponent } from "./logout/logout.component";
import { ImagesService } from "./answers/images.service";

@NgModule({
  declarations: [
    AppComponent,
    QuestionComponent,
    GameComponent,

    SectionComponent,

    NavHeaderComponent,
    ValidTextComponent,
    ChatComponent,
    ScoreboardComponent,
    WelcomeComponent,
    LogoutComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    NgbModule,
    HttpClientModule
  ],
  providers: [
    AnswersService,
    AnswerBusService,
    ChatBusService,
    FamilySearchService,
    MessageBusService,
    ScoresService,
    ImagesService,
    ToastService,
    {
      provide: InjectableRxStompConfig,
      useValue: customRxStompConfig
    },
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
      deps: [InjectableRxStompConfig]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
