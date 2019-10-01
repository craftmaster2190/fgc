package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Accessors(chain = true)
public class User implements UserDetails {
	private static final long serialVersionUID = 20190930L;

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
	@NotNull
	private Long id;
	@NotBlank
	@JsonDeserialize(using = StringTrimDeserializer.class)
	private String username;
	@NotBlank
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	@JsonProperty(access = Access.READ_ONLY)
	private Boolean isAdmin;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (Objects.equals(getIsAdmin(), Boolean.TRUE)) {
			return Set.of(new SimpleGrantedAuthority("ADMIN"));
		}
		return Set.of();
	}
}
