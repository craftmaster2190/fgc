import { Question } from './question';

export interface Answer {
  questionId: number;
  question?: Question;
  values: Array<string>;
}
