package com.craftmaster.lds.fgc.config;

import static com.craftmaster.lds.fgc.config.Predicates.not;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

import com.craftmaster.lds.fgc.db.TransactionalContext;
import com.craftmaster.lds.fgc.user.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

  private final TransactionalContext transactionalContext;
  private final DeviceRepository deviceRepository;
  private final DeviceInfoRepository deviceInfoRepository;
  private final UserRepository userRepository;
  private final DeviceService deviceService;

  public Authentication updateSession(User savedUser, HttpSession session) {
    return updateSession(savedUser, session, SessionDeviceId.get(session));
  }

  public Authentication updateSession(User user, HttpSession session, UUID deviceId) {
    UsernamePasswordAuthenticationToken authReq =
        new UsernamePasswordAuthenticationToken(user, deviceId, user.getAuthorities());
    Authentication auth = authenticate(authReq);

    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(auth);
    SessionDeviceId.set(session, deviceId);
    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
    return auth;
  }

  public Authentication authenticate(
      User user, HttpSession session, HttpServletRequest request, UUID deviceId) {
    Authentication auth = updateSession(user, session, deviceId);

    SessionDeviceId.set(session, deviceId);
    transactionalContext.run(
        () -> {
          var device = deviceService.getOrCreate(deviceId);

          String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
          String forwardedFor = request.getHeader(AwsHeaders.X_FORWARDED_FOR);
          String fingerprint = SessionFingerprint.get(session);

          Optional.of(
                  deviceInfoRepository.findByDeviceIdAndUserAgentAndInetAddressAndFingerprint(
                      deviceId, userAgent, forwardedFor, fingerprint))
              .filter(not(List::isEmpty))
              .map(list -> list.get(0))
              .orElseGet(
                  () ->
                      deviceInfoRepository.save(
                          new DeviceInfo()
                              .setFingerprint(fingerprint)
                              .setUserAgent(userAgent)
                              .setDeviceId(deviceId)
                              .setInetAddress(forwardedFor)))
              .setLastLogIn(Instant.now());

          deviceService.addDevice(user, device);

          userRepository.save(user);
        });
    return auth;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    return new UsernamePasswordAuthenticationToken(
        authentication.getPrincipal(),
        authentication.getCredentials(),
        ((User) authentication.getPrincipal()).getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
