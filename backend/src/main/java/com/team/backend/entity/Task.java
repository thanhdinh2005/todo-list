package com.team.backend.entity;

import com.team.backend.exception.AppException;
import com.team.backend.exception.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends BaseEntity {

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private boolean completed;

  @Column(name = "due_date")
  private Instant dueDate;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false, updatable = false)
  private User owner;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  private Task(String title, String description, Instant dueDate,
               User owner, Category category) {
    this.title = validateTitle(title);
    this.description = description;
    this.dueDate = dueDate;
    this.owner = owner;
    this.category = category;
    this.completed = false;
  }

  public static Task create(String title, String description, Instant dueDate,
                            User owner, Category category) {
    if (owner == null) {
      throw new IllegalArgumentException("Task phải thuộc về 1 owner");
    }
    validateCategoryOwnership(owner, category);
    return new Task(title, description, dueDate, owner, category);
  }

  public void editDetails(String title, String description, Instant dueDate, Category category) {
    validateCategoryOwnership(this.owner, category);
    this.title = validateTitle(title);
    this.description = description;
    this.dueDate = dueDate;
    this.category = category;
  }

  public void complete() {
    this.completed = true;
  }

  public void reopen() {
    this.completed = false;
  }

  public boolean isOwnedBy(User user) {
    return this.owner.getId().equals(user.getId());
  }

  public boolean isOverdue() {
    return !completed && dueDate != null && dueDate.isBefore(Instant.now());
  }

  private static String validateTitle(String title) {
    if (title == null || title.isBlank())
      throw new AppException(
        ErrorCode.BAD_REQUEST, "Title is required"
      );
    if (title.length() > 255)
      throw new AppException(
        ErrorCode.BAD_REQUEST, "Title cannot exceed 255 characters"
      );
    return title.trim();
  }

  private static void validateCategoryOwnership(User owner, Category category) {
    if (category != null && category.isOwnedBy(owner.getId())) {
      throw new AppException(
        ErrorCode.BAD_REQUEST, "Cannot assign someone else's category"
      );
    }
  }
}
