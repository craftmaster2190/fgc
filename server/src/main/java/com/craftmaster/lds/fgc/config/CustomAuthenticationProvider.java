package com.craftmaster.lds.fgc.config;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

import com.craftmaster.lds.fgc.db.TransactionalContext;
import com.craftmaster.lds.fgc.user.*;
import java.time.Instant;
import java.util.HashSet;
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

  public Authentication updateSession(User savedUser, HttpSession session) {
    return updateSession(savedUser, session, (UUID) session.getAttribute("DEVICE_ID"));
  }

  public Authentication updateSession(User user, HttpSession session, UUID deviceId) {
    UsernamePasswordAuthenticationToken authReq =
        new UsernamePasswordAuthenticationToken(user, deviceId, user.getAuthorities());
    Authentication auth = authenticate(authReq);

    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(auth);
    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
    return auth;
  }

  public Authentication authenticate(
      User user, HttpSession session, HttpServletRequest request, UUID deviceId) {
    Authentication auth = updateSession(user, session, deviceId);

    session.setAttribute("DEVICE_ID", deviceId);
    transactionalContext.run(
        () -> {
          var device =
              deviceRepository
                  .findById(deviceId)
                  .orElseGet(() -> deviceRepository.save(new Device().setId(deviceId)));

          String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
          String forwardedFor = request.getHeader("x-forwarded-for");

          deviceInfoRepository
              .findByDeviceIdAndUserAgentAndInetAddress(device.getId(), userAgent, forwardedFor)
              .orElseGet(
                  () ->
                      deviceInfoRepository.save(
                          new DeviceInfo()
                              .setUserAgent(userAgent)
                              .setDeviceId(device.getId())
                              .setInetAddress(forwardedFor)))
              .setLastLogIn(Instant.now());

          Optional.ofNullable(user.getDevices())
              .orElseGet(
                  () -> {
                    HashSet<Device> devices = new HashSet<>();
                    user.setDevices(devices);
                    return devices;
                  })
              .add(device);

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
