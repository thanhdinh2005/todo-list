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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseEntity {

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  private String description;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "role_permissions",
    joinColumns = @JoinColumn(name = "role_id"),
    inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  private Set<Permission> permissions = new HashSet<>();

  private Role(String name, String description) {
    this.name = validateName(name);
    this.description = description;
  }

  public static Role create(String name, String description) {
    return new Role(name, description);
  }

  public void grantPermissions(Set<Permission> newPermissions) {
    this.permissions.addAll(newPermissions);
  }

  public void rename(String newName) {
    this.name = validateName(newName);
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  public void replacePermissions(Set<Permission> newPermissions) {
    this.permissions.clear();
    this.permissions.addAll(newPermissions);
  }

  private static String validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Role name must not be blank");
    }
    return name.trim();
  }
}
