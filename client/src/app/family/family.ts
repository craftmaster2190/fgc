import { User } from "../auth/user";

export interface Family {
  id: string;
  name: string;
  isFamily?: boolean;
  users?: Array<User>;
}
