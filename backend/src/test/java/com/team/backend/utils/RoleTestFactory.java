package com.team.backend.utils;

import com.team.backend.entity.Role;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.util.UUID;

public final class RoleTestFactory {

  private RoleTestFactory() {}

  public static Role create(UUID id, String name) {
    try {
      Constructor<Role> constructor = Role.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      Role role = constructor.newInstance();

      ReflectionTestUtils.setField(role, "id", id);
      ReflectionTestUtils.setField(role, "name", name);

      return role;
    } catch (Exception e) {
      throw new RuntimeException("Failed to build test Role via reflection", e);
    }
  }

  public static Role userRole() {
    return create(UUID.randomUUID(), "ROLE_USER");
  }
}
