package com.team.backend.entity;

import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
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

  private User(String email, String hashPassword, String fullName) {
    this.email = email;
    this.hashPassword = hashPassword;
    this.fullName = fullName;
  }

  // ========== FACTORY METHOD ===========
  public static User register(
    String email,
    String rawPassword,
    String fullName,
    PasswordEncoder passwordEncoder
  ) {
    String cleanEmail = validateAndCleanEmail(email);
    validatePassword(rawPassword);

    return new User(cleanEmail, passwordEncoder.encode(rawPassword), fullName.trim());
  }

  public static User createByAdmin(
    String email,
    String rawPassword,
    String fullName,
    PasswordEncoder passwordEncoder
  ) {
    String cleanEmail = validateAndCleanEmail(email);
    validatePassword(rawPassword);

    return new User(cleanEmail, passwordEncoder.encode(rawPassword), fullName.trim());
  }

  // ========= BEHAVIOR METHODS ===========
  public void assignRole(Role role) {
    if (role != null) {
      this.roles.add(role);
    }
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

  // ========= PRIVATE HELPERS ===========
  private static String validateAndCleanEmail(String email) {
    if (email == null || !email.contains("@") || email.isBlank()) {
      throw new AppException(ErrorCode.BAD_REQUEST, "Invalid email format");
    }
    return email.trim().toLowerCase();
  }

  private static void validatePassword(String rawPassword) {
    if (rawPassword == null || rawPassword.isBlank() || rawPassword.length() < 6) {
      throw new AppException(ErrorCode.BAD_REQUEST, "Password must be at least 6 characters");
    }
  }
}
