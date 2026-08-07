package com.team.backend.entity;

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
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String token;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private Instant expiryDate;

  @Column(nullable = false)
  private boolean revoked = false;

  private RefreshToken(String token, User user, Instant expiryDate) {
    this.token = token;
    this.user = user;
    this.expiryDate = expiryDate;
  }

  //========== FACTORY METHOD ===========
  public static RefreshToken issueFor(User user, String token, Instant expiryDate) {
    return new RefreshToken(token, user, expiryDate);
  }

  //========= BEHAVIOR METHOD ===========

  public void revoke() {
    this.revoked = true;
  }

  public boolean isExpired() {
    return Instant.now().isAfter(expiryDate);
  }

  public boolean isValid() {
    return !revoked && !isExpired();
  }
}
