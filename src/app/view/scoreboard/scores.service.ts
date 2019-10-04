import { Scores } from './scores';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class ScoresService {
  constructor(private readonly httpClient: HttpClient) {}

  get() {
    return this.httpClient.get<Scores>("/api/score").toPromise();
  }
}
