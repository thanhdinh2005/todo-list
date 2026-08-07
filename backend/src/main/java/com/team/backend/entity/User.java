package com.team.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String hashPassword;

  @Column(nullable = false)
  private String fullName;

  @Column(nullable = false)
  private boolean enabled = true;

  @Getter(AccessLevel.NONE)
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private Set<Role> roles = new HashSet<>();

  private User(
    String email,
    String hashPassword,
    String fullName
  ) {
    this.email = email;
    this.hashPassword = hashPassword;
    this.fullName = fullName;
  }

  //========== FACTORY METHOD ===========
  public static User create(
    String email,
    String rawPassword,
    String fullName,
    PasswordEncoder passwordEncoder
  ) {
    validateEmail(email);
    return new User(email, passwordEncoder.encode(rawPassword), fullName);
  }

  //========= BEHAVIOR METHOD ===========

  public void changePassword(String rawPassword, PasswordEncoder passwordEncoder) {
    this.hashPassword = passwordEncoder.encode(rawPassword);
  }

  public void changeFullName(String fullName) {
    if (fullName == null || fullName.isBlank()) {
      throw new IllegalArgumentException("Full name must not be blank");
    }
    this.fullName = fullName;
  }

  public void enable() {
    this.enabled = true;
  }

  public void disable() {
    this.enabled = false;
  }

  public void assignRole(Role role) {
    this.roles.add(role);
  }

  public void removeRole(Role role) {
    this.roles.remove(role);
  }

  public boolean hasRole(String roleName) {
    return roles.stream().anyMatch(r -> r.getName().equals(roleName));
  }

  public Set<Role> getRoles() {
    return Collections.unmodifiableSet(roles);
  }

  private static void validateEmail(String email) {
    if (email == null || !email.contains("@")) {
      throw new IllegalArgumentException("Invalid email: " + email);
    }
  }
}
