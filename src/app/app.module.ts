import { AnswerBusService } from './model/messaging/answer-bus.service';
import { AnswersService } from './model/answers/answers.service';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AuthGuard } from './view/auth/auth-guard.service';
import { AuthInterceptor } from './view/auth/auth-interceptor.service';
import { AuthService } from './view/auth/auth.service';
import { ChatBusService } from './view/chat/chat-bus.service';
import { ChatComponent } from './view/chat/chat.component';
import { customRxStompConfig } from './config/custom-rx-stomp.config';
import { FamilySearchService } from './view/register/family-search.service';
import { GameComponent } from './view/game/game.component';
import { LoginComponent } from './view/login/login.component';
import { MessageBusService } from './model/messaging/message-bus.service';
import { NavHeaderComponent } from './view/nav-header/nav-header.component';
import { NotAuthGuard } from './view/auth/not-auth-guard.service';
import { QuestionComponent } from './model/question/question.component';
import { RegisterComponent } from './view/register/register.component';
import { SectionComponent } from './model/section/section.component';
import { ToastService } from './view/util/toast/toast.service';
import { ValidTextComponent } from './view/util/valid-text/valid-text.component';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from '@stomp/ng2-stompjs';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgModule } from '@angular/core';

@NgModule({
  declarations: [
    AppComponent,
    QuestionComponent,
    GameComponent,
    LoginComponent,
    SectionComponent,
    RegisterComponent,
    NavHeaderComponent,
    ValidTextComponent,
    ChatComponent
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
    AuthService,
    AuthGuard,
    NotAuthGuard,
    AuthInterceptor,
    ChatBusService,
    FamilySearchService,
    MessageBusService,
    ToastService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
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
