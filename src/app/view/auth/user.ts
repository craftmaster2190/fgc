import { Family } from "./family";

export interface User {
  id: string;
  name?: string;
  family?: Family;
  isAdmin?: boolean;
}
