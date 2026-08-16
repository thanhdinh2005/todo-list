package com.team.backend.security;

import com.team.backend.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {
  private final User user;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
    this.user = user;
    this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() { return user.getHashPassword(); }

  @Override
  public String getUsername() { return user.getEmail(); }

  @Override
  public boolean isEnabled() { return user.isEnabled(); }

  public UUID getId() { return user.getId(); }
}
