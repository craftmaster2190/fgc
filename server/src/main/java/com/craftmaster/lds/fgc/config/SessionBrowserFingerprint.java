package com.craftmaster.lds.fgc.config;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;

public enum SessionBrowserFingerprint {
  ;

  public static final String BROWSER_FINGERPRINT = "X_BROWSER_ID_FINGERPRINT";

  @Nullable
  public static String get(HttpSession httpSession) {
    return (String) httpSession.getAttribute(BROWSER_FINGERPRINT);
  }

  public static void set(HttpSession httpSession, String fingerprint) {
    httpSession.setAttribute(BROWSER_FINGERPRINT, fingerprint);
  }
}
