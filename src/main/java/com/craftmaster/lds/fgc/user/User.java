package com.craftmaster.lds.fgc.user;

import java.util.Collection;
import java.util.Set;
import javax.persistence.Entity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Accessors(chain = true)
public class User implements UserDetails {

  private final Collection<? extends GrantedAuthority> authorities = Set.of();
  private final boolean accountNonExpired = true;
  private final boolean accountNonLocked = true;
  private final boolean credentialsNonExpired = true;
  private final boolean enabled = true;
  private String username;
  private String password;

}
