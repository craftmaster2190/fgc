import { User } from "./user";

export interface UserGroup {
  familyName: string;
  users: Array<User>;
}
