import { User } from "../auth/user";
import { Time } from "./time";

export interface Chat {
  value: string;
  user: User;
  id: Time;
}
