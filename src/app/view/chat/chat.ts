import { User } from "../auth/user";

export interface Chat {
  message: string;
  user: User;
}
