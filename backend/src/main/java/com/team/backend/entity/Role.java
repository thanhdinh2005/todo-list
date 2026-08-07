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

  @Column(nullable = false, unique = true)
  private String name;

  private String description;

  @Getter(AccessLevel.NONE)
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "role_permissions",
    joinColumns = @JoinColumn(name = "role_id"),
    inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  private Set<Permission> permissions = new HashSet<>();

  private Role(String name, String description) {
    this.name = name;
    this.description = description;
  }

  //========== FACTORY METHOD ===========
  public static Role create(String name, String description) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Role name must not be blank");
    }
    return new Role(name, description);
  }

  //========= BEHAVIOR METHOD ===========

  public void grantPermission(Permission permission) {
    this.permissions.add(permission);
  }

  public void revokePermission(Permission permission) {
    this.permissions.remove(permission);
  }

  public boolean hasPermission(String permissionName) {
    return permissions.stream().anyMatch(p -> p.getName().equals(permissionName));
  }

  public Set<Permission> getPermissions() {
    return Collections.unmodifiableSet(permissions);
  }
}
