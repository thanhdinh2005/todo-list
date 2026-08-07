package com.team.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Đại diện cho 1 sự kiện đã xảy ra — không extend BaseEntity vì tự quản lý
 * thời điểm ghi nhận (performedAt) và không cần updatedAt (bất biến sau khi tạo).
 */
@Entity
@Table(name = "audit_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

  @Id
  @UuidGenerator
  private UUID id;

  @Column(nullable = false)
  private String entityName;

  @Column(nullable = false)
  private String entityId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Action action;

  private String performedBy;

  @Column(nullable = false)
  private Instant performedAt;

  @Column(columnDefinition = "TEXT")
  private String oldValue;

  @Column(columnDefinition = "TEXT")
  private String newValue;

  private AuditLog(
    String entityName,
    String entityId,
    Action action,
    String performedBy,
    String oldValue,
    String newValue
  ) {
    this.entityName = entityName;
    this.entityId = entityId;
    this.action = action;
    this.performedBy = performedBy;
    this.performedAt = Instant.now();
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  //========== FACTORY METHOD ===========
  public static AuditLog recordCreate(String entityName, String entityId, String performedBy, String newValue) {
    return new AuditLog(entityName, entityId, Action.CREATE, performedBy, null, newValue);
  }

  public static AuditLog recordUpdate(String entityName, String entityId, String performedBy, String oldValue, String newValue) {
    return new AuditLog(entityName, entityId, Action.UPDATE, performedBy, oldValue, newValue);
  }

  public static AuditLog recordDelete(String entityName, String entityId, String performedBy, String oldValue) {
    return new AuditLog(entityName, entityId, Action.DELETE, performedBy, oldValue, null);
  }

  public enum Action {
    CREATE, UPDATE, DELETE
  }
}
