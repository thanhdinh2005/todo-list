package com.team.backend.service;

import com.team.backend.entity.User;
import com.team.backend.repository.UserRepository;
import com.team.backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

    Set<GrantedAuthority> authorities = user.getRoles().stream()
      .flatMap(role -> role.getPermissions().stream())
      .map(permission -> new SimpleGrantedAuthority(permission.getName()))
      .collect(Collectors.toSet());

    return new CustomUserDetails(user, authorities);
  }
}
