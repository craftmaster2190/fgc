import { Time } from "../chat/time";
import { User } from "../auth/user";

export default interface DeviceInfo {
  createdAt: Time;
  fingerprint: string;
  browserFingerprint: string;
  userAgent: string;
  inetAddress: string;
  lastLogIn: Time;
}
