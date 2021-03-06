package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Formula;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "appuser")
public class User implements UserDetails {
  private static final long serialVersionUID = 20200226L;

  @Transient @JsonIgnore private final boolean accountNonExpired = true;
  @Transient @JsonIgnore private final boolean credentialsNonExpired = true;
  @Transient @JsonIgnore private final boolean enabled = true;
  @Transient @JsonIgnore private final String password = null;
  @Id private UUID id = UUID.randomUUID();

  @Column(columnDefinition = "text")
  private String name;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty(access = Access.READ_ONLY)
  private Boolean isAdmin;

  @Column(insertable = false, updatable = false)
  @JsonIgnore
  private UUID familyId;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable
  @JsonIgnore
  @EqualsAndHashCode.Exclude
  private Set<Device> devices;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "familyId")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Family family;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id")
  private UserProfile userProfile;

  @EqualsAndHashCode.Exclude
  @Formula("(CASE WHEN profile_image IS NULL THEN 'FALSE' ELSE 'TRUE' END)")
  private boolean hasProfileImage;

  @Transient
  @JsonIgnore
  @Override
  public String getUsername() {
    return getId().toString();
  }

  @ToString.Include
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (Objects.equals(getIsAdmin(), Boolean.TRUE)) {
      return Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    return Set.of();
  }

  @ToString.Exclude @JsonIgnore @EqualsAndHashCode.Exclude
  private Instant createdAt = Instant.now();

  @Transient
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return getDevices().stream().map(Device::getBannedAt).noneMatch(Objects::nonNull);
  }
}
