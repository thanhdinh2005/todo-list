package com.team.backend.entity;

import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String colorCode;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private Category(String name, String colorCode, User user) {
    this.name = name;
    this.colorCode = colorCode;
    this.user = user;
  }

  //========== FACTORY METHOD ===========
  public static Category create(String name, String colorCode, User user) {
    if (name == null || name.isBlank()) {
      throw new AppException(ErrorCode.BAD_REQUEST, "Category name must not be blank");
    }
    if (user == null) {
      throw new AppException(ErrorCode.BAD_REQUEST, "User must not be null");
    }

    // Đặt màu mặc định nếu không truyền lên
    String defaultColor = (colorCode == null || colorCode.isBlank()) ? "#FFFFFF" : colorCode;

    return new Category(name, defaultColor, user);
  }

  //========= BEHAVIOR METHOD ===========

  public void updateCategory(String name, String colorCode) {
    if (name == null || name.isBlank()) {
      throw new AppException(ErrorCode.BAD_REQUEST, "Category name must not be blank");
    }
    this.name = name;
    if (colorCode != null && !colorCode.isBlank()) {
      this.colorCode = colorCode;
    }
  }
}
