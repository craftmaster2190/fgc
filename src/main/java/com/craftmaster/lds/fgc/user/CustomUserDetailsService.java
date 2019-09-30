package com.craftmaster.lds.fgc.user;

import com.craftmaster.lds.fgc.config.AccessDeniedExceptionFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final AccessDeniedExceptionFactory accessDeniedExceptionFactory;

  @Override
  public User loadUserByUsername(String username) throws UsernameNotFoundException {
    return Optional.ofNullable(username)
      .map(String::trim)
      .filter(StringUtils::hasText)
      .flatMap(userRepository::findByUsernameIgnoreCase)
      .orElseThrow(accessDeniedExceptionFactory::get);
  }
}
