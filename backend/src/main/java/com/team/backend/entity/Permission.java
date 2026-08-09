package com.team.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permission extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;

  private String description;

  private Permission(String name, String description) {
    this.name = name;
    this.description = description;
  }

  //========== FACTORY METHOD ===========
  public static Permission create(String name, String description) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Permission name must not be blank");
    }
    return new Permission(name, description);
  }
}
