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
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { customRxStompConfig } from "./config/custom-rx-stomp.config";
import { AnswersService } from "./model/answers/answers.service";
import { AnswerBusService } from "./model/messaging/answer-bus.service";
import { MessageBusService } from "./model/messaging/message-bus.service";
import { QuestionComponent } from "./model/question/question.component";
import { SectionComponent } from "./model/section/section.component";
import { ChatBusService } from "./view/chat/chat-bus.service";
import { ChatComponent } from "./view/chat/chat.component";
import { GameComponent } from "./view/game/game.component";
import { NavHeaderComponent } from "./view/nav-header/nav-header.component";
import { FamilySearchService } from "./view/register/family-search.service";
import { ScoreboardComponent } from "./view/scoreboard/scoreboard.component";
import { ScoresService } from "./view/scoreboard/scores.service";
import { ToastService } from "./view/util/toast/toast.service";
import { ValidTextComponent } from "./view/util/valid-text/valid-text.component";
import { WelcomeComponent } from "./welcome/welcome.component";

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
    WelcomeComponent
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
