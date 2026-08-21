package com.team.backend.utils;

import com.team.backend.entity.RefreshToken;
import com.team.backend.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

public final class RefreshTokenTestFactory {
  private RefreshTokenTestFactory() {}

  public static RefreshToken create(UUID id, User user, Instant expiryDate) {
    RefreshToken token = RefreshToken.issueFor(user, expiryDate);
    ReflectionTestUtils.setField(token, "id", id);
    return token;
  }

  public static RefreshToken createValid(User user) {
    return create(UUID.randomUUID(), user, Instant.now().plusSeconds(3600));
  }

  public static RefreshToken createExpired(User user) {
    return create(UUID.randomUUID(), user, Instant.now().minusSeconds(3600));
  }

  public static RefreshToken createRevoked(User user) {
    RefreshToken token = createValid(user);
    token.revoke();
    return token;
  }
}
