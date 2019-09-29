import { AnswerBusService } from './model/messaging/answer-bus.service';
import { AnswersService } from './model/answers/answers.service';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { customRxStompConfig } from './config/custom-rx-stomp.config';
import { GameComponent } from './view/game/game.component';
import { LoginComponent } from './view/login/login.component';
import { QuestionComponent } from './model/question/question.component';
import { SectionComponent } from './model/section/section.component';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from '@stomp/ng2-stompjs';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgModule } from '@angular/core';

@NgModule({
  declarations: [
    AppComponent,
    QuestionComponent,
    GameComponent,
    LoginComponent,
    SectionComponent
  ],
  imports: [BrowserModule, AppRoutingModule, FormsModule, NgbModule],
  providers: [
    AnswersService,
    AnswerBusService,
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
