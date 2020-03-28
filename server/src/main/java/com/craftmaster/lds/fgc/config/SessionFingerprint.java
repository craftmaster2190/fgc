package com.craftmaster.lds.fgc.config;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;

public enum SessionFingerprint {
  ;

  public static final String FINGERPRINT = "X_DEVICE_ID_FINGERPRINT";

  @Nullable
  public static String get(HttpSession httpSession) {
    return (String) httpSession.getAttribute(FINGERPRINT);
  }

  public static void set(HttpSession httpSession, String fingerprint) {
    httpSession.setAttribute(FINGERPRINT, fingerprint);
  }
}
