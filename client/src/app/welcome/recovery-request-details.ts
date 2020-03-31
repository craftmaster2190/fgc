import { Time } from "../chat/time";
import { User } from "../auth/user";
import DeviceInfo from "./device-info";

export default interface RecoveryRequestDetails {
  createdAt: Time;
  userId: string;
  user: User;
  deviceId: string;
  deviceInfos: Array<DeviceInfo>;
  usersOtherDeviceInfos: Array<DeviceInfo>;
  userComment: string;
}
