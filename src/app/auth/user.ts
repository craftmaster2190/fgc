import { Family } from "../family/family";

export interface User {
  id: string;
  name?: string;
  family?: Family;
  isAdmin?: boolean;
}
