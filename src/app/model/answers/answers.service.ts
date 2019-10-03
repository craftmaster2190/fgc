import { Injectable } from "@angular/core";

@Injectable()
export class AnswersService {
  constructor() {}

  firstPresidency = () => ["Russell M. Nelson"];

  quorumOfTheTwelve = () => [];

  conferenceSessions = () => [
    "Saturday Morning Session",
    "Saturday Afternoon Session",
    "General Women's Session",
    "Sunday Morning Session",
    "Sunday Afternoon Session"
  ]
}
