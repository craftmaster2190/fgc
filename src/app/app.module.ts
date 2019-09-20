import { AnswersService } from './model/answers/answers.service';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { GameComponent } from './view/game/game.component';
import { LoginComponent } from './view/login/login.component';
import { QuestionComponent } from './model/question/question.component';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgModule } from '@angular/core';
import { SectionComponent } from './model/section/section.component';
import { SectionCardComponent } from './model/section-card/section-card.component';

@NgModule({
  declarations: [
    AppComponent,
    QuestionComponent,
    GameComponent,
    LoginComponent,
    SectionComponent,
    SectionCardComponent
  ],
  imports: [BrowserModule, AppRoutingModule, FormsModule, NgbModule],
  providers: [AnswersService],
  bootstrap: [AppComponent]
})
export class AppModule {}
