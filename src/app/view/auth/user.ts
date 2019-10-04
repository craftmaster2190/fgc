import { Family } from './family';

export interface User {
  username: string;
  /**
   * Only used at register time
   */
  password?: string;
  family: Family;
  isAdmin?: boolean;
}
