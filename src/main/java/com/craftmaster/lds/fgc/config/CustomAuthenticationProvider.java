package com.craftmaster.lds.fgc.config;

import com.craftmaster.lds.fgc.user.Device;
import com.craftmaster.lds.fgc.user.DeviceRepository;
import com.craftmaster.lds.fgc.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

  private final TransactionalContext transactionalContext;
  private final DeviceRepository deviceRepository;

  public Authentication authenticate(User user, HttpSession session, UUID deviceId) {
    UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(user, deviceId, Set.of());
    Authentication auth = authenticate(authReq);

    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(auth);
    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
    session.setAttribute("DEVICE_ID", deviceId);
    transactionalContext.run(() -> {
      Device device = deviceRepository
        .findById(deviceId)
        .orElseGet(() ->
          deviceRepository.save(new Device().setId(deviceId)));
      Optional.ofNullable(device
        .getUsers())
        .orElseGet(HashSet::new)
        .add(user);
    });
    return auth;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), Set.of());
  }


  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
