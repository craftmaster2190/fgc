package com.craftmaster.lds.fgc.config;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;

public enum SessionDeviceFingerprint {
  ;

  public static final String DEVICE_FINGERPRINT = "X_DEVICE_ID_FINGERPRINT";

  @Nullable
  public static String get(HttpSession httpSession) {
    return (String) httpSession.getAttribute(DEVICE_FINGERPRINT);
  }

  public static void set(HttpSession httpSession, String fingerprint) {
    httpSession.setAttribute(DEVICE_FINGERPRINT, fingerprint);
  }
}
