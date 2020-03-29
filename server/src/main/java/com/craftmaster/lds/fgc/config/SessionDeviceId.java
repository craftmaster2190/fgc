package com.craftmaster.lds.fgc.config;

import java.util.UUID;
import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;

public enum SessionDeviceId {
  ;

  private static final String DEVICE_ID = "DEVICE_ID";

  @Nullable
  public static UUID get(HttpSession httpSession) {
    return (UUID) httpSession.getAttribute(DEVICE_ID);
  }

  public static void set(HttpSession httpSession, UUID deviceId) {
    httpSession.setAttribute(DEVICE_ID, deviceId);
  }
}
