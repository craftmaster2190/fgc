import { Scores } from "./scores";
import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable()
export class ScoresService {
  constructor(private readonly httpClient: HttpClient) {}

  get() {
    return this.httpClient.get<Scores>("/api/score").toPromise();
  }
  getUserCount() {
    return this.httpClient.get<number>("/api/user/count").toPromise();
  }
  getUsernames() {
    return this.httpClient.get<Array<string>>("/api/user/all").toPromise();
  }
}
