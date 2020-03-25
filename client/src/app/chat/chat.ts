import { User } from "../auth/user";
import { Time } from "./time";

export interface Chat {
  value: string;
  userId: string;
  id: Time;
  delete?: boolean;
}
