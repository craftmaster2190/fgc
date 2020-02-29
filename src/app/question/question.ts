export interface Question {
  id: number;
  correctAnswers?: Array<string>;
  enabled: boolean;
  pointValue?: number;
}
