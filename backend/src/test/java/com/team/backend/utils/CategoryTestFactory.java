package com.team.backend.utils;

import com.team.backend.entity.Category;
import com.team.backend.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public final class CategoryTestFactory {
  private CategoryTestFactory() {}

  public static Category create(UUID id, String name, String colorCode, User owner) {
    Category category = Category.create(name, colorCode, owner);
    ReflectionTestUtils.setField(category, "id", id);
    return category;
  }

  public static Category createDefault(User owner) {
    return create(UUID.randomUUID(), "Work", "#FF5733", owner);
  }
}
