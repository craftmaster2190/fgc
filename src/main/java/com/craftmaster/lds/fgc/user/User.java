package com.craftmaster.lds.fgc.user;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Accessors(chain = true)
public class User implements UserDetails , Serializable {

  @Transient
  private final Collection<? extends GrantedAuthority> authorities = Set.of();

  @Transient private final boolean accountNonExpired = true;

  @Transient private final boolean accountNonLocked = true;

  @Transient  private final boolean credentialsNonExpired = true;

  @Transient private final boolean enabled = true;

  @Id
  @NotNull
  private Long id;
  @NotNull
  private String username;
  private String password;


}
