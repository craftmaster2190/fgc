package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Accessors(chain = true)
public class User implements UserDetails {

  @Transient
  private final boolean accountNonExpired = true;
  @Transient
  private final boolean accountNonLocked = true;
  @Transient
  private final boolean credentialsNonExpired = true;
  @Transient
  private final boolean enabled = true;
  @Id
  @GeneratedValue
  private Long id;
  @NotBlank
  @JsonDeserialize(using = StringTrimDeserializer.class)
  private String username;
  @NotBlank
  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;
  @JsonProperty(access = Access.READ_ONLY)
  private Boolean isAdmin;

  @Column(insertable = false, updatable = false)
  @JsonIgnore
  private Long familyId;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "familyId")
  @ToString.Exclude
  @Valid
  private Family family;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (Objects.equals(getIsAdmin(), Boolean.TRUE)) {
      return Set.of(new SimpleGrantedAuthority("ADMIN"));
    }
    return Set.of();
  }
}

