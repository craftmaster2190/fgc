import { map } from "rxjs/operators";
import { IMessage } from "@stomp/stompjs";

export function mapMessageTo<T>() {
  return map((message: IMessage) => {
    const parsed = JSON.parse(message.body) as T | Array<T>;
    return Array.isArray(parsed) ? parsed : [parsed];
  });
}
