package com.craftmaster.lds.fgc.user;

import com.craftmaster.lds.fgc.config.AccessDeniedExceptionFactory;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final AccessDeniedExceptionFactory accessDeniedExceptionFactory;

  @Override
  public User loadUserByUsername(String uuid) throws UsernameNotFoundException {
    return Optional.ofNullable(uuid)
        .map(UUID::fromString)
        .flatMap(userRepository::findById)
        .orElseThrow(accessDeniedExceptionFactory::get);
  }
}
