import { InjectableRxStompConfig } from "@stomp/ng2-stompjs";

export const customRxStompConfig: InjectableRxStompConfig = {
  // Which server?
  brokerURL:
    `${location.protocol.indexOf("https") > -1 ? "wss" : "ws"}` +
    `://${location.hostname}:${location.port}/game-ws`,

  // Headers
  // Typical keys: login, passcode, host
  connectHeaders: {},

  // How often to heartbeat?
  // Interval in milliseconds, set to 0 to disable
  heartbeatIncoming: 0, // Typical value 0 - disabled
  heartbeatOutgoing: 20000, // Typical value 20000 - every 20 seconds

  // Wait in milliseconds before attempting auto reconnect
  // Set to 0 to disable
  // Typical value 500 (500 milli seconds)
  reconnectDelay: 200,

  beforeConnect: () => {
    return Promise.resolve();
  }
};
