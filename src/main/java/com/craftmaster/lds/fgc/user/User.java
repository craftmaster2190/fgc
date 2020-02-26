package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "appuser")
public class User implements UserDetails {
  private static final long serialVersionUID = 20200226L;

  @Transient
  @JsonIgnore
  private final boolean accountNonExpired = true;
  @Transient
  @JsonIgnore
  private final boolean accountNonLocked = true;
  @Transient
  @JsonIgnore
  private final boolean credentialsNonExpired = true;
  @Transient
  @JsonIgnore
  private final boolean enabled = true;
  @Transient
  @JsonIgnore
  private final String password = null;
  @Id
  @GeneratedValue
  @JsonIgnore
  private UUID id;
  private String name;
  @JsonInclude(Include.NON_NULL)
  @JsonProperty(access = Access.READ_ONLY)
  private Boolean isAdmin;
  @Column(insertable = false, updatable = false)
  @JsonIgnore
  private Long familyId;
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "id", referencedColumnName = "userId")
  @ToString.Exclude
  @JsonIgnore
  private Set<Device> devices;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "familyId")
  @ToString.Exclude
  private Family family;

  @Transient
  @JsonIgnore
  @Override
  public String getUsername() {
    return getId().toString();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (Objects.equals(getIsAdmin(), Boolean.TRUE)) {
      return Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    return Set.of();
  }
}

