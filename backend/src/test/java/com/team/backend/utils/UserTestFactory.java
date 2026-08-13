package com.team.backend.utils;

import com.team.backend.entity.Role;
import com.team.backend.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class UserTestFactory {

  private UserTestFactory() {}

  public static User create(UUID id, String email, String fullName, boolean enabled, Role... roles) {
    try {
      Constructor<User> constructor = User.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      User user = constructor.newInstance();

      ReflectionTestUtils.setField(user, "id", id);
      ReflectionTestUtils.setField(user, "email", email);
      ReflectionTestUtils.setField(user, "hashPassword", "encoded-password");
      ReflectionTestUtils.setField(user, "fullName", fullName);
      ReflectionTestUtils.setField(user, "enabled", enabled);

      if (roles.length > 0) {
        Set<Role> roleSet = new HashSet<>(Arrays.asList(roles));
        ReflectionTestUtils.setField(user, "roles", roleSet);
      }

      return user;
    } catch (Exception e) {
      throw new RuntimeException("Failed to build test User via reflection", e);
    }
  }

  public static User createDefault(Role... roles) {
    return create(UUID.randomUUID(), "test@test.com", "Test User", true, roles);
  }
}
