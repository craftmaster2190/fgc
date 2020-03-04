import { GameComponent } from "./game/game.component";
import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { WelcomeComponent } from "./welcome/welcome.component";
import { KnownGuard } from "./auth/known.guard";
import { LogoutComponent } from "./logout/logout.component";

const routes: Routes = [
  { path: "game", component: GameComponent, canActivate: [KnownGuard] },
  { path: "welcome", component: WelcomeComponent },
  { path: "logout", component: LogoutComponent, canActivate: [KnownGuard] },
  { path: "", redirectTo: "/welcome", pathMatch: "full" },
  { path: "**", redirectTo: "/welcome" }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
