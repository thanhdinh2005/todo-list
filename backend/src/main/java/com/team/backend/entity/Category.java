package com.team.backend.entity;

import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.regex.Pattern;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

  private static final Pattern HEX_COLOR = Pattern.compile("^#[0-9A-Fa-f]{6}$");

  @Column(nullable = false, length = 100)
  private String name;

  @Column(name = "color_code", nullable = false, length = 7)
  private String colorCode;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false, updatable = false)
  private User owner;

  private Category(String name, String colorCode, User owner) {
    this.name = validateName(name);
    this.colorCode = validateColorCode(colorCode);
    this.owner = owner;
  }

  public static Category create(String name, String colorCode, User owner) {
    if (owner == null)
      throw new AppException(
        ErrorCode.BAD_REQUEST, "Category must be belong to at least one user"
      );

    return new Category(name, colorCode, owner);
  }

  public void rename(String newName) {
    if (this.name.equalsIgnoreCase(newName))
      return;
    this.name = validateName(newName);
  }

  public void changeColor(String newColorCode) {
    this.colorCode = validateColorCode(newColorCode);
  }

  public boolean isOwnedBy(UUID userId) {
    return this.owner.getId().equals(userId);
  }

  private static String validateName(String name) {
    if (name == null || name.isBlank())
      throw new AppException(
        ErrorCode.BAD_REQUEST, "Category name is required"
      );

    if (name.length() > 100) {
      throw new AppException(
        ErrorCode.BAD_REQUEST, "Category name cannot exceed 100 characters"
      );
    }
    return name.trim();
  }

  private static String validateColorCode(String colorCode) {
    if (colorCode == null || !HEX_COLOR.matcher(colorCode).matches()) {
      throw new AppException(
        ErrorCode.BAD_REQUEST, "Invalid Color code"
      );
    }
    return colorCode;
  }
}
