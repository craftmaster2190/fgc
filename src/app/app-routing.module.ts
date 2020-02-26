import { GameComponent } from "./view/game/game.component";
import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";

const routes: Routes = [
  { path: "game", component: GameComponent },
  { path: "", redirectTo: "/game", pathMatch: "full" },
  { path: "**", redirectTo: "/game" }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
