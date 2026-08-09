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

import java.time.Instant;

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
  private boolean completed = false;

  private Instant dueDate;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private Task(String title, String description, Instant dueDate, User user) {
    this.title = title;
    this.description = description;
    this.dueDate = dueDate;
    this.user = user;
  }

  //========== FACTORY METHOD ===========
  public static Task create(String title, String description, Instant dueDate, User user) {
    if (title == null || title.isBlank()) {
      throw new AppException(ErrorCode.BAD_REQUEST, "Task title must not be blank");
    }
    if (user == null) {
      throw new AppException(ErrorCode.BAD_REQUEST, "User must not be null");
    }
    return new Task(title, description, dueDate, user);
  }

  //========= BEHAVIOR METHOD ===========

  public void markAsCompleted() {
    this.completed = true;
  }

  public void markAsIncomplete() {
    this.completed = false;
  }

  public void updateDetails(String title, String description, Instant dueDate) {
    if (title == null || title.isBlank()) {
      throw new AppException(ErrorCode.BAD_REQUEST, "Task title must not be blank");
    }
    this.title = title;
    this.description = description;
    this.dueDate = dueDate;
  }
}
