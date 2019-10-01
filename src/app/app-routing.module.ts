import { AuthGuard } from './view/auth/auth-guard.service';
import { GameComponent } from './view/game/game.component';
import { LoginComponent } from './view/login/login.component';
import { NotAuthGuard } from './view/auth/not-auth-guard.service';
import { RegisterComponent } from './view/register/register.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: "login", component: LoginComponent, canActivate: [NotAuthGuard] },
  {
    path: "login/register",
    component: RegisterComponent,
    canActivate: [NotAuthGuard]
  },
  { path: "game", component: GameComponent, canActivate: [AuthGuard] },
  { path: "", redirectTo: "/game", pathMatch: "full" },
  { path: "**", redirectTo: "/game" }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
