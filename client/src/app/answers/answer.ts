import { Question } from "../question/question";

export interface Answer {
  questionId: number;
  question?: Question;
  values: Array<string>;
}
